package com.zaurh.bober.screen.edit_profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel

@Composable
fun EP_DeleteImageAlert(
    editProfileViewModel: EditProfileViewModel
) {
    val deleteImageState = editProfileViewModel.deleteImageState.value

    if (deleteImageState.confirmation) {
        AlertDialog(
            title = { Text(text = "Delete image") },
            text = {
                Column {
                    Text(text = "Are you sure you want to delete image?")
                    Spacer(modifier = Modifier.size(12.dp))

                    AnimatedVisibility(visible = deleteImageState.cannotDelete) {

                        Row {
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.warning_ic),
                                contentDescription = ""
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "You need to have at least one image.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                    }
                }
            },
            onDismissRequest = {
                editProfileViewModel.onDeleteImageState("")
            },
            confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(
                    colorResource(id = R.color.backgroundBottom)
                ), onClick = {
                    editProfileViewModel.deleteImage(deleteImageState.selectedImage)
                }) {
                    if (deleteImageState.loading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Loading...", color = Color.White)
                            Spacer(modifier = Modifier.size(4.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        Text(text = "Confirm", color = Color.White)
                    }
                }

            },
            dismissButton = {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.white)
                ), onClick = {
                    editProfileViewModel.onDeleteImageState("")
                }) {
                    Text(text = "No, go back.", color = colorResource(id = R.color.darkBlue))
                }
            }
        )
    }
}