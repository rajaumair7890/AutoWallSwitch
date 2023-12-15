package com.codingwithumair.app.autowallswitch.utils

import android.app.WallpaperManager
import com.codingwithumair.app.autowallswitch.AutoWallSwitchApplication
import com.codingwithumair.app.autowallswitch.utils.Constants.Dark_Wallpaper_FileName
import com.codingwithumair.app.autowallswitch.utils.Constants.Light_Wallpaper_FileName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object WallpaperUtils {

	fun changeWallpaper(
		application: AutoWallSwitchApplication,
		changeToDark: Boolean
	){
		CoroutineScope(Dispatchers.IO).launch {
			val localFileStorageRepository = application.container.localFileStorageRepository
			if (changeToDark) {
				localFileStorageRepository.loadImageFromInternalStorage(
					Dark_Wallpaper_FileName
				)
			} else {
				localFileStorageRepository.loadImageFromInternalStorage(
					Light_Wallpaper_FileName
				)
			}.collectLatest { bitmap ->
				if (bitmap != null) {
					WallpaperManager.getInstance(application).apply {
						setBitmap(bitmap)
					}
				}
			}
		}
	}
}