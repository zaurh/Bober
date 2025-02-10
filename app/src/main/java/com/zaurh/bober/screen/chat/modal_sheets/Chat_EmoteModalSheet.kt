package com.zaurh.bober.screen.chat.modal_sheets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaurh.bober.data.message.EmoteData
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.chat.modal_sheets.components.Chat_EmoteModalItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat_EmoteModalSheet(
    chatScreenViewModel: ChatScreenViewModel,
    enabled: Boolean,
    currentUserId: String,
    onClick: (EmoteData) -> Unit,
    onDismiss: () -> Unit
) {
    val emoteModalState = chatScreenViewModel.emoteModalState.value

    if (enabled) {
        ModalBottomSheet(content = {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 40.dp)
            ) {
                val emoteList = emoteModalState.messageData?.emoted?.sortedByDescending { it.senderId == currentUserId } ?: listOf()
                items(emoteList){
                    Chat_EmoteModalItem(
                        profilePic = it.senderImage,
                        username = it.senderUsername,
                        emoteSealed = it.emoteSealed,
                        onClick = {
                           onClick(it)
                        },
                        senderIsMyself = it.senderId == currentUserId
                    )
                }
            }


        }, onDismissRequest = {
            onDismiss()
        })
    }
}