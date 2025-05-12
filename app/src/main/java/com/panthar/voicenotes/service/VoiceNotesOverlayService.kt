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
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import com.panthar.voicenotes.util.startListeningLoop
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceNotesOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var floatingButton: ImageButton
    private lateinit var closeTarget: ImageView

    private var isSpeechRecognitionActive = false
    private lateinit var micParams: WindowManager.LayoutParams
    private lateinit var closeParams: WindowManager.LayoutParams

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
        floatingButton = ImageButton(this).apply {
            setImageResource(com.panthar.voicenotes.R.drawable.ic_baseline_mic_24)
            background = null
        }

        floatingButton.setOnClickListener {
            if (!isSpeechRecognitionActive) {
                startSpeechRecognition()
            } else {
                stopSpeechRecognition()
            }
        }

        floatingButton.setOnTouchListener(object : View.OnTouchListener {
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
                        windowManager.updateViewLayout(floatingButton, micParams)
                        moved = true
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        hideCloseTarget()
                        if (moved && isOverlapping(floatingButton, closeTarget)) {
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

        windowManager.addView(floatingButton, micParams)
    }

    private fun startSpeechRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        startListeningLoop(
            speechRecognizer,
            onPartial = { Log.d("Speech", "Partial: $it") },
            onFinal = { Log.d("Speech", "Final: $it") },
            shouldContinue = { true })
        isSpeechRecognitionActive = true
        floatingButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
    }

    private fun stopSpeechRecognition() {
        if (this::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
        }
        isSpeechRecognitionActive = false
        floatingButton.setImageResource(com.panthar.voicenotes.R.drawable.ic_baseline_mic_24)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            windowManager.removeView(floatingButton)
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
                getString(com.panthar.voicenotes.R.string.voice_notes_overlay),
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(com.panthar.voicenotes.R.string.voice_notes_overlay_active))
            .setSmallIcon(com.panthar.voicenotes.R.drawable.ic_baseline_mic_24).build()

        startForeground(1001, notification)
    }

    private fun overlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE
    }

    override fun onBind(intent: Intent?): IBinder? = null
}