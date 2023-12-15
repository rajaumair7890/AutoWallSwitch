package com.codingwithumair.app.autowallswitch.backgroundWork

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.codingwithumair.app.autowallswitch.model.Time
import com.codingwithumair.app.autowallswitch.utils.Constants.Dark_Wallpaper_Work
import com.codingwithumair.app.autowallswitch.utils.Constants.Light_Wallpaper_Work
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class WorkManagerRepository(context: Context) {

	private val workManager = WorkManager.getInstance(context)

	fun scheduleSwitchToDarkWallpaper(time: Time){
		val workRequest = PeriodicWorkRequestBuilder<DarkWallpaperWorker>(
			24,TimeUnit.HOURS,
		).setInitialDelay(time.calculateInitialDelaySeconds(), TimeUnit.SECONDS)
		workManager.enqueueUniquePeriodicWork(
			Dark_Wallpaper_Work,
			ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
			workRequest.build()
		)
	}

	fun scheduleSwitchToLightWallpaper(time: Time){
		val workRequest = PeriodicWorkRequestBuilder<LightWallpaperWorker>(
			24,
			TimeUnit.HOURS,
		).setInitialDelay(
			time.calculateInitialDelaySeconds(),
			TimeUnit.SECONDS
		).build()
		workManager.enqueueUniquePeriodicWork(
			Light_Wallpaper_Work,
			ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
			workRequest
		)
	}

	fun cancelAllWork(){
		workManager.cancelAllWork()
	}

	private fun Time.calculateInitialDelaySeconds():Long {
		val currentDateTime = LocalDateTime.now()
		var calculatedDateTime = currentDateTime
			.plusHours((hourOfDay - currentDateTime.hour).toLong())
			.plusMinutes((minute - currentDateTime.minute).toLong())
		if(calculatedDateTime.isBefore(currentDateTime)){
			//if time has already passed for today then add 24 hours
			calculatedDateTime = calculatedDateTime.plusHours(24)
		}
		val zoneOffset = ZonedDateTime.now().offset
		return calculatedDateTime.toEpochSecond(zoneOffset) - currentDateTime.toEpochSecond(zoneOffset)
	}
}