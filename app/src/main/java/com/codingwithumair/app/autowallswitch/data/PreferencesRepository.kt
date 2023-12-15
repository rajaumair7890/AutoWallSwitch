package com.codingwithumair.app.autowallswitch.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.codingwithumair.app.autowallswitch.model.AutoWallPreferences
import com.codingwithumair.app.autowallswitch.model.Time
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferencesRepository(
	context: Context
) {

	private val dataStore = context.dataStore

	val preferences: Flow<AutoWallPreferences> = dataStore.data
		.catch {
			if(it is IOException){
				Log.e(TAG, "error reading preferences", it)
				emit(emptyPreferences())
			}else{
				throw it
			}
		}.map{ preferences ->
			AutoWallPreferences(
				lightWallpaperTime = preferences[Light_Wallpaper_Time]?.convertToTime() ?: Time(),
				darkWallpaperTime = preferences[Dark_Wallpaper_Time]?.convertToTime() ?: Time(),
				shouldFollowSystemDarkMode = preferences[Should_Follow_System_DarkMode] ?: true,
				shouldAutoChangeWallpaper = preferences[Should_Auto_Change_Wallpaper] ?: true
			)
		}

	suspend fun changeLightWallpaperTime(newTime: Time){
		dataStore.edit { preferences ->
			preferences[Light_Wallpaper_Time] = newTime.convertToString()
		}
	}

	suspend fun changeDarkWallpaperTime(newTime: Time){
		dataStore.edit { preferences ->
			preferences[Dark_Wallpaper_Time] = newTime.convertToString()
		}
	}

	suspend fun changeShouldFollowSystemDarkMode(newPreference: Boolean){
		dataStore.edit { preferences ->
			preferences[Should_Follow_System_DarkMode] = newPreference
		}
	}

	suspend fun changeShouldAutoChangeWallpaper(newPreference: Boolean){
		dataStore.edit { preferences ->
			preferences[Should_Auto_Change_Wallpaper] = newPreference
		}
	}

	private fun String.convertToTime(): Time {
		val splittedString = split("/")
		return if (splittedString.isEmpty()){
			 Time()
		}else {
			Time(
				splittedString[0].toInt(),
				splittedString[1].toInt()
			)
		}
	}

	private fun Time.convertToString(): String{
		return "${hourOfDay}/${minute}"
	}

	private companion object{
		private val Light_Wallpaper_Time = stringPreferencesKey("light_wallpaper_time")
		private val Dark_Wallpaper_Time = stringPreferencesKey("dark_wallpaper_time")
		private val Should_Follow_System_DarkMode = booleanPreferencesKey("should_follow_system_darkMode")
		private val Should_Auto_Change_Wallpaper = booleanPreferencesKey("should_auto_change_wallpaper")

		private const val WALLPAPER_DATASTORE = "wallpaper_datastore"

		private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
			name = WALLPAPER_DATASTORE
		)

		const val TAG = "PreferencesRepository"
	}
}