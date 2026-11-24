package com.zaurh.bober.data.user

import kotlinx.serialization.Serializable

/** One entry of the current user's chat list, including their personal settings for that chat. */
@Serializable
data class ChatData(
    val recipientId: String,
    val recipientImage: String,
    val recipientUsername: String,
    val lastUpdated: Long,
    val backgroundColor: ChatBackground = ChatBackground.DEFAULT,
    val doodleEnabled: Boolean = false,
)

/**
 * Chat background colors. Names mirror the backend's `BackgroundColor` enum (that's what is stored);
 * the actual colors live in the UI layer (`ui/theme/ChatBackgrounds.kt`).
 */
@Serializable
enum class ChatBackground {
    DEFAULT,
    PURPLE, PURPLE_LIGHT, PURPLE_DARK, LAVENDER, VIOLET,
    RED, RED_LIGHT, RED_DARK, PINK, CRIMSON,
    YELLOW, YELLOW_LIGHT, YELLOW_DARK, GOLD, ORANGE,
    BLUE, BLUE_LIGHT, BLUE_DARK, SKY_BLUE, NAVY,
    GREEN, GREEN_LIGHT, GREEN_DARK, MINT, LIME,
    GRAY, GRAY_LIGHT, GRAY_DARK, SILVER, CHARCOAL,
    BROWN, BROWN_LIGHT, BROWN_DARK, TAN, BEIGE,
    WHITE, BLACK, IVORY, SNOW, COAL,
}
