package com.zaurh.bober.screen.edit_profile.components.modal_sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EP_HeightMS(
    editProfileViewModel: EditProfileViewModel
) {
    val heightState = editProfileViewModel.heightState.value
    val heightTextState = if (editProfileViewModel.heightTextState.value == "0") "" else editProfileViewModel.heightTextState.value
    val focusRequester = remember { FocusRequester() }

    if (heightState) {
        LaunchedEffect(key1 = heightTextState) {
            focusRequester.requestFocus()

        }
        ModalBottomSheet(content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Height",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = {
                        editProfileViewModel.onHeightStateChange(false)
                    }) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = "Add your height to your profile", color = Color.Gray)
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "Centimeters", fontSize = 12.sp, color = Color.Gray)
                TextField(
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        selectionColors = TextSelectionColors(
                            handleColor = MaterialTheme.colorScheme.onSecondary,
                            backgroundColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = { Text(text = "Add your height") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    value = heightTextState,
                    onValueChange = editProfileViewModel::onHeightTextChange,
                    singleLine = true,
                    maxLines = 1
                )

            }
        }, onDismissRequest = {
            editProfileViewModel.onHeightStateChange(false)
        })
    }
}