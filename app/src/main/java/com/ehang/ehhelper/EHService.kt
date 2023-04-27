package com.ehang.ehhelper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.core.app.NotificationCompat


class EHService: Service() {
	private val TAG = "EHService"
	private val NOTIFICATION_ID = 1
	private val name = "Ehang Helper"
	private val channel_id = "Ehang Helper"
	private val content = "service is running"

	private var initialX: Int = 0
	private var initialY: Int = 0
	private var initialTouchX: Float = 0.toFloat()
	private var initialTouchY: Float = 0.toFloat()

	private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
	private val floatingButton: View by lazy {
		val fb = LayoutInflater.from(this).inflate(R.layout.item_fb, null)
		fb.setOnClickListener {
			rotateScreen()
		}
		fb.setOnTouchListener { _, event ->
			when(event.action) {
				MotionEvent.ACTION_DOWN -> {
					initialX = fbParams.x
					initialY = fbParams.y
					initialTouchX = event.rawX
					initialTouchY = event.rawY
				}

				MotionEvent.ACTION_MOVE -> {
					fbParams.x = initialX + (event.rawX - initialTouchX).toInt()
					fbParams.y = initialY + (event.rawY - initialTouchY).toInt()
					windowManager?.updateViewLayout(fb, fbParams)
				}

				else -> {}
			}
			return@setOnTouchListener false
		}
		fb
	}
	private val fbParams: LayoutParams by lazy {
		val params =
			LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_APPLICATION_OVERLAY,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
		params.gravity = Gravity.TOP or Gravity.START
		params.x = 0
		params.y = 50
		params
	}


	override fun onCreate() {
		super.onCreate()
		Log.d(TAG, "onService create")
		createNotificationChannel()
		val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channel_id)
			.setContentTitle(name)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentText(content)

		// 开启前台服务
		startForeground(NOTIFICATION_ID, builder.build())

		windowManager.addView(floatingButton, fbParams)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Log.d(TAG, "onStartCommand")
		return super.onStartCommand(intent, flags, startId)
	}

	override fun onDestroy() {
		super.onDestroy()
		windowManager.removeView(floatingButton)
	}

	override fun onBind(intent: Intent): IBinder? {
		return null
	}

	private fun createNotificationChannel() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel = NotificationChannel(channel_id, name, importance)
			channel.description = content

			val notificationManager = getSystemService(NotificationManager::class.java)
			notificationManager.createNotificationChannel(channel)
		}
	}

	private fun rotateScreen() {
		val orientation = resources.configuration.orientation
		if(orientation == Configuration.ORIENTATION_PORTRAIT) {
			startActivity(Intent(this, LandscapeActivity::class.java))
		} else {
			startActivity(Intent(this, PortraitActivity::class.java))
		}
	}
}