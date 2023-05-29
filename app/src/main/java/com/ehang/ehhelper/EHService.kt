package com.ehang.ehhelper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Configuration
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PointF
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat


class EHService: AccessibilityService() {
	private val TAG = "EHService"
	private val NOTIFICATION_ID = 1
	private val name = "Ehang Helper"
	private val channel_id = "Ehang Helper"
	private val content = "service is running"

	private var initialX: Int = 0
	private var initialY: Int = 0
	private var initialTouchX: Float = 0.toFloat()
	private var initialTouchY: Float = 0.toFloat()

	private lateinit var upPath: Path
	private lateinit var downPath: Path


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

	private val douyinPackageName = "com.ss.android.ugc.aweme"
	private var currentPackageName = ""

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

		val displayMetrics = DisplayMetrics()
		windowManager.defaultDisplay.getMetrics(displayMetrics)
		//douyin definitely open in Portrait mode
		var screenWidth = 0
		var screenHeight = 0
		if(displayMetrics.widthPixels < displayMetrics.heightPixels) {
			screenWidth = displayMetrics.widthPixels
			screenHeight = displayMetrics.heightPixels
		} else {
			screenWidth = displayMetrics.heightPixels
			screenHeight = displayMetrics.widthPixels
		}
		val startPoint = PointF(screenWidth / 2f, screenHeight / 4f)
		val endPoint = PointF(screenWidth / 2f, screenHeight * 3 / 4f)
		Log.d(TAG, "startPoint $startPoint")
		Log.d(TAG, "endPoint $endPoint")
		upPath = Path()
		upPath.moveTo(startPoint.x, startPoint.y)
		upPath.lineTo(endPoint.x, endPoint.y)
		downPath = Path()
		downPath.moveTo(endPoint.x, endPoint.y)
		downPath.lineTo(startPoint.x, startPoint.y)
	}

	override fun onDestroy() {
		super.onDestroy()
		windowManager.removeView(floatingButton)
	}

	override fun onKeyEvent(event: KeyEvent): Boolean {
		if(TextUtils.equals(currentPackageName, douyinPackageName)){
			when(event.keyCode) {
				KeyEvent.KEYCODE_DPAD_UP -> {
					if(event.action == KeyEvent.ACTION_DOWN) {
						Log.d(TAG, "往上滑")
						dispatchGesture(
							GestureDescription.Builder().addStroke(StrokeDescription(upPath, 0, 200)).build(),
							null, null)
					}
					return true
				}

				KeyEvent.KEYCODE_DPAD_DOWN -> {
					if(event.action == KeyEvent.ACTION_DOWN) {
						Log.d(TAG, "往下滑")
						dispatchGesture(
							GestureDescription.Builder().addStroke(StrokeDescription(downPath, 0, 200)).build(),
							null, null)
					}
					return true
				}
			}
		}
		return super.onKeyEvent(event)
	}

	override fun onAccessibilityEvent(event: AccessibilityEvent) {
		if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			val nodeInfo = event.source
			if(nodeInfo != null) {
				currentPackageName = nodeInfo.packageName.toString()
				Log.d(TAG, "Current package name: $currentPackageName")
			}
		}
	}

	override fun onInterrupt() {
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
			startActivity(Intent(this, LandscapeActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK))
		} else {
			startActivity(Intent(this, PortraitActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK))
		}
	}
}