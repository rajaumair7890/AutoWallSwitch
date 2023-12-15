package com.codingwithumair.app.autowallswitch.model

import android.graphics.Bitmap

data class UiState(
	val lightWallpaperTime: Time = Time(),
	val darkWallpaperTime: Time = Time(),
	val lightWallpaperBitmap: Bitmap? = null,
	val darkWallpaperBitmap: Bitmap? = null,
	val shouldAutoChangeWallpaper: Boolean = true,
	val shouldFollowSystemDarkMode: Boolean = true
)

data class AutoWallPreferences(
	val lightWallpaperTime: Time = Time(),
	val darkWallpaperTime: Time = Time(),
	val shouldAutoChangeWallpaper: Boolean = true,
	val shouldFollowSystemDarkMode: Boolean = true
)

data class Time(
	val hourOfDay: Int = 0,
	val minute: Int = 0
)