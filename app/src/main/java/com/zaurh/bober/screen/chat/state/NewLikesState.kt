package com.zaurh.bober.screen.chat.state

import com.zaurh.bober.data.responses.WhoLikesData

data class NewLikesState(
    val newLikes: List<WhoLikesData> = listOf(),
    val size: Int = 0,
    val loading: Boolean = false,
    val error: String = "",
    val empty: Boolean = false
)
