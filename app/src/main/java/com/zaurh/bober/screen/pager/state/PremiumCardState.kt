package com.zaurh.bober.screen.pager.state

data class PremiumCardState(
    val congsText: Boolean = false,
    val expiryDate: Int = 30,
    val alertState: Boolean = false,
    val loading: Boolean = false,
    val error: String = ""
)
