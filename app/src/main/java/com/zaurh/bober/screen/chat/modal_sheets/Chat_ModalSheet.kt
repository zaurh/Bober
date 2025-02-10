@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.bober.screen.chat.modal_sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat_ModalSheet(
    backgroundEnabled: Boolean,
    chatScreenViewModel: ChatScreenViewModel,
    profileViewModel: ProfileViewModel,
    blockedUsersViewModel: BlockedUsersViewModel
) {
    if (chatScreenViewModel.modalSheetState.value) {
        ModalBottomSheet(content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 40.dp)
            ) {
                if (backgroundEnabled){
                    Chat_ModalSheetItem(icon = Icons.Default.Star, title = "Background color") {
                        chatScreenViewModel.closeModalSheet()
                        chatScreenViewModel.openBackgroundModalSheet()
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }

                Chat_ModalSheetItem(icon = Icons.Default.Clear, title = "Unmatch") {
                    chatScreenViewModel.closeModalSheet()
                    profileViewModel.onUnMatchStateChange()
                }
                Spacer(modifier = Modifier.size(8.dp))
                Chat_ModalSheetItem(icon = Icons.Default.Warning, title = "Report") {
                    chatScreenViewModel.closeModalSheet()
                    profileViewModel.onReportStateChange()
                }
                Spacer(modifier = Modifier.size(8.dp))
                Chat_ModalSheetItem(icon = Icons.Default.Lock, title = "Block") {
                    chatScreenViewModel.closeModalSheet()
                    blockedUsersViewModel.onBlockStateChange()
                }

            }
        }, onDismissRequest = {
            chatScreenViewModel.closeModalSheet()
        })
    }
}