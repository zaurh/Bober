package com.zaurh.bober.screen.edit_profile.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DiscardChangesAlert(alertState: Boolean, onDismiss: () -> Unit, onDiscard: () -> Unit) {
    if (!alertState) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Discard changes?") },
        text = { Text("You have unsaved changes to your profile.") },
        confirmButton = { TextButton(onClick = onDiscard) { Text("Discard") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Keep editing") } }
    )
}
