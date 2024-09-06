package com.zaurh.bober.screen.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.data.user.UserData

@Composable
fun SettingsDistance(
    userData: UserData,
    onValueChange: (distance: Float) -> Unit,
    onSelectCheckBox: (Boolean) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(200f) }
    var showFullDistanceState by remember { mutableStateOf(true) }

    val maximumDistance = userData.maximumDistance ?: 200f
    val showFullDistance = userData.showFullDistance ?: true

    LaunchedEffect(key1 = true) {
        sliderValue = maximumDistance
        showFullDistanceState = showFullDistance
    }


    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Maximum distance", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
            Text(
                text = "${sliderValue.toInt()} km.",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Slider(
            value = sliderValue,
            valueRange = 2f..200f,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = {
                onValueChange(sliderValue)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onSecondary,
                activeTrackColor = MaterialTheme.colorScheme.onSecondary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSecondary,
            ),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            Modifier
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Show people further if there is \nno one in the selected distance.",
                color = MaterialTheme.colorScheme.primary
            )
            Checkbox(
                checked = showFullDistanceState,
                onCheckedChange = {
                    showFullDistanceState = it
                    onSelectCheckBox(it)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.onSecondary,
                    uncheckedColor = Color.Gray,
                    checkmarkColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    }
}