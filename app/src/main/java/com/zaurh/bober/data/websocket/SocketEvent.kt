package com.zaurh.bober.data.websocket

import com.zaurh.bober.data.message.MessageDataDto
import kotlinx.serialization.Serializable

/** Everything the backend's chat socket (`/chat-socket`, see `RoomController`) can push to the app. */
sealed interface SocketEvent {
    /** A new chat message. Also echoed back to the sender. */
    data class NewMessage(val message: MessageDataDto) : SocketEvent

    /** `typing:<senderId>` */
    data class Typing(val senderUserId: String) : SocketEvent

    /** `deliveredMessage:<userId>`: messages sent to [recipientUserId] reached their device. */
    data class Delivered(val recipientUserId: String) : SocketEvent

    /** `readMessages:<readerId><otherId>`: [readerUserId] read every message [otherUserId] sent them. */
    data class Read(val readerUserId: String, val otherUserId: String) : SocketEvent

    /** `triggerOnline:<userId><true|false><lastSeenMillis>` */
    data class Presence(val userId: String, val online: Boolean, val lastSeen: Long) : SocketEvent

    /** `like_sent:{json}`: someone liked the current user. */
    data class LikeReceived(val like: NewLikeCommand) : SocketEvent

    /** `unlike_sent:{json}` */
    data class LikeRemoved(val command: RelationshipCommand) : SocketEvent

    /** `got_match:{json}`: sent to both users of a new match. */
    data class Matched(val match: GotMatchCommand) : SocketEvent

    /** `unmatch_sent:{json}`: sent to both users. */
    data class Unmatched(val command: RelationshipCommand) : SocketEvent

    /** `block_sent:{json}`: sent to both users. */
    data class Blocked(val command: RelationshipCommand) : SocketEvent

    /** `sent_emote:` / `remove_emote:` with the updated message. Not a new message. */
    data class ReactionChanged(val message: MessageDataDto?) : SocketEvent

    /** `no_longer_matched:`: the last message was rejected because the users aren't matched anymore. */
    data object NoLongerMatched : SocketEvent

    /** Plain-text notices (e.g. "No recipient set...") and anything this app version doesn't know. */
    data class Unknown(val raw: String) : SocketEvent
}

@Serializable
data class NewLikeCommand(
    val senderUserId: String = "",
    val senderImage: List<String> = listOf(),
    val senderUsername: String = "",
    val senderAge: Int = 0,
    val recipientUserId: String = "",
)

@Serializable
data class GotMatchCommand(
    val senderUserId: String = "",
    val senderImage: List<String> = listOf(),
    val senderUsername: String = "",
    val recipientUserId: String = "",
    val recipientImage: List<String> = listOf(),
    val recipientUsername: String = "",
)

/** Shared shape of the backend's `UnlikeCommand`, `UnMatchCommand` and `BlockCommand`. */
@Serializable
data class RelationshipCommand(
    val senderUserId: String = "",
    val recipientUserId: String = "",
) {
    /** The user on the other side of the relationship from [currentUserId]. */
    fun otherThan(currentUserId: String?): String =
        if (senderUserId == currentUserId) recipientUserId else senderUserId
}
