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
import com.zaurh.bober.ui.theme.outfitFamily

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
            Text(text = title, fontFamily = outfitFamily)
        }, text = {
            Text(text = text, fontFamily = outfitFamily)
        }, onDismissRequest = {
            onDismiss()
        }, confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ), onClick = {
                onConfirm()
            }) {
                Text(text = confirmText, color = Color.White, fontFamily = outfitFamily)
            }
        }, dismissButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.white)
            ), onClick = {
                onDismiss()
            }) {
                Text(text = "No, go back.", color = colorResource(id = R.color.darkBlue), fontFamily = outfitFamily)
            }
        })
    }
}

