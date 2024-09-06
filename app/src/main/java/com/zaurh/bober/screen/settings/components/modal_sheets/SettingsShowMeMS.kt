package com.zaurh.bober.screen.settings.components.modal_sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.screen.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsShowMeMS(
    settingsViewModel: SettingsViewModel
) {
    val showMeState = settingsViewModel.showMeState.value.state
    val selectedShowMe = settingsViewModel.showMeState.value.showMe
    val loadingState = settingsViewModel.showMeState.value.loading

    if (showMeState) {
        ModalBottomSheet(content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Show me",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        if (loadingState){
                            CircularProgressIndicator(modifier = Modifier.size(24.dp),color = colorResource(id = R.color.backgroundBottom))
                        }
                    }

                    IconButton(onClick = settingsViewModel::onShowMeChange) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = "Select gender you want to match", color = Color.Gray)
                Spacer(modifier = Modifier.size(16.dp))
                ShowMe.entries.forEach {
                    Text(text = if (selectedShowMe == it) it.displayName + " ✔" else it.displayName,
                        modifier = Modifier
                            .clickable {
                                settingsViewModel.selectShowMe(it)
                            }
                            .padding(8.dp)
                            .fillMaxWidth(),
                        color = if (selectedShowMe == it) MaterialTheme.colorScheme.onSecondary else Color.Gray,
                        fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.size(48.dp))

            }
        }, onDismissRequest = {
            settingsViewModel.onShowMeStateChange(false)
        })
    }
}