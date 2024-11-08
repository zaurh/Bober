package com.zaurh.bober.screen.liked_users.state

import com.zaurh.bober.data.responses.LikedUserData

data class LikedUsersState(
    val likedUsers: List<LikedUserData> = listOf(),
    val loading: Boolean = false,
    val error: String = "",
    val empty: Boolean = false
)
