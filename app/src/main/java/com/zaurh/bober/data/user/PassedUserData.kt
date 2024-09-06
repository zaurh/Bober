package com.zaurh.bober.data.user

import com.zaurh.bober.util.DateSerializer
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class PassedUserData(
    val userId: String,
    @Serializable(with = DateSerializer::class) val addedAt: Date
)
