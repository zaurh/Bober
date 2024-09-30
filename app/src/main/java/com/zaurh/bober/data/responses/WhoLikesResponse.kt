package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class WhoLikesResponse(
    val success: Boolean,
    val whoLikesList: List<WhoLikesData> = listOf(),
    val message: String = "No message.",
)

@Serializable
data class WhoLikesData(
    val id: String,
    val image: String,
    val username: String,
    val age: String
)