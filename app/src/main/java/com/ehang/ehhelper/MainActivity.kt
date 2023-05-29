package com.ehang.ehhelper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 *  Created by GuoShaoHong on 2023/4/27!!!
 */
class MainActivity: AppCompatActivity() {
	private lateinit var btnPA: View
	private lateinit var tvPA: View
	private lateinit var btnFW: View

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.layout_main)

		btnPA = findViewById(R.id.btn_permission_accessibility)
		btnPA.setOnClickListener {
			startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
		}
		tvPA = findViewById(R.id.tv_hint_accessibility)
		btnFW = findViewById(R.id.btn_permission_float_window)
		btnFW.setOnClickListener {
			startActivity(
				Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
		}

		findViewById<View>(R.id.btn_start_server).setOnClickListener {
			sendBroadcast(Intent(broadcastStartServer))
			finish()
		}
		findViewById<View>(R.id.btn_end_server).setOnClickListener {
			sendBroadcast(Intent(broadcastStopServer))
			finish()
		}
	}

	override fun onResume() {
		super.onResume()
		btnFW.visibility = if(Settings.canDrawOverlays(this)) View.GONE else View.VISIBLE
		val hasPermission = isAccessibilityServiceEnabled(this, EHService::class.java)
		btnPA.visibility = if(hasPermission) View.GONE else View.VISIBLE
		tvPA.visibility = if(hasPermission) View.GONE else View.VISIBLE
	}

	private fun isAccessibilityServiceEnabled(context: Context, accessibilityServiceClass: Class<*>): Boolean {
		val serviceName = "${context.packageName}/${accessibilityServiceClass.name}"
		var accessibilityEnabled = 0
		try {
			accessibilityEnabled = Settings.Secure.getInt(
				context.applicationContext.contentResolver,
				Settings.Secure.ACCESSIBILITY_ENABLED
			)
		} catch(e: Settings.SettingNotFoundException) {
			e.printStackTrace()
		}
		val stringSplitter = TextUtils.SimpleStringSplitter(':')

		if(accessibilityEnabled == 1) {
			val settingValue = Settings.Secure.getString(
				context.applicationContext.contentResolver,
				Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
			)
			settingValue?.let {
				stringSplitter.setString(it)
				while(stringSplitter.hasNext()) {
					val accessibilityService = stringSplitter.next()
					if(accessibilityService.equals(serviceName, ignoreCase = true)) {
						return true
					}
				}
			}
		}
		return false
	}
}
