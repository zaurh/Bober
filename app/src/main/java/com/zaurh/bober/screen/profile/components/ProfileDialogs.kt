package com.zaurh.bober.screen.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zaurh.bober.data.report.Reason
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.screen.profile.ProfileDialog
import com.zaurh.bober.screen.profile.ProfileViewModel

/**
 * Unmatch / Block / Report dialogs for [user]. Used by the profile and chat screens.
 * [onRelationshipEnded] runs after a successful unmatch or block (the conversation is gone).
 */
@Composable
fun ProfileDialogs(viewModel: ProfileViewModel, user: UserData?, onRelationshipEnded: () -> Unit) {
    val state = viewModel.state.value
    val userId = user?.id?.takeIf { it.isNotEmpty() } ?: return
    val name = user.name?.ifEmpty { null } ?: user.username.orEmpty()

    when (state.dialog) {
        ProfileDialog.Unmatch -> ConfirmDialog(
            title = "Unmatch $name?",
            text = "Your conversation will be deleted and you won't see each other again unless you match anew.",
            confirm = "Unmatch",
            busy = state.busy,
            onConfirm = { viewModel.unMatch(userId, onRelationshipEnded) },
            onDismiss = viewModel::closeDialog
        )

        ProfileDialog.Block -> ConfirmDialog(
            title = "Block $name?",
            text = "They won't be able to see you or message you. You can unblock them later in Settings.",
            confirm = "Block",
            busy = state.busy,
            onConfirm = { viewModel.block(userId, onRelationshipEnded) },
            onDismiss = viewModel::closeDialog
        )

        ProfileDialog.Report -> ReportDialog(viewModel, userId, name)
        null -> Unit
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    text: String,
    confirm: String,
    busy: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !busy,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onError)
                else Text(confirm)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !busy) { Text("Cancel") } }
    )
}

@Composable
private fun ReportDialog(viewModel: ProfileViewModel, userId: String, name: String) {
    val state = viewModel.state.value
    val form = state.report
    AlertDialog(
        onDismissRequest = { if (!state.busy) viewModel.closeDialog() },
        icon = { Icon(Icons.Default.Warning, contentDescription = null) },
        title = { Text("Report $name") },
        text = {
            Column {
                Text("What's wrong? $name won't know you reported them.")
                Spacer(Modifier.height(8.dp))
                Column(Modifier.selectableGroup()) {
                    Reason.entries.filter { it != Reason.NOT_SELECTED }.forEach { reason ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .selectable(
                                    selected = form.reason == reason,
                                    role = Role.RadioButton,
                                    onClick = { viewModel.selectReason(reason) }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = form.reason == reason, onClick = null)
                            Spacer(Modifier.size(12.dp))
                            Text(reason.displayName, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.details,
                    onValueChange = viewModel::onReportDetailsChange,
                    label = { Text("Details (optional)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.report(userId) },
                enabled = form.reason != null && !state.busy,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                if (state.busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onError)
                else Text("Report")
            }
        },
        dismissButton = { TextButton(onClick = viewModel::closeDialog, enabled = !state.busy) { Text("Cancel") } }
    )
}
