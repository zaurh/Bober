package com.zaurh.bober.data.user

import kotlinx.serialization.Serializable

@Serializable
data class MatchData(
    val matchUserId: String,
    val new: Boolean
)