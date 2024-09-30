package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class LikedUsersResponse(
    val success: Boolean,
    val likedUserList: List<LikedUserData> = listOf(),
    val message: String = "No message.",
)

@Serializable
data class LikedUserData(
    val id: String? = null,
    val image: String? = null,
    val username: String? = null,
    val age: String? = null
)