package com.zaurh.bober.data.message

import kotlinx.serialization.Serializable

/** A message reaction. Mirrors the backend's `EmoteSealed`; [displayName] is the emoji shown. */
@Serializable
enum class EmoteSealed(val displayName: String) {
    NOTHING(""),
    THUMB("👍"),
    HEART("❤️"),
    LAUGH("😂"),
    CRY("😢"),
    ANGRY("😡");

    companion object {
        /** The reactions a user can pick (everything but [NOTHING]). */
        val pickable: List<EmoteSealed> = entries.filter { it != NOTHING }
    }
}

/** One user's reaction on a message. */
@Serializable
data class EmoteData(
    val emoteSealed: EmoteSealed = EmoteSealed.NOTHING,
    val senderId: String = "",
    val senderUsername: String = "",
    val senderImage: String = "",
)
