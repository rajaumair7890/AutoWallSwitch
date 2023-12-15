package com.codingwithumair.app.autowallswitch

import android.app.Application
import com.codingwithumair.app.autowallswitch.data.AppContainer

class AutoWallSwitchApplication: Application() {

	lateinit var container: AppContainer

	override fun onCreate() {
		super.onCreate()
		container = AppContainer(this)
	}
}