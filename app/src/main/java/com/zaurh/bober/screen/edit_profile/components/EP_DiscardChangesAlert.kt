package com.zaurh.bober.screen.edit_profile.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.zaurh.bober.R

@Composable
fun DiscardChangesAlert(
    alertState: Boolean,
    onDismiss: () -> Unit,
    onDiscard: () -> Unit
) {
    if (alertState) {
        AlertDialog(title = {
            Text(text = "Discard changes")
        }, text = {
            Text(text = "Are you sure you want to discard all changes?")
        }, onDismissRequest = {
            onDismiss()
        }, confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.secondary
            ), onClick = {
                onDiscard()
            }) {
                Text(text = "Discard", color = Color.White)
            }
        }, dismissButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.white)
            ), onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel", color = colorResource(id = R.color.darkBlue))
            }
        })
    }
}