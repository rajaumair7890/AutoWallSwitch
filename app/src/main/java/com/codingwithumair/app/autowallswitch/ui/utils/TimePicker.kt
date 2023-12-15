package com.codingwithumair.app.autowallswitch.ui.utils

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.codingwithumair.app.autowallswitch.model.Time


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
	currentTime: Time,
	onTimeConfirm: (Time) -> Unit,
	modifier: Modifier = Modifier
){
	var showTimePicker by remember{
		mutableStateOf(false)
	}
	val timePickerState = rememberTimePickerState()

	TimeCard(
		time = currentTime,
		onClick = {
			showTimePicker = true
		},
		modifier = modifier
			.defaultMinSize(100.dp, 50.dp)
	)

	if (showTimePicker){
		Box(propagateMinConstraints = false){
			TimePickerDialog(
				onCancel = { showTimePicker = false },
				onConfirm = {
					onTimeConfirm(
						Time(
							hourOfDay = timePickerState.hour,
							minute = timePickerState.minute,
						)
					)
					Log.d("TimePicker", "is24Hours: ${timePickerState.is24hour}")
					showTimePicker = false
				}
			) {
				androidx.compose.material3.TimePicker(state = timePickerState)
			}
		}
	}
}

@Composable
private fun TimeCard(
	time: Time,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
){
	Card(
		shape = RoundedCornerShape(14.dp),
		modifier = modifier
			.clickable(onClick = onClick)
			.width(150.dp)
	){
		Row{
			TimeCardItem(
				text = time.hourOfDay.toString(),
				modifier = Modifier
					.weight(1f)
					.background(MaterialTheme.colorScheme.primaryContainer)
			)
			TimeCardItem(
				text = time.minute.toString(),
				modifier = Modifier
					.weight(1f)
					.background(MaterialTheme.colorScheme.secondaryContainer)
			)
		}
	}
}

@Composable
private fun TimeCardItem(
	text: String,
	modifier: Modifier = Modifier
){
	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
	) {
		Text(
			text = text,
			fontFamily = FontFamily.Monospace,
			fontSize = 48.sp,
			fontWeight = FontWeight.Bold,
			modifier = Modifier.padding(2.dp)
		)
	}
}



@Composable
private fun TimePickerDialog(
	title: String = "Select Time",
	onCancel: () -> Unit,
	onConfirm: () -> Unit,
	toggle: @Composable () -> Unit = {},
	content: @Composable () -> Unit,
) {
	Dialog(
		onDismissRequest = onCancel,
		properties = DialogProperties(
			usePlatformDefaultWidth = false
		),
	) {
		Surface(
			shape = MaterialTheme.shapes.extraLarge,
			tonalElevation = 6.dp,
			modifier = Modifier
				.width(IntrinsicSize.Min)
				.height(IntrinsicSize.Min)
				.background(
					shape = MaterialTheme.shapes.extraLarge,
					color = MaterialTheme.colorScheme.surface
				),
		) {
			toggle()
			Column(
				modifier = Modifier.padding(24.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 20.dp),
					text = title,
					style = MaterialTheme.typography.labelMedium
				)
				content()
				Row(
					modifier = Modifier
						.height(40.dp)
						.fillMaxWidth()
				) {
					Spacer(modifier = Modifier.weight(1f))
					TextButton(
						onClick = onCancel
					) { Text("Cancel") }
					TextButton(
						onClick = onConfirm
					) { Text("OK") }
				}
			}
		}
	}
}