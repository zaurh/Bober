package com.zaurh.bober.data.message

import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.requests.ChangeChatBackgroundRequest
import com.zaurh.bober.data.requests.EmoteMessageRequest
import com.zaurh.bober.data.requests.RemoveEmoteRequest
import com.zaurh.bober.data.responses.EmotedMessageResponse
import com.zaurh.bober.data.responses.MessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

/** Chat REST endpoints. Live messaging goes through the chat socket (see WebSocketRepository). */
interface MessageApi {
    @GET("/messages")
    suspend fun getPrivateMessages(): GetAllMessagesResponse

    @GET("/chat_list")
    suspend fun getChatList(): ChatListResponse

    @PUT("/chat_background")
    suspend fun changeChatBackground(@Body request: ChangeChatBackgroundRequest): MessageResponse

    /** Adds or replaces the current user's reaction. Both users get the updated message over the socket. */
    @PUT("/emote_message")
    suspend fun emoteMessage(@Body request: EmoteMessageRequest): EmotedMessageResponse

    @PUT("/remove_emote")
    suspend fun removeEmote(@Body request: RemoveEmoteRequest): EmotedMessageResponse
}
