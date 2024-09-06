@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.bober.screen.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.data.report.Reason
import com.zaurh.bober.screen.profile.ProfileViewModel

@Composable
fun Profile_ReportAlert(
    profileViewModel: ProfileViewModel,
) {
    val profileData = profileViewModel.profileDataState.collectAsState()
    val profileUsername = profileData.value?.username

    val reportState = profileViewModel.reportState.value.reportState
    val dropdownState = profileViewModel.reportState.value.reasonDropdown
    val selectedReason = profileViewModel.reportState.value.selectedReason.displayName
    val optionalReason = profileViewModel.reportState.value.optionalReason
    val loading = profileViewModel.reportState.value.loading

    val focus = LocalFocusManager.current
    val context = LocalContext.current


    if (reportState) {
        AlertDialog(
            title = { Text(text = "Report reason") },
            text = {
                ExposedDropdownMenuBox(
                    expanded = dropdownState,
                    onExpandedChange = { profileViewModel.onDropdownChange() },
                ) {
                    Column {
                        Column {
                            Text(text = "Select report reason:")
                            Spacer(modifier = Modifier.size(8.dp))
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(30))
                                    .clickable {
                                        focus.clearFocus()
                                    }
                                    .menuAnchor(),
                                value = selectedReason,
                                onValueChange = {},
                                enabled = false,
                                colors = TextFieldDefaults.colors(
                                    disabledContainerColor = Color.White,
                                    disabledTextColor = colorResource(id = R.color.darkBlue),
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                trailingIcon = {
                                    Icon(
                                        modifier = Modifier.rotate(if (dropdownState) 180f else 0f),
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "",
                                        tint = colorResource(id = R.color.darkBlue)
                                    )
                                },
                                textStyle = TextStyle.Default.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            ExposedDropdownMenu(
                                modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
                                expanded = dropdownState,
                                onDismissRequest = profileViewModel::onDropdownChange,
                            ) {
                                Reason.entries.forEach { reason ->
                                    DropdownMenuItem(
                                        text = { Text(reason.displayName, color = Color.White) },
                                        onClick = {
                                            profileViewModel.selectReason(reason)
                                            profileViewModel.onDropdownChange()
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Optional reason:")
                        Spacer(modifier = Modifier.size(8.dp))
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(30)),
                            placeholder = { Text(text = "Write here")},
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                focusedTextColor = colorResource(id = R.color.darkBlue),
                                unfocusedTextColor = colorResource(id = R.color.darkBlue),
                                focusedPlaceholderColor = colorResource(id = R.color.darkBlue),
                                unfocusedPlaceholderColor = colorResource(id = R.color.darkBlue),
                                selectionColors = TextSelectionColors(
                                    handleColor = Color.Gray,
                                    backgroundColor = Color.Gray
                                ),
                                cursorColor = colorResource(R.color.darkBlue)
                            ),
                            value = optionalReason,
                            onValueChange = profileViewModel::onOptionalChange
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "* Don't worry, $profileUsername won't know about it.", color = Color.Gray)
                    }
                }

            },
            onDismissRequest = {
                profileViewModel.onReportStateChange()
            },
            confirmButton = {
                Button(enabled = !loading,colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),onClick = {
                    profileViewModel.report(
                        recipientId = profileData.value?.id ?: "",
                        context = context
                    )
                }) {
                    Text(text = if (loading) "Loading..." else "Report", color = Color.White)
                }
            },
            dismissButton = {
                Button(colors = ButtonDefaults.buttonColors(
                    Color.White
                ),onClick = {
                    profileViewModel.onReportStateChange()
                }) {
                    Text(text = "Close", color = colorResource(id = R.color.darkBlue))
                }
            }
        )
    }

}