package com.zaurh.bober.data.user

import kotlinx.serialization.Serializable

/** Mirrors the backend's `PremiumCards` enum. */
@Serializable
enum class PremiumCards(val displayName: String) {
    NONE(displayName = "None"),
    BOBERIUM(displayName = "Boberium"),
    TITANIUM(displayName = "Titanium")
}

@Serializable
data class PremiumCardData(
    val name: PremiumCards = PremiumCards.NONE,
    val takenDate: Long = 0,
    val expiryDate: Long = 0,
    val cancelled: Boolean = false,
    /** Card was granted (e.g. the welcome card) but not activated yet. */
    val received: Boolean = false,
    /** Card was activated; it stays active until [expiryDate]. */
    val taken: Boolean = false,
) {
    fun isActive(now: Long = System.currentTimeMillis()): Boolean = taken && now < expiryDate
}
