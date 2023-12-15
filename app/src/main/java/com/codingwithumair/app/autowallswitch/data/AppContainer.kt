package com.codingwithumair.app.autowallswitch.data

import android.content.Context
import com.codingwithumair.app.autowallswitch.backgroundWork.WorkManagerRepository

class AppContainer(
	private val context: Context
) {

	val localFileStorageRepository by lazy{
		LocalFileStorageRepository(context)
	}

	val preferencesRepository by lazy {
		PreferencesRepository(context)
	}

	val workManagerRepository by lazy {
		WorkManagerRepository(context)
	}

}