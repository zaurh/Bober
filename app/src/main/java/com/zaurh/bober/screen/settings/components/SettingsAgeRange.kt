package com.zaurh.bober.screen.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.data.user.UserData

@Composable
fun SettingsAgeRange(
    userData: UserData,
    onValueChange: (ageRangeStart: Float, ageRangeEnd: Float) -> Unit
) {
    var sliderValue by remember { mutableStateOf(0f..100f) }
    val ageRangeStart = userData.ageRangeStart ?: 18f
    val ageRangeEnd = userData.ageRangeEnd ?: 100f

    LaunchedEffect(key1 = true) {
        sliderValue = (ageRangeStart..ageRangeEnd)
    }


    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Age range", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
            val (start, end) = sliderValue.start.toInt() to sliderValue.endInclusive.toInt()
            Text(
                text = "$start - $end",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        RangeSlider(
            valueRange = 18f..100f,
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = {
                onValueChange(
                    sliderValue.start, sliderValue.endInclusive
                )
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onSecondary,
                activeTrackColor = MaterialTheme.colorScheme.onSecondary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSecondary,
            )
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "Show users only within the selected age range.", color = Color.Gray, fontSize = 12.sp)
    }
}