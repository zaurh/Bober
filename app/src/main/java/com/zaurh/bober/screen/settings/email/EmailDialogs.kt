package com.zaurh.bober.screen.settings.email

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/** Lets the user enter an email address and have a verification link sent to it. */
@Composable
fun EmailVerificationDialog(
    state: EmailDialogState,
    verifiedEmail: String?,
    onEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (!state.open) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Email") },
        text = {
            Column {
                Text(
                    text = if (verifiedEmail.isNullOrEmpty()) {
                        "We'll send you a link. Open it on this phone to verify your email."
                    } else {
                        "$verifiedEmail is verified. Enter a new address to change it."
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    onValueChange = onEmailChange,
                    singleLine = true,
                    enabled = !state.sending,
                    placeholder = { Text(text = "you@example.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Send),
                )
                state.message?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = if (state.sent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                enabled = !state.sending && state.email.isNotBlank(),
                onClick = onSend
            ) {
                if (state.sending) {
                    CircularProgressIndicator(modifier = Modifier.height(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(text = if (state.sent) "Send again" else "Send link", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = if (state.sent) "Done" else "Cancel") }
        }
    )
}

/** Shows progress and the result after the user opens the verification link. */
@Composable
fun EmailLinkResultDialog(state: EmailLinkState, onDismiss: () -> Unit) {
    if (!state.verifying && state.message == null) return
    AlertDialog(
        onDismissRequest = { if (!state.verifying) onDismiss() },
        title = {
            Text(
                text = when {
                    state.verifying -> "Verifying email…"
                    state.success -> "Email verified"
                    else -> "Couldn't verify email"
                }
            )
        },
        text = {
            if (state.verifying) CircularProgressIndicator() else Text(text = state.message.orEmpty())
        },
        confirmButton = {
            if (!state.verifying) TextButton(onClick = onDismiss) { Text(text = "OK") }
        }
    )
}
