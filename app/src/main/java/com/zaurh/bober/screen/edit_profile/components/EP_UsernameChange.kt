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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel

@Composable
fun EP_UsernameChange(
    editProfileViewModel: EditProfileViewModel
) {
    val usernameState = editProfileViewModel.usernameState.value
    val currentUser = editProfileViewModel.userDataState.collectAsState()

    if (usernameState.changeState) {
        AlertDialog(
            title = { Text(text = "Change username") },
            text = {
                Column {
                    EP_TextField(
                        title = "Pick yourself a unique username.",
                        placeholder = "Username",
                        value = usernameState.text,
                        onValueChange = editProfileViewModel::onUsernameChange,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    AnimatedVisibility(visible = usernameState.error.isNotEmpty()) {
                        Row {
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.warning_ic),
                                contentDescription = ""
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(text = usernameState.error, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            onDismissRequest = {
                editProfileViewModel.onUsernameChangeState()
                editProfileViewModel.onUsernameChange(username = currentUser.value?.username ?: "")
            },
            confirmButton = {
                val usernameIsSame = usernameState.text == currentUser.value?.username
                Button(enabled = !usernameIsSame,colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = Color.LightGray
                ), onClick = {
                    editProfileViewModel.checkUsername()
                }) {
                    if (usernameState.loading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Checking...", color = Color.White)
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
            })
    }

}