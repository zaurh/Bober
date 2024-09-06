package com.zaurh.bober.screen.settings.components

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
fun DeleteAccountAlert(
    alertState: Boolean,
    countdown: Int,
    enabled: Boolean,
    loading: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    if (alertState) {
        AlertDialog(title = {
            Text(text = "Delete account?")
        }, text = {
            Text(text = "Are you sure you want to delete your account permanently?")
        }, onDismissRequest = {
            onDismiss()
        }, confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
               MaterialTheme.colorScheme.secondary
            ), enabled = enabled, onClick = {
                onDelete()
            }) {
                Text(text = if (countdown > 0) "$countdown" else if (loading) "Loading..." else "Yes, delete.", color = Color.White)
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