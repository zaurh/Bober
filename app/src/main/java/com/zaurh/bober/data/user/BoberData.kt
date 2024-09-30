package com.zaurh.bober.data.user

import kotlinx.serialization.Serializable

@Serializable
data class BoberData(
    val userId: String,
    val image: List<String>,
    val distance: Int,
    val username: String,
    val name: String,
    val jobTitle: String,
    val age: Int,
    val aboutMe: String
)
