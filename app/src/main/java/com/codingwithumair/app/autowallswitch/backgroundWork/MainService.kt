package com.codingwithumair.app.autowallswitch.backgroundWork

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codingwithumair.app.autowallswitch.AutoWallSwitchApplication
import com.codingwithumair.app.autowallswitch.R
import com.codingwithumair.app.autowallswitch.utils.WallpaperUtils

class MainService: Service() {

	private var lastDarkModeEnabledState = false

	private val darkModeReceiver = object: BroadcastReceiver(){
		override fun onReceive(context: Context?, intent: Intent?) {
			Log.d("DarkModeReceiver", "on Receive Called")
			if(intent?.action == Intent.ACTION_CONFIGURATION_CHANGED){
				val isDarkMode = this@MainService.application.resources.configuration.isNightModeActive
				Log.d("MainService", "DarkMode: $isDarkMode")
				if(lastDarkModeEnabledState != isDarkMode){
					val application = this@MainService.application as AutoWallSwitchApplication
					WallpaperUtils.changeWallpaper(application, isDarkMode).also{
						lastDarkModeEnabledState = isDarkMode
					}
				}
			}
		}
	}

	override fun onCreate() {
		super.onCreate()
		startForeground(2, makeNotification())
		lastDarkModeEnabledState = application.resources.configuration.isNightModeActive
		registerReceiver(darkModeReceiver, IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
	}

	override fun onDestroy() {
		unregisterReceiver(darkModeReceiver)
		super.onDestroy()
	}

	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	private fun makeNotification(): Notification {
		(getSystemService(NOTIFICATION_SERVICE) as NotificationManager).apply {
			createNotificationChannel(
				NotificationChannel(
					packageName,
					"Wallpaper Service",
					NotificationManager.IMPORTANCE_LOW
				)
			)
		}
		return NotificationCompat
			.Builder(this, packageName)
			.setOngoing(false)
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.setContentTitle("Wallpaper Service is Running")
			.setPriority(NotificationManager.IMPORTANCE_LOW)
			.setCategory(Notification.CATEGORY_SERVICE)
			.build()
	}
}