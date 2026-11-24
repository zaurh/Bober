package com.zaurh.bober.screen.settings.components.modal_sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.screen.settings.SettingsViewModel

/** Who appears on the discover deck. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsShowMeMS(settingsViewModel: SettingsViewModel) {
    val state = settingsViewModel.showMeState.value
    if (!state.state) return
    ModalBottomSheet(
        onDismissRequest = { settingsViewModel.onShowMeStateChange(false) },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Show me", style = MaterialTheme.typography.titleLarge)
            Text(
                "Who do you want to see on Discover?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Column(Modifier.selectableGroup()) {
                ShowMe.entries.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .selectable(
                                selected = state.showMe == option,
                                role = Role.RadioButton,
                                onClick = { settingsViewModel.selectShowMe(option) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = state.showMe == option, onClick = null)
                        Spacer(Modifier.size(14.dp))
                        Text(option.displayName, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = settingsViewModel::onShowMeChange,
                enabled = !state.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                else Text("Save")
            }
        }
    }
}
