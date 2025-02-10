package com.zaurh.bober.data.user

import kotlinx.serialization.Serializable

@Serializable
data class PremiumCardData(
    val name: PremiumCards,
    val takenDate: Long,
    val expiryDate: Long,
    val cancelled: Boolean,
    val received: Boolean,
    val taken: Boolean
)
