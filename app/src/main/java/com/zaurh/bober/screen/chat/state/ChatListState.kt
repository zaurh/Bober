package com.zaurh.bober.screen.chat.state

import com.zaurh.bober.data.user.ChatData

data class ChatListState(
    val chatList: List<ChatData> = listOf(),
    val loading: Boolean = false,
    val error: String = "",
    val empty: Boolean = false
)
