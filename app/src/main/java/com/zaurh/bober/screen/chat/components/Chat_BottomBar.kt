package com.zaurh.bober.screen.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zaurh.bober.screen.chat.ChatScreenViewModel

@Composable
fun ChatBottomBar(
    modifier: Modifier = Modifier,
    chatScreenViewModel: ChatScreenViewModel
) {
    val gifState = chatScreenViewModel.gifState.value
    val placeHolder = if (gifState) "Search gif on Tenor..." else "Type a message..."
    val value =
        if (gifState) chatScreenViewModel.tenorQuery.value else chatScreenViewModel.messageText.value


    BottomAppBar {
        Column(
            modifier
                .fillMaxSize()
        ) {
            Chat_SendMessageField(
                placeholder = placeHolder,
                value = value,
                onValueChange = if (gifState) chatScreenViewModel::onTenorQueryChange else chatScreenViewModel::onMessageChange,
                onGif = chatScreenViewModel::onGifStateChange,
                gifState = gifState,
                onSend = {
                    chatScreenViewModel.sendMessage()
                }
            )
        }
    }
}