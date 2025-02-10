package com.zaurh.bober.data.requests

import com.zaurh.bober.data.user.PremiumCards
import kotlinx.serialization.Serializable

@Serializable
data class PremiumCardRequest(
    val premiumCardName: PremiumCards
)
