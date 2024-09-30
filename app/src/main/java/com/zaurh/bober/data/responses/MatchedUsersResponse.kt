package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class MatchedUserResponse(
    val success: Boolean,
    val matchedUserList: List<MatchedUserData> = listOf(),
    val message: String = "No message.",
)

@Serializable
data class MatchedUserData(
    val id: String,
    val image: String,
    val username: String,
    val name: String,
    val new: Boolean
)