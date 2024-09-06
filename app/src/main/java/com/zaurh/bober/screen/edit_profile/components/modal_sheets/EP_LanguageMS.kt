package com.zaurh.bober.screen.edit_profile.components.modal_sheets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ContextualFlowRowOverflow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.data.user.Languages
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EP_LanguageMS(
    editProfileViewModel: EditProfileViewModel
) {
    val languageState = editProfileViewModel.languageState.value

    if (languageState.modalState) {
        ModalBottomSheet(content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Languages (${languageState.selectedLanguages.count()}/10)",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = editProfileViewModel::onLanguagesStateChange) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = "Select languages you speak", color = Color.Gray)
                Spacer(modifier = Modifier.size(16.dp))
                var maxLines by remember {
                    mutableIntStateOf(4)
                }
                ContextualFlowRow(
                    modifier = Modifier
                        .animateContentSize()
                        .padding(8.dp),
                    maxLines = maxLines,
                    itemCount = Languages.entries.size,
                    overflow = ContextualFlowRowOverflow.expandOrCollapseIndicator(
                        expandIndicator = {
                            TextButton(onClick = { maxLines += 3 }) {
                                Text(text = "${this@expandOrCollapseIndicator.totalItemCount - this@expandOrCollapseIndicator.shownItemCount} + more")
                            }
                        },
                        collapseIndicator = {
                            TextButton(onClick = { maxLines = 4 }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "")
                                Text(text = "Hide")
                            }
                        }
                    )
                ) { index ->
                    val alreadySelected =
                        languageState.selectedLanguages.contains(Languages.entries[index])

                    Button(modifier = Modifier.padding(4.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = if (alreadySelected) MaterialTheme.colorScheme.onTertiary else Color.Gray
                    ), onClick = {
                        editProfileViewModel.onLanguagesChange(Languages.entries[index])
                    }) {
                        val languageText =
                            if (alreadySelected) Languages.entries[index].displayName + " ✔" else Languages.entries[index].displayName

                        Text(text = languageText, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.size(48.dp))
            }
        }, onDismissRequest = editProfileViewModel::onLanguagesStateChange)
    }

}