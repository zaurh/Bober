package com.zaurh.bober.screen.edit_profile.components.modal_sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EP_GenderMS(
    editProfileViewModel: EditProfileViewModel
) {
    if (editProfileViewModel.genderState.value) {
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
                        text = "Gender",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = {
                        editProfileViewModel.onGenderStateChange(false)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = "Select your gender", color = Color.Gray)
                Spacer(modifier = Modifier.size(16.dp))
                Gender.entries.forEach {
                    Text(
                        text = if (editProfileViewModel.gender.value == it) it.displayName + " ✔" else it.displayName,
                        modifier = Modifier
                            .clickable {
                                editProfileViewModel.onGenderSelect(it)
                            }
                            .padding(8.dp)
                            .fillMaxWidth(),
                        color = if (editProfileViewModel.gender.value == it) MaterialTheme.colorScheme.onSecondary else Color.Gray,
                        fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.size(48.dp))

            }
        }, onDismissRequest = {
            editProfileViewModel.onGenderStateChange(false)
        })
    }

}
