package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class BlockedUsersResponse(
    val success: Boolean,
    val blockList: List<BlockedUserData> = listOf(),
    val message: String = "No message.",
)

@Serializable
data class BlockedUserData(
    val id: String? = null,
    val image: String? = null,
    val username: String? = null,
    val name: String? = null
)