package com.ehang.ehhelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 *  Created by GuoShaoHong on 2023/4/27!!!
 */
class MainActivity: AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.layout_main)
		findViewById<View>(R.id.btn_start_server).setOnClickListener {
			checkFloatWindowPermissionAndShow()
		}
		findViewById<View>(R.id.btn_end_server).setOnClickListener {
			stopService(Intent(this@MainActivity, EHService::class.java))
		}
	}

	private val floatingWindowPermissionResult =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			if(Settings.canDrawOverlays(this)) {
				showFloatingWindow()
			}
		}

	private fun checkFloatWindowPermissionAndShow() {
		if(Settings.canDrawOverlays(this)) {
			showFloatingWindow()
		} else {
			floatingWindowPermissionResult.launch(
				Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
		}
	}

	private fun showFloatingWindow() {
		startService(Intent(this@MainActivity, EHService::class.java))
		finish()
	}

}
