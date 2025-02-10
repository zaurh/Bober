package com.zaurh.bober.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.zaurh.bober.data.user.BackgroundColor
import com.zaurh.bober.screen.chat.ChatScreenViewModel

@Composable
fun ChatBottomBar(
    focusRequester: FocusRequester,
    enabled: Boolean,
    backgroundColor: BackgroundColor,
    chatScreenViewModel: ChatScreenViewModel
) {
    val gifState = chatScreenViewModel.gifState.value
    val placeHolder = if (gifState) "Search gif on Tenor..." else "Type a message..."
    val value =
        if (gifState) chatScreenViewModel.tenorQuery.value else chatScreenViewModel.messageText.value

//    val textLineCount = value.split("\n").size
//    val textFieldHeight = minOf(200.dp + (textLineCount - 1) * 30.dp, 150.dp)

    Column(
        modifier = Modifier.background(if (backgroundColor == BackgroundColor.DEFAULT) MaterialTheme.colorScheme.surface else backgroundColor.color),
        verticalArrangement = Arrangement.Bottom
    ) {
        Chat_SendMessageField(
            placeholder = placeHolder,
            value = value,
            focusRequester = focusRequester,
            onValueChange = if (gifState) chatScreenViewModel::onTenorQueryChange else chatScreenViewModel::onMessageChange,
            onGif = chatScreenViewModel::onGifStateChange,
            gifState = gifState,
            onSend = {
                chatScreenViewModel.sendMessage()
            },
            enabled = enabled
        )
    }
//    BottomAppBar(
////        modifier = Modifier.fillMaxHeight()
//    ) {
//
//
//    }
}