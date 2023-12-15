package com.codingwithumair.app.autowallswitch

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codingwithumair.app.autowallswitch.backgroundWork.MainService
import com.codingwithumair.app.autowallswitch.ui.mainScreen.MainScreen
import com.codingwithumair.app.autowallswitch.ui.mainScreen.MainViewModel
import com.codingwithumair.app.autowallswitch.ui.theme.AutoWallSwitchTheme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		WindowCompat.setDecorFitsSystemWindows(window, false)
		super.onCreate(savedInstanceState)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			val permission = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
			if(permission == PackageManager.PERMISSION_DENIED){
				requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
			}
		}
		setContent {
			AutoWallSwitchTheme {
				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {

					val viewModel: MainViewModel = viewModel(factory = MainViewModel.factory)
					val uiState by viewModel.uiState.collectAsState()

					LaunchedEffect(key1 = uiState.shouldFollowSystemDarkMode){
						delay(500) // It takes some time to read values from dataStore
						val shouldStartService = uiState.shouldFollowSystemDarkMode && uiState.shouldAutoChangeWallpaper
						startOrStopService(shouldStartService)
					}

					MainScreen(
						shouldAutoChangeWallpaper = uiState.shouldAutoChangeWallpaper,
						onAutoChangeWallpaperChange = viewModel::updateShouldAutoChangeWallpaper,
						shouldFollowSystemDarkMode = uiState.shouldFollowSystemDarkMode,
						onFollowSystemDarkModeChange = viewModel::updateShouldFollowSystemDarkMode,
						lightWallpaper = uiState.lightWallpaperBitmap,
						darkWallpaper = uiState.darkWallpaperBitmap,
						onLightWallpaperChange = viewModel::updateLightWallpaper,
						onDarkWallpaperChange = viewModel::updateDarkWallpaper,
						lightWallPaperTime = uiState.lightWallpaperTime,
						darkWallpaperTime = uiState.darkWallpaperTime,
						onLightWallpaperTimeChanged = viewModel::updateLightWallpaperTime,
						onDarkWallpaperTimeChanged = viewModel::updateDarkWallpaperTime
					)
				}
			}
		}
	}

	private fun startOrStopService(shouldStartService: Boolean){
		val serviceIntent = Intent(this, MainService::class.java)
		val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
		val isAlreadyRunning = activityManager?.getRunningServices(Integer.MAX_VALUE)?.any {
			it.service == serviceIntent.component
		} ?: false

		Log.d(TAG, "ServiceAlreadyRunning: $isAlreadyRunning")

		if(shouldStartService) {
			if(!isAlreadyRunning) {
				startForegroundService(serviceIntent)
				Log.d(TAG, "ServiceStarted")
			}
		}else {
			if(isAlreadyRunning) {
				stopService(serviceIntent)
				Log.d(TAG, "ServiceStopped")
			}
		}
	}

	private companion object{
		const val TAG = "MainActivity"
	}
}
