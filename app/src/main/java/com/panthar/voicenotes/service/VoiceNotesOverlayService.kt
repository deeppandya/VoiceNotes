package com.panthar.voicenotes.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import com.panthar.voicenotes.R
import com.panthar.voicenotes.di.entrypoints.NoteUseCasesEntryPoint
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.util.startListeningLoop
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoiceNotesOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var speechRecognizer: SpeechRecognizer

    private lateinit var micIcon: ImageView
    private lateinit var micIconContainer: View

    private lateinit var closeTarget: ImageView

    private lateinit var micParams: WindowManager.LayoutParams
    private lateinit var closeParams: WindowManager.LayoutParams

    private var isListening:Boolean = false
    private var currentUtterance:String = ""
    private var recognizedText:String = ""

    private var isSave:Boolean = false

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var finalNote: String = ""

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        setUpMicButton()
        setUpCloseTarget()

        startForegroundNotification()
    }

    private fun setUpCloseTarget() {
        closeTarget = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            visibility = View.GONE
        }

        closeParams = WindowManager.LayoutParams(
            200,
            200,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = 100
        }

        windowManager.addView(closeTarget, closeParams)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpMicButton() {
        micIconContainer = LayoutInflater.from(this)
            .inflate(R.layout.overlay_layout, null, false) as FrameLayout
        micIcon = micIconContainer.findViewById<ImageView>(R.id.micIcon)
        micIconContainer.setOnClickListener {
            if (!isListening) {
                if (isSave) {
                    isSave = false
                    micIcon.setImageResource(R.drawable.ic_baseline_mic_24)
                    micIconContainer.setBackgroundResource(R.drawable.mic_button_bg)
                    val saveNoteUseCase = EntryPointAccessors.fromApplication(
                        applicationContext,
                        NoteUseCasesEntryPoint::class.java
                    ).saveNoteUseCase()

                    serviceScope.launch {
                        val newNote = Note(
                            title = getString(R.string.quick_hello),
                            content = finalNote,
                            timestamp = System.currentTimeMillis()
                        )
                        saveNoteUseCase.invoke(newNote)
                        finalNote = ""
                    }
                } else {
                    startSpeechRecognition()
                }
            } else {
                stopSpeechRecognition()
            }
        }

        micIconContainer.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0
            var initialY = 0
            var touchX = 0f
            var touchY = 0f
            var moved = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showCloseTarget()
                        moved = false
                        initialX = micParams.x
                        initialY = micParams.y
                        touchX = event.rawX
                        touchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - touchX).toInt()
                        val dy = (event.rawY - touchY).toInt()
                        micParams.x = initialX + dx
                        micParams.y = initialY + dy
                        windowManager.updateViewLayout(micIconContainer, micParams)
                        moved = true
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        hideCloseTarget()
                        if (moved && isOverlapping(micIconContainer, closeTarget)) {
                            stopSelf()
                            return moved
                        } else {
                            v.performClick()
                            return false
                        }
                    }
                }
                return false
            }
        })

        micParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }

        windowManager.addView(micIconContainer, micParams)
    }

    private fun startSpeechRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        startListeningLoop(
            speechRecognizer,
            onPartial = { currentUtterance = it },
            onFinal = {
                recognizedText = "$recognizedText $it"
                currentUtterance = ""
            },
            shouldContinue = { true })
        isListening = true
        recognizedText = ""
        micIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        micIconContainer.setBackgroundResource(R.drawable.clear_button_bg)
    }

    private fun stopSpeechRecognition() {
        Log.e("SpeechRecord", "$recognizedText $currentUtterance")
        finalNote = "$recognizedText $currentUtterance"
        if (finalNote.isNotEmpty()) {
            micIcon.setImageResource(R.drawable.ic_outline_check_24)
            micIconContainer.setBackgroundResource(R.drawable.save_button_bg)
            isSave = true
        } else {
            micIcon.setImageResource(R.drawable.ic_baseline_mic_24)
            micIconContainer.setBackgroundResource(R.drawable.mic_button_bg)
        }
        if (this::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
        }
        isListening = false
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        try {
            windowManager.removeView(micIconContainer)
            windowManager.removeView(closeTarget)
        } catch (_: Exception) {
        }
        if (this::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
    }

    private fun showCloseTarget() {
        closeTarget.visibility = View.VISIBLE
        windowManager.updateViewLayout(closeTarget, closeParams)
    }

    private fun hideCloseTarget() {
        closeTarget.visibility = View.GONE
        windowManager.updateViewLayout(closeTarget, closeParams)
    }

    private fun isOverlapping(view1: View, view2: View): Boolean {
        val loc1 = IntArray(2)
        val loc2 = IntArray(2)
        view1.getLocationOnScreen(loc1)
        view2.getLocationOnScreen(loc2)

        val rect1 = Rect(loc1[0], loc1[1], loc1[0] + view1.width, loc1[1] + view1.height)
        val rect2 = Rect(loc2[0], loc2[1], loc2[0] + view2.width, loc2[1] + view2.height)

        return Rect.intersects(rect1, rect2)
    }

    private fun startForegroundNotification() {
        val channelId = "voice_notes_notification_channel_id"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.voice_notes_overlay),
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.voice_notes_overlay_active))
            .setSmallIcon(R.drawable.ic_baseline_mic_24).build()

        startForeground(1001, notification)
    }

    private fun overlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE
    }

    override fun onBind(intent: Intent?): IBinder? = null
}