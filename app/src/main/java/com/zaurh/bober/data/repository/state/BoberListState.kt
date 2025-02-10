package com.zaurh.bober.data.repository.state

import com.zaurh.bober.data.user.BoberData

data class BoberListState(
    val boberList: List<BoberData> = listOf(),
    val loading: Boolean = false,
    val error: String = "",
    val empty: Boolean = false
)
