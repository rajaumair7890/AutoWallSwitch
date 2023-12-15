package com.codingwithumair.app.autowallswitch.backgroundWork

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingwithumair.app.autowallswitch.AutoWallSwitchApplication
import com.codingwithumair.app.autowallswitch.utils.WallpaperUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DarkWallpaperWorker(
	appContext: Context,
	params: WorkerParameters
): CoroutineWorker(appContext, params) {

	override suspend fun doWork(): Result {
		return withContext(Dispatchers.IO) {
			return@withContext try {
				val application = applicationContext as AutoWallSwitchApplication
				WallpaperUtils.changeWallpaper(
					application,
					true
				)
				Result.success()
			} catch (e: Exception) {
				e.printStackTrace()
				Result.failure()
			}
		}
	}
}

class LightWallpaperWorker(
	appContext: Context,
	params: WorkerParameters
): CoroutineWorker(appContext, params) {

	override suspend fun doWork(): Result {
		return withContext(Dispatchers.IO) {
			return@withContext try {
				val application = applicationContext as AutoWallSwitchApplication
				WallpaperUtils.changeWallpaper(
					application,
					false
				)
				Result.success()
			} catch (e: Exception) {
				e.printStackTrace()
				Result.failure()
			}
		}
	}
}