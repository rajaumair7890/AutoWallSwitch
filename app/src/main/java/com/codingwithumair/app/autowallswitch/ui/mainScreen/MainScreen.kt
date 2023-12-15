package com.codingwithumair.app.autowallswitch.ui.mainScreen

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.codingwithumair.app.autowallswitch.R
import com.codingwithumair.app.autowallswitch.model.Time
import com.codingwithumair.app.autowallswitch.ui.utils.TimePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
	shouldAutoChangeWallpaper: Boolean,
	onAutoChangeWallpaperChange: (Boolean) -> Unit,
	shouldFollowSystemDarkMode: Boolean,
	onFollowSystemDarkModeChange : (Boolean) -> Unit,
	lightWallpaper: Bitmap?,
	darkWallpaper: Bitmap?,
	onLightWallpaperChange: (Uri) -> Unit,
	onDarkWallpaperChange: (Uri) -> Unit,
	lightWallPaperTime: Time,
	darkWallpaperTime: Time,
	onLightWallpaperTimeChanged: (Time) -> Unit,
	onDarkWallpaperTimeChanged: (Time) -> Unit,
	modifier: Modifier = Modifier
){

	LazyColumn(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top,
		modifier = modifier
			.fillMaxSize()
	) {
		item {
			Spacer(modifier = Modifier.size(48.dp))
			DarkAndLightCard(
				lightWallpaper = lightWallpaper,
				darkWallpaper = darkWallpaper,
				onLightWallpaperChange = onLightWallpaperChange,
				onDarkWallpaperChange = onDarkWallpaperChange
			)
		}
		item {
			PreferencesCard(
				shouldAutoChangeWallpaper = shouldAutoChangeWallpaper,
				onAutoChangeWallpaperChange = onAutoChangeWallpaperChange,
				shouldFollowSystemDarkMode = shouldFollowSystemDarkMode,
				onFollowSystemDarkModeChange = onFollowSystemDarkModeChange,
				lightWallPaperTime = lightWallPaperTime,
				darkWallpaperTime = darkWallpaperTime,
				onLightWallpaperTimeChanged = onLightWallpaperTimeChanged,
				onDarkWallpaperTimeChanged = onDarkWallpaperTimeChanged
			)
		}
		item {
			if(shouldFollowSystemDarkMode) {
				var expanded by remember {
					mutableStateOf(false)
				}
				ElevatedCard(
					onClick = { expanded = !expanded },
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
						.animateContentSize(
							animationSpec = spring(
								dampingRatio = Spring.DampingRatioLowBouncy,
								stiffness = Spring.StiffnessLow
							)
						)
				) {
					TitleText(
						text = stringResource(id = R.string.note),
						modifier = Modifier.padding(12.dp)
					)
					if (expanded) {
						Text(
							text = stringResource(id = R.string.note_body),
							fontFamily = FontFamily.Monospace,
							fontSize = 14.sp,
							fontWeight = FontWeight.Bold,
							modifier = Modifier.padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
						)
					}
				}
			}
		}
	}
}


@Composable
fun DarkAndLightCard(
	lightWallpaper: Bitmap?,
	darkWallpaper: Bitmap?,
	onLightWallpaperChange: (Uri) -> Unit,
	onDarkWallpaperChange: (Uri) -> Unit,
	modifier: Modifier = Modifier
){
	val lightWallpaperImagePicker = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent(),
		onResult = { uri ->
			if(uri != null){
				onLightWallpaperChange(uri)
			}
		}
	)
	val darkWallpaperImagePicker = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent(),
		onResult = { uri ->
			if(uri != null){
				onDarkWallpaperChange(uri)
			}
		}
	)

	ElevatedCard(
		modifier = modifier
			.fillMaxWidth()
			.padding(16.dp)
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			ImageAndTextColumn(
				image = lightWallpaper,
				onImageClick = {
					lightWallpaperImagePicker.launch("image/*")
				},
				text = stringResource(id = R.string.light_wallpaper)
			)
			ImageAndTextColumn(
				image = darkWallpaper,
				onImageClick = {
					darkWallpaperImagePicker.launch("image/*")
				},
				text = stringResource(id = R.string.dark_wallpaper)
			)
		}
	}
}


@Composable
fun PreferencesCard(
	shouldAutoChangeWallpaper: Boolean,
	onAutoChangeWallpaperChange: (Boolean) -> Unit,
	shouldFollowSystemDarkMode: Boolean,
	onFollowSystemDarkModeChange : (Boolean) -> Unit,
	lightWallPaperTime: Time,
	darkWallpaperTime: Time,
	onLightWallpaperTimeChanged: (Time) -> Unit,
	onDarkWallpaperTimeChanged: (Time) -> Unit,
	modifier: Modifier = Modifier
){
	ElevatedCard(
		modifier = modifier
			.fillMaxWidth()
			.padding(16.dp)
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp),
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp)
				.animateContentSize(
					animationSpec = spring(
						dampingRatio = Spring.DampingRatioLowBouncy,
						stiffness = Spring.StiffnessLow
					)
				)
		) {
			TextAndSwitchRow(
				text = stringResource(id = R.string.auto_change_wallpaper),
				checked = shouldAutoChangeWallpaper,
				onCheckChange = onAutoChangeWallpaperChange
			)
			if(shouldAutoChangeWallpaper) {

				TextAndSwitchRow(
					text = stringResource(id = R.string.follow_system_darkMode),
					checked = shouldFollowSystemDarkMode,
					onCheckChange = onFollowSystemDarkModeChange
				)

				if (!shouldFollowSystemDarkMode) {
					Divider(thickness = 2.dp, color = Color.Gray)
					TimePickerItem(
						text = stringResource(id = R.string.switch_to_light_wallpaper_at),
						currentTime = lightWallPaperTime,
						onTimeConfirm = onLightWallpaperTimeChanged
					)
					TimePickerItem(
						text = stringResource(id = R.string.switch_to_dark_wallpaper_at),
						currentTime = darkWallpaperTime,
						onTimeConfirm = onDarkWallpaperTimeChanged
					)
				}
			}
		}
	}
}


@Composable
fun ImageAndTextColumn(
	image: Bitmap?,
	onImageClick: () -> Unit,
	text: String,
	modifier: Modifier = Modifier
){
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		ImageCard(
			image = image,
			onClick = onImageClick
		)
		Text(
			text = text,
			fontFamily = FontFamily.Monospace,
			fontSize = 14.sp,
			fontWeight = FontWeight.Bold
		)
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCard(
	image: Bitmap?,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
){
	val configuration = LocalConfiguration.current
	Card(
		modifier = if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
			modifier.size(width = 150.dp, height = 320.dp)
		}else{
			modifier.size(width = 320.dp, height = 150.dp)
		},
		onClick = onClick
	) {
		AsyncImage(
			model = image,
			contentDescription = null,
			contentScale = ContentScale.Crop,
			alignment = Alignment.Center
		)
	}
}


@Composable
fun TimePickerItem(
	text: String,
	currentTime: Time,
	onTimeConfirm: (Time) -> Unit,
){
	Text(
		text = text,
		fontFamily = FontFamily.Monospace,
		fontSize = 16.sp,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.padding(2.dp)
	)
	TimePicker(
		currentTime = currentTime,
		onTimeConfirm = onTimeConfirm
	)
}


@Composable
fun TitleText(
	text: String,
	modifier: Modifier = Modifier
){
	Text(
		text = text,
		fontFamily = FontFamily.Monospace,
		fontSize = 18.sp,
		fontWeight = FontWeight.Bold,
		modifier = modifier
	)
}

@Composable
fun TextAndSwitchRow(
	text: String,
	checked: Boolean,
	onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
){
	Row(
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
	) {
		TitleText(
			text,
			modifier = Modifier.padding(4.dp)
		)
		Switch(
			checked = checked,
			onCheckedChange = onCheckChange
		)
	}
}

