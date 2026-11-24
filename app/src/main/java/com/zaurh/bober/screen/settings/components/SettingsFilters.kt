package com.zaurh.bober.screen.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

const val MIN_AGE = 18f
const val MAX_AGE = 100f
const val MIN_DISTANCE_KM = 2f
const val MAX_DISTANCE_KM = 200f

/** Age range filter. Follows the saved value (also when it loads late) and saves when the drag ends. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeRangeFilter(start: Float, end: Float, onChange: (Float, Float) -> Unit) {
    var range by remember(start, end) { mutableStateOf(start.coerceIn(MIN_AGE, MAX_AGE)..end.coerceIn(MIN_AGE, MAX_AGE)) }
    FilterRow(title = "Age range", value = "${range.start.toInt()} – ${range.endInclusive.toInt()}${if (range.endInclusive >= MAX_AGE) "+" else ""}") {
        RangeSlider(
            value = range,
            valueRange = MIN_AGE..MAX_AGE,
            onValueChange = { range = it },
            onValueChangeFinished = { onChange(range.start, range.endInclusive) }
        )
    }
}

/** Maximum distance filter, same saving behaviour as [AgeRangeFilter]. */
@Composable
fun DistanceFilter(distance: Float, onChange: (Float) -> Unit) {
    var value by remember(distance) { mutableFloatStateOf(distance.coerceIn(MIN_DISTANCE_KM, MAX_DISTANCE_KM)) }
    FilterRow(title = "Maximum distance", value = "${value.toInt()} km") {
        Slider(
            value = value,
            valueRange = MIN_DISTANCE_KM..MAX_DISTANCE_KM,
            onValueChange = { value = it },
            onValueChangeFinished = { onChange(value) }
        )
    }
}

@Composable
private fun FilterRow(title: String, value: String, slider: @Composable () -> Unit) {
    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Text(value, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(4.dp))
        slider()
    }
}
