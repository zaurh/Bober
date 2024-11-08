package com.zaurh.bober.screen.chat.state

import com.zaurh.bober.data.responses.MatchedUserData

data class MatchListState(
    val matchList: List<MatchedUserData> = listOf(),
    val loading: Boolean = false,
    val error: String = "",
    val empty: Boolean = false
)
