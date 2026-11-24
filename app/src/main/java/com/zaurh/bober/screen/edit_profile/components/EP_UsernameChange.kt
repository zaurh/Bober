package com.zaurh.bober.screen.edit_profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel

/** Username changes are saved on their own (they need an availability check). */
@Composable
fun EP_UsernameChange(editProfileViewModel: EditProfileViewModel) {
    val state = editProfileViewModel.usernameState.value
    val current = editProfileViewModel.userDataState.collectAsState().value?.username.orEmpty()
    if (!state.changeState) return
    val close = {
        editProfileViewModel.onUsernameChangeState()
        editProfileViewModel.onUsernameChange(current)
    }
    AlertDialog(
        onDismissRequest = { if (!state.loading) close() },
        title = { Text("Change username") },
        text = {
            Column {
                Text("Pick a unique username. People can find you by it.")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.text,
                    onValueChange = editProfileViewModel::onUsernameChange,
                    prefix = { Text("@") },
                    singleLine = true,
                    isError = state.error.isNotEmpty(),
                    supportingText = state.error.takeIf { it.isNotEmpty() }?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = editProfileViewModel::checkUsername,
                enabled = !state.loading && state.text.isNotBlank() && state.text != current
            ) {
                if (state.loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                else Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = close, enabled = !state.loading) { Text("Cancel") } }
    )
}
