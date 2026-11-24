package com.zaurh.bober.screen.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R

/** Sign-in → "Forgot password?": sends a reset link to the account's verified email. */
@Composable
fun ForgotPasswordDialog(viewModel: PasswordViewModel) {
    val state = viewModel.forgot.value
    if (!state.open) return
    AlertDialog(
        onDismissRequest = viewModel::closeForgot,
        icon = { Icon(Icons.Default.Lock, contentDescription = null) },
        title = { Text("Reset password") },
        text = {
            Column {
                Text(
                    if (state.sent) "Open the link on this phone to choose a new password."
                    else "Enter the email you verified in Bober. We'll send you a link to choose a new password."
                )
                if (!state.sent) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onForgotEmailChange,
                        singleLine = true,
                        enabled = !state.sending,
                        placeholder = { Text("you@example.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.sendResetLink() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Message(state.message, isError = !state.sent)
            }
        },
        confirmButton = {
            if (state.sent) {
                TextButton(onClick = viewModel::closeForgot) { Text("Done") }
            } else {
                ProgressButton("Send link", loading = state.sending, enabled = state.email.isNotBlank(), onClick = viewModel::sendResetLink)
            }
        },
        dismissButton = {
            if (!state.sent) TextButton(onClick = viewModel::closeForgot) { Text("Cancel") }
        }
    )
}

/** Opened from the reset email's link: choose a new password with the link's token. */
@Composable
fun ResetPasswordDialog(viewModel: PasswordViewModel, onDone: () -> Unit = {}) {
    val state = viewModel.reset.value
    if (state.token == null) return
    val close = {
        viewModel.closeReset()
        if (state.done) onDone()
    }
    AlertDialog(
        onDismissRequest = { if (!state.saving) close() },
        icon = { Icon(Icons.Default.Lock, contentDescription = null) },
        title = {
            Text(
                when {
                    state.checking -> "Checking link…"
                    state.done -> "Password changed"
                    !state.tokenValid -> "Link expired"
                    else -> "Choose a new password"
                }
            )
        },
        text = {
            Column {
                when {
                    state.checking -> CircularProgressIndicator()
                    state.done -> Text("You can now sign in with your new password.")
                    !state.tokenValid -> Text(state.message.orEmpty())
                    else -> {
                        PasswordInput(state.password, viewModel::onResetPasswordChange, "New password", enabled = !state.saving)
                        Spacer(Modifier.height(8.dp))
                        PasswordInput(state.confirm, viewModel::onResetConfirmChange, "Repeat new password", enabled = !state.saving, last = true, onDone = viewModel::saveResetPassword)
                        Message(state.message ?: newPasswordError(state.password, state.confirm).takeIf { state.password.isNotEmpty() }, isError = true)
                    }
                }
            }
        },
        confirmButton = {
            when {
                state.checking -> Unit
                state.done || !state.tokenValid -> TextButton(onClick = close) { Text("OK") }
                else -> ProgressButton(
                    "Save",
                    loading = state.saving,
                    enabled = state.password.isNotEmpty() && state.confirm.isNotEmpty(),
                    onClick = viewModel::saveResetPassword
                )
            }
        },
        dismissButton = {
            if (!state.checking && !state.done && state.tokenValid) TextButton(onClick = close) { Text("Cancel") }
        }
    )
}

/** Settings → Change password. */
@Composable
fun ChangePasswordDialog(viewModel: PasswordViewModel) {
    val state = viewModel.change.value
    if (!state.open) return
    AlertDialog(
        onDismissRequest = { if (!state.saving) viewModel.closeChange() },
        icon = { Icon(Icons.Default.Lock, contentDescription = null) },
        title = { Text(if (state.done) "Password changed" else "Change password") },
        text = {
            Column {
                if (state.done) {
                    Text("Use your new password next time you sign in.")
                } else {
                    PasswordInput(state.current, viewModel::onCurrentChange, "Current password", enabled = !state.saving)
                    Spacer(Modifier.height(8.dp))
                    PasswordInput(state.password, viewModel::onChangePasswordChange, "New password", enabled = !state.saving)
                    Spacer(Modifier.height(8.dp))
                    PasswordInput(state.confirm, viewModel::onChangeConfirmChange, "Repeat new password", enabled = !state.saving, last = true, onDone = viewModel::saveChangedPassword)
                    Message(state.message ?: newPasswordError(state.password, state.confirm).takeIf { state.password.isNotEmpty() }, isError = true)
                }
            }
        },
        confirmButton = {
            if (state.done) {
                TextButton(onClick = viewModel::closeChange) { Text("Done") }
            } else {
                ProgressButton(
                    "Save",
                    loading = state.saving,
                    enabled = state.current.isNotEmpty() && state.password.isNotEmpty() && state.confirm.isNotEmpty(),
                    onClick = viewModel::saveChangedPassword
                )
            }
        },
        dismissButton = {
            if (!state.done) TextButton(onClick = viewModel::closeChange, enabled = !state.saving) { Text("Cancel") }
        }
    )
}

@Composable
private fun PasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    last: Boolean = false,
    onDone: () -> Unit = {},
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = if (last) ImeAction.Done else ImeAction.Next),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    painterResource(if (visible) R.drawable.visibilityon_ic else R.drawable.visibilityoff_ic),
                    contentDescription = if (visible) "Hide password" else "Show password",
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun Message(message: String?, isError: Boolean) {
    if (message.isNullOrEmpty()) return
    Spacer(Modifier.height(8.dp))
    Text(message, color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
}

@Composable
private fun ProgressButton(text: String, loading: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled && !loading) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(text)
        }
    }
}
