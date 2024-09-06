package com.zaurh.bober.screen.profile.components

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
fun Profile_ConfirmationAlert(
    title: String,
    text: String,
    confirmText: String,
    alertState: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (alertState) {
        AlertDialog(title = {
            Text(text = title)
        }, text = {
            Text(text = text)
        }, onDismissRequest = {
            onDismiss()
        }, confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ), onClick = {
                onConfirm()
            }) {
                Text(text = confirmText, color = Color.White)
            }
        }, dismissButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.white)
            ), onClick = {
                onDismiss()
            }) {
                Text(text = "No, go back.", color = colorResource(id = R.color.darkBlue))
            }
        })
    }
}

