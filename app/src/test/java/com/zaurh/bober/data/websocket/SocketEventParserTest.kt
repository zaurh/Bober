package com.zaurh.bober.data.websocket

import com.zaurh.bober.data.message.MessageStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Frames below are copied from what the backend's RoomController sends. */
class SocketEventParserTest {

    private val parser = SocketEventParser()

    private val alice = "11111111-1111-1111-1111-111111111111"
    private val bob = "22222222-2222-2222-2222-222222222222"

    // Backend MessageData encoded with kotlinx: timestamp is a string, plus fields the app doesn't use.
    private val messageJson = """
        {"text":"hi","senderUsername":"alice","senderUserId":"$alice","recipientUserId":"$bob",
         "recipientUsername":"bob","replyingMessageId":"","emoted":[],"timestamp":"1719842400000",
         "status":"DELIVERED","id":"33333333-3333-3333-3333-333333333333"}
    """.trimIndent().replace("\n", "")

    @Test
    fun `chat message json becomes NewMessage`() {
        val event = parser.parse(messageJson) as SocketEvent.NewMessage
        assertEquals("hi", event.message.text)
        assertEquals(alice, event.message.senderUserId)
        assertEquals(1719842400000L, event.message.timestamp)
        assertEquals(MessageStatus.DELIVERED, event.message.status)
    }

    @Test
    fun `message text containing a colon is still a message`() {
        val json = messageJson.replace("\"text\":\"hi\"", "\"text\":\"typing: hello\"")
        val event = parser.parse(json) as SocketEvent.NewMessage
        assertEquals("typing: hello", event.message.text)
    }

    @Test
    fun typing() {
        assertEquals(SocketEvent.Typing(alice), parser.parse("typing:$alice"))
    }

    @Test
    fun delivered() {
        assertEquals(SocketEvent.Delivered(bob), parser.parse("deliveredMessage:$bob"))
    }

    @Test
    fun `read receipt splits reader and other user`() {
        assertEquals(SocketEvent.Read(readerUserId = bob, otherUserId = alice), parser.parse("readMessages:$bob$alice"))
    }

    @Test
    fun presence() {
        assertEquals(
            SocketEvent.Presence(userId = alice, online = true, lastSeen = 1719842400000L),
            parser.parse("triggerOnline:${alice}true1719842400000")
        )
        assertEquals(
            SocketEvent.Presence(userId = bob, online = false, lastSeen = 5L),
            parser.parse("triggerOnline:${bob}false5")
        )
    }

    @Test
    fun `like with json payload`() {
        val frame = """like_sent:{"senderUserId":"$alice","senderImage":["a.jpg"],"senderUsername":"alice","senderAge":25,"recipientUserId":"$bob"}"""
        val event = parser.parse(frame) as SocketEvent.LikeReceived
        assertEquals(alice, event.like.senderUserId)
        assertEquals(25, event.like.senderAge)
    }

    @Test
    fun `match with json payload`() {
        val frame = """got_match:{"senderUserId":"$alice","senderImage":["a.jpg"],"senderUsername":"alice","recipientUserId":"$bob","recipientImage":["b.jpg"],"recipientUsername":"bob"}"""
        val event = parser.parse(frame) as SocketEvent.Matched
        assertEquals(alice, event.match.senderUserId)
        assertEquals(listOf("b.jpg"), event.match.recipientImage)
    }

    @Test
    fun `relationship commands`() {
        val payload = """{"senderUserId":"$alice","recipientUserId":"$bob"}"""
        assertTrue(parser.parse("unlike_sent:$payload") is SocketEvent.LikeRemoved)
        assertTrue(parser.parse("unmatch_sent:$payload") is SocketEvent.Unmatched)
        val blocked = parser.parse("block_sent:$payload") as SocketEvent.Blocked
        assertEquals(bob, blocked.command.otherThan(alice))
        assertEquals(alice, blocked.command.otherThan(bob))
    }

    @Test
    fun `emote frames are not new messages`() {
        val event = parser.parse("sent_emote:$messageJson")
        assertTrue(event is SocketEvent.ReactionChanged)
        assertNull((parser.parse("remove_emote:null") as SocketEvent.ReactionChanged).message)
    }

    @Test
    fun `no longer matched`() {
        assertEquals(SocketEvent.NoLongerMatched, parser.parse("no_longer_matched:You are no longer matched."))
    }

    @Test
    fun `plain text and malformed frames never throw`() {
        assertTrue(parser.parse("No recipient set. Use 'switchRecipient' command.") is SocketEvent.Unknown)
        assertTrue(parser.parse("got_match:not json") is SocketEvent.Unknown)
        assertTrue(parser.parse("{broken") is SocketEvent.Unknown)
        assertTrue(parser.parse("") is SocketEvent.Unknown)
    }
}
