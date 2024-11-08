package com.zaurh.bober.screen.blocked_users.state

import com.zaurh.bober.data.responses.BlockedUserData

data class BlockedUsersState(
    val blockList: List<BlockedUserData> = listOf(),
    val loading: Boolean = false,
    val error: String = "",
    val empty: Boolean = false
)
