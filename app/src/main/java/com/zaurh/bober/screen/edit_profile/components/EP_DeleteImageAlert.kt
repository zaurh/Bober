package com.zaurh.bober.screen.edit_profile.components

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
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel

@Composable
fun EP_DeleteImageAlert(editProfileViewModel: EditProfileViewModel) {
    val state = editProfileViewModel.deleteImageState.value
    if (!state.confirmation) return
    AlertDialog(
        onDismissRequest = { if (!state.loading) editProfileViewModel.onDeleteImageState("") },
        title = { Text("Remove this photo?") },
        text = {
            Text(
                if (state.cannotDelete) "You need at least one photo on your profile."
                else "It will be removed from your profile right away."
            )
        },
        confirmButton = {
            if (!state.cannotDelete) {
                Button(
                    onClick = { editProfileViewModel.deleteImage(state.selectedImage) },
                    enabled = !state.loading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    if (state.loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onError)
                    else Text("Remove")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { editProfileViewModel.onDeleteImageState("") }, enabled = !state.loading) {
                Text(if (state.cannotDelete) "OK" else "Cancel")
            }
        }
    )
}
