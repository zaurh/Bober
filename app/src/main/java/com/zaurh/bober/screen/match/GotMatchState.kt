package com.zaurh.bober.screen.match

data class GotMatchState(
    val gotMatch: Boolean = false,
    val recipientId: String = "",
    val recipientImage: String = "",
    val recipientUsername: String = "",
)
