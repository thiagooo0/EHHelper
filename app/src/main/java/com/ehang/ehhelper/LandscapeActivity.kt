package com.ehang.ehhelper

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LandscapeActivity: AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_landscape)
		requestedOrientation =
			ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
		finish()
	}
}