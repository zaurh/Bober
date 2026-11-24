package com.zaurh.bober.data.remote

import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.responses.GetUserDataResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.user.PremiumCards
import com.zaurh.bober.data.user.UserUpdate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Response bodies in the exact shape the Ktor backend produces. */
class BackendJsonTest {

    @Test
    fun `get_user_data with backend-only fields decodes`() {
        val future = System.currentTimeMillis() + 86_400_000
        val body = """
            {"success":true,"message":"Retrieved successfully.","user":{
              "id":"u1","googleId":"","fcmToken":"t","password":"","salt":"","passRecoverToken":"",
              "username":"bober1","email":"a@b.c","emailIsVerified":true,"gender":"MAN","showMe":"EVERYONE",
              "interests":["MUSIC"],"languages":["ENGLISH"],"imageUrl":["x.jpg"],
              "gotLiked":[{"id":"u2","image":"y.jpg","username":"bob","age":"25","new":true}],
              "passedUsers":[{"userId":"u3","addedAt":"2024-07-08T08:53:28+0200"}],
              "matchList":[{"id":"u4","image":"z.jpg","username":"carol","name":"Carol","new":false}],
              "chatList":[{"recipientId":"u4","recipientImage":"z.jpg","recipientUsername":"carol",
                           "backgroundColor":"DEFAULT","doodleEnabled":false,"lastUpdated":1}],
              "premiumCards":[{"name":"BOBERIUM","takenDate":1,"expiryDate":$future,"cancelled":false,"received":false,"taken":true}],
              "likeToken":999999,"online":true,"lastSeen":1}}
        """.trimIndent()

        val user = NetworkJson.decodeFromString<GetUserDataResponse>(body).user!!

        assertEquals("bober1", user.username)
        assertEquals("bob", user.gotLiked!!.single().username)
        assertEquals("u3", user.passedUsers!!.single().userId)
        assertEquals("carol", user.matchList!!.single().username)
        assertEquals(PremiumCards.BOBERIUM, user.premiumCards!!.single().name)
        assertTrue(user.isPremium)
        assertEquals(999999, user.likeToken)
    }

    @Test
    fun `welcome card that was never activated is not premium`() {
        val body = """{"success":true,"user":{"premiumCards":[{"name":"BOBERIUM","takenDate":0,"expiryDate":0,"cancelled":false,"received":true,"taken":false}]}}"""
        assertFalse(NetworkJson.decodeFromString<GetUserDataResponse>(body).user!!.isPremium)
    }

    @Test
    fun `messages with string timestamps decode`() {
        val body = """{"success":true,"messageList":[{"text":"hi","senderUsername":"a","senderUserId":"1",
            "recipientUserId":"2","recipientUsername":"b","replyingMessageId":"","emoted":[],
            "timestamp":"1719842400000","status":"READ","id":"m1"}],"message":"ok"}"""
        val message = NetworkJson.decodeFromString<GetAllMessagesResponse>(body).messageList.single()
        assertEquals(1719842400000L, message.timestamp)
    }

    @Test
    fun `update_user only sends the fields that are set`() {
        val json = NetworkJson.encodeToString(UserUpdate.serializer(), UserUpdate(aboutMe = "hello"))
        assertEquals("""{"aboutMe":"hello"}""", json)
    }

    @Test
    fun `error bodies decode`() {
        val body = """{"success":false,"message":"You don't have enough like token."}"""
        assertEquals("You don't have enough like token.", NetworkJson.decodeFromString<MessageResponse>(body).message)
    }
}
