package com.zaurh.bober.screen.settings

data class DeleteAccountState(
    val alert: Boolean = false,
    val countdown: Int = 5,
    val enabled: Boolean = false,
    val loading: Boolean = false
)
