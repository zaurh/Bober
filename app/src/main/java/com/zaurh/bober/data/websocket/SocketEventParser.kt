package com.zaurh.bober.data.websocket

import com.zaurh.bober.data.message.MessageDataDto
import com.zaurh.bober.data.remote.NetworkJson
import kotlinx.serialization.json.Json

/**
 * Parses chat socket frames. Never throws: a frame this app version doesn't understand becomes
 * [SocketEvent.Unknown] instead of tearing down the message stream.
 */
class SocketEventParser(private val json: Json = NetworkJson) {

    fun parse(frame: String): SocketEvent = runCatching { parseOrThrow(frame) }
        .getOrElse { SocketEvent.Unknown(frame) }

    private fun parseOrThrow(frame: String): SocketEvent {
        val (prefix, body) = splitPrefix(frame) ?: return parseMessage(frame)

        return when (prefix) {
            "typing" -> SocketEvent.Typing(senderUserId = body)
            "deliveredMessage" -> SocketEvent.Delivered(recipientUserId = body)
            "readMessages" -> parseRead(body)
            "triggerOnline" -> parsePresence(body)
            "like_sent" -> SocketEvent.LikeReceived(json.decodeFromString(body))
            "unlike_sent" -> SocketEvent.LikeRemoved(json.decodeFromString(body))
            "got_match" -> SocketEvent.Matched(json.decodeFromString(body))
            "unmatch_sent" -> SocketEvent.Unmatched(json.decodeFromString(body))
            "block_sent" -> SocketEvent.Blocked(json.decodeFromString(body))
            "sent_emote", "remove_emote" ->
                SocketEvent.ReactionChanged(json.decodeFromString<MessageDataDto?>(body))
            "no_longer_matched" -> SocketEvent.NoLongerMatched
            else -> SocketEvent.Unknown(frame)
        }
    }

    /** Chat messages are bare JSON objects; command frames are `<command>:<payload>`. */
    private fun splitPrefix(frame: String): Pair<String, String>? {
        if (frame.startsWith("{")) return null
        val separator = frame.indexOf(':')
        if (separator <= 0) return null
        val prefix = frame.substring(0, separator)
        if (prefix !in KNOWN_COMMANDS) return null
        return prefix to frame.substring(separator + 1)
    }

    private fun parseMessage(frame: String): SocketEvent {
        if (!frame.startsWith("{")) return SocketEvent.Unknown(frame)
        val message = json.decodeFromString<MessageDataDto>(frame)
        return if (message.senderUserId.isEmpty()) SocketEvent.Unknown(frame) else SocketEvent.NewMessage(message)
    }

    /** `<readerId><otherId>`: two user ids (UUIDs) concatenated. */
    private fun parseRead(body: String): SocketEvent {
        require(body.length % 2 == 0) { "Unexpected readMessages payload" }
        val half = body.length / 2
        return SocketEvent.Read(readerUserId = body.substring(0, half), otherUserId = body.substring(half))
    }

    /** `<userId><true|false><lastSeen>`. User ids are UUIDs, which can't contain "true"/"false". */
    private fun parsePresence(body: String): SocketEvent {
        val match = PRESENCE_REGEX.matchEntire(body) ?: error("Unexpected triggerOnline payload")
        val (userId, online, lastSeen) = match.destructured
        return SocketEvent.Presence(userId = userId, online = online.toBoolean(), lastSeen = lastSeen.toLong())
    }

    private companion object {
        val PRESENCE_REGEX = Regex("^(.+?)(true|false)(\\d+)$")
        val KNOWN_COMMANDS = setOf(
            "typing", "deliveredMessage", "readMessages", "triggerOnline", "like_sent", "unlike_sent",
            "got_match", "unmatch_sent", "block_sent", "sent_emote", "remove_emote", "no_longer_matched",
        )
    }
}
