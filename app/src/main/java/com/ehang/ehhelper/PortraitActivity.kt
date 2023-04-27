package com.ehang.ehhelper

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PortraitActivity: AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_portrait)
		requestedOrientation =
			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
		finish()
	}
}