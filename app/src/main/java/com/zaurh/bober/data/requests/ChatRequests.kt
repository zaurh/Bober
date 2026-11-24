package com.zaurh.bober.data.requests

import com.zaurh.bober.data.message.EmoteData
import com.zaurh.bober.data.user.ChatBackground
import kotlinx.serialization.Serializable

/** Body of `PUT /chat_background`: the current user's background for their chat with [recipientId]. */
@Serializable
data class ChangeChatBackgroundRequest(
    val recipientId: String,
    val backgroundColor: ChatBackground,
    val doodle: Boolean,
)

/** Body of `PUT /emote_message`. The backend takes the reacting user from the JWT. */
@Serializable
data class EmoteMessageRequest(
    val messageId: String,
    val emoteData: EmoteData,
)

/** Body of `PUT /remove_emote`: removes the current user's reaction from the message. */
@Serializable
data class RemoveEmoteRequest(
    val messageId: String,
)
