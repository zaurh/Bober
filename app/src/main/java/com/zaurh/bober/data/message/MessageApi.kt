package com.zaurh.bober.data.message

import com.zaurh.bober.data.requests.ChangeChatBackgroundRequest
import com.zaurh.bober.data.requests.EmoteMessageRequest
import com.zaurh.bober.data.requests.RemoveEmoteRequest
import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.EmotedMessageResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.UnreadMessagesSizeResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface MessageApi {
    @GET("/priv_messages")
    suspend fun getRecipientMessages(
        @Header("Authorization") token: String,
        @Query("recipientUserIds") recipientUserId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): GetAllMessagesResponse

    @PUT("/emote_message")
    suspend fun emoteMessage(
        @Header("Authorization") token: String,
        @Body emoteMessageRequest: EmoteMessageRequest
    ): EmotedMessageResponse

    @PUT("/chat_background")
    suspend fun changeChatBackground(
        @Header("Authorization") token: String,
        @Body chatBackgroundRequest: ChangeChatBackgroundRequest
    ): MessageResponse

    @PUT("/remove_emote")
    suspend fun removeEmote(
        @Header("Authorization") token: String,
        @Body removeEmoteRequest: RemoveEmoteRequest
    ): EmotedMessageResponse

    @GET("/chat_list")
    suspend fun getChatList(
        @Header("Authorization") token: String,
    ): ChatListResponse


    @GET("/get_unread_messages_size")
    suspend fun getUnreadMessagesSize(
        @Header("Authorization") token: String
    ): UnreadMessagesSizeResponse

}


