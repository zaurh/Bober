package com.zaurh.bober.screen.settings.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Permanent account deletion. The button unlocks after a short countdown so it can't be hit by accident. */
@Composable
fun DeleteAccountAlert(
    alertState: Boolean,
    countdown: Int,
    enabled: Boolean,
    loading: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    if (!alertState) return
    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text("Delete your account?") },
        text = { Text("Your profile, matches and messages will be deleted permanently. This can't be undone.") },
        confirmButton = {
            Button(
                onClick = onDelete,
                enabled = enabled && !loading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                when {
                    loading -> CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onError)
                    countdown > 0 -> Text("Delete ($countdown)")
                    else -> Text("Delete")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !loading) { Text("Cancel") } }
    )
}
