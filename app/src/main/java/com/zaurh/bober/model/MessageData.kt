package com.zaurh.bober.model

import com.zaurh.bober.data.message.EmoteData
import com.zaurh.bober.data.message.MessageStatus

/** A chat message as shown in the UI. */
data class MessageData(
    val id: String? = null,
    val text: String? = null,
    val senderUsername: String? = null,
    val senderUserId: String? = null,
    val recipientUserId: String? = null,
    val recipientUsername: String? = null,
    val timestamp: String? = null,
    val status: MessageStatus = MessageStatus.SENT,
    /** Id of the message this one replies to, if any. */
    val replyingMessageId: String? = null,
    val emoted: List<EmoteData> = emptyList(),
) {
    val isGif: Boolean get() = text.orEmpty().endsWith(".gif")

    /** Short text for previews (reply bar, quoted reply). */
    val preview: String get() = if (isGif) "GIF" else text.orEmpty()
}
