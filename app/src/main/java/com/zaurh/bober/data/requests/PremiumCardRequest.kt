package com.zaurh.bober.data.requests

import com.zaurh.bober.data.user.PremiumCards
import kotlinx.serialization.Serializable

/** Body of `PUT /premium`. */
@Serializable
data class PremiumCardRequest(
    val premiumCardName: PremiumCards
)
