package com.codingwithumair.app.autowallswitch.ui.mainScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.codingwithumair.app.autowallswitch.AutoWallSwitchApplication
import com.codingwithumair.app.autowallswitch.backgroundWork.WorkManagerRepository
import com.codingwithumair.app.autowallswitch.data.LocalFileStorageRepository
import com.codingwithumair.app.autowallswitch.data.PreferencesRepository
import com.codingwithumair.app.autowallswitch.model.AutoWallPreferences
import com.codingwithumair.app.autowallswitch.model.Time
import com.codingwithumair.app.autowallswitch.model.UiState
import com.codingwithumair.app.autowallswitch.utils.Constants.Dark_Wallpaper_FileName
import com.codingwithumair.app.autowallswitch.utils.Constants.Light_Wallpaper_FileName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
	private val preferencesRepository: PreferencesRepository,
	private val localFileStorageRepository: LocalFileStorageRepository,
	private val workManagerRepository: WorkManagerRepository
): ViewModel() {

	private val _uiState = MutableStateFlow(UiState())
	val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch(Dispatchers.IO) {

			val preferences = preferencesRepository.preferences.firstOrNull() ?: AutoWallPreferences()

			_uiState.update {
				it.copy(
					lightWallpaperBitmap = localFileStorageRepository.loadImageFromInternalStorage(
						Light_Wallpaper_FileName).firstOrNull(),
					darkWallpaperBitmap = localFileStorageRepository.loadImageFromInternalStorage(
						Dark_Wallpaper_FileName).firstOrNull(),
					lightWallpaperTime = preferences.lightWallpaperTime,
					darkWallpaperTime = preferences.darkWallpaperTime,
					shouldFollowSystemDarkMode = preferences.shouldFollowSystemDarkMode,
					shouldAutoChangeWallpaper = preferences.shouldAutoChangeWallpaper
				)
			}
		}
	}

	fun updateLightWallpaper(uri: Uri){
		viewModelScope.launch {
			val bitmap = localFileStorageRepository.getImageBitmapFromContentUri(uri)
			localFileStorageRepository.saveImageToInternalStorage(Light_Wallpaper_FileName, bitmap)
			_uiState.update {
				it.copy(lightWallpaperBitmap = bitmap)
			}
		}
	}

	fun updateDarkWallpaper(uri: Uri){
		viewModelScope.launch {
			val bitmap = localFileStorageRepository.getImageBitmapFromContentUri(uri)
			localFileStorageRepository.saveImageToInternalStorage(Dark_Wallpaper_FileName, bitmap)
			_uiState.update {
				it.copy(darkWallpaperBitmap = bitmap)
			}
		}
	}

	fun updateLightWallpaperTime(time: Time){
		_uiState.update {
			it.copy(lightWallpaperTime = time)
		}.also {
			viewModelScope.launch(Dispatchers.IO) {
				preferencesRepository.changeLightWallpaperTime(time)
			}
			if(_uiState.value.shouldAutoChangeWallpaper
				&& _uiState.value.shouldFollowSystemDarkMode.not()
			){
				workManagerRepository.scheduleSwitchToLightWallpaper(time)
			}
		}
	}

	fun updateDarkWallpaperTime(time: Time){
		_uiState.update {
			it.copy(darkWallpaperTime = time)
		}.also {
			viewModelScope.launch(Dispatchers.IO){
				preferencesRepository.changeDarkWallpaperTime(time)
			}
			if(_uiState.value.shouldAutoChangeWallpaper
				&& _uiState.value.shouldFollowSystemDarkMode.not()
			){
				workManagerRepository.scheduleSwitchToDarkWallpaper(time)
			}
		}
	}

	fun updateShouldFollowSystemDarkMode(newPreference: Boolean){
		_uiState.update {
			it.copy(shouldFollowSystemDarkMode = newPreference)
		}.also {
			if(newPreference){
				cancelAllWork()
			}
			viewModelScope.launch(Dispatchers.IO) {
				preferencesRepository.changeShouldFollowSystemDarkMode(newPreference)
			}
		}
	}

	fun updateShouldAutoChangeWallpaper(newPreference: Boolean){
		_uiState.update {
			it.copy(shouldAutoChangeWallpaper = newPreference)
		}.also {
			if(newPreference){
				reScheduleWork()
			}else{
				updateShouldFollowSystemDarkMode(false)
				cancelAllWork()
			}
			viewModelScope.launch(Dispatchers.IO) {
				preferencesRepository.changeShouldAutoChangeWallpaper(newPreference)
			}
		}
	}

	private fun cancelAllWork() = workManagerRepository.cancelAllWork()

	private fun reScheduleWork(){
		if(
			_uiState.value.darkWallpaperTime.hourOfDay != 0
			&& _uiState.value.darkWallpaperTime.minute != 0
		){
			workManagerRepository.scheduleSwitchToDarkWallpaper(_uiState.value.darkWallpaperTime)
		}
		if(
			_uiState.value.lightWallpaperTime.hourOfDay != 0
			&& _uiState.value.lightWallpaperTime.minute != 0
		){
			workManagerRepository.scheduleSwitchToLightWallpaper(_uiState.value.lightWallpaperTime)
		}
	}

	companion object{
		val factory = viewModelFactory {
			initializer {
				val application = (this[APPLICATION_KEY] as AutoWallSwitchApplication)
				MainViewModel(
					application.container.preferencesRepository,
					application.container.localFileStorageRepository,
					application.container.workManagerRepository
				)
			}
		}
	}
}