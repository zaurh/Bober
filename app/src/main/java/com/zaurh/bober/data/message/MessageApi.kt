package com.zaurh.bober.data.message

import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MessageApi {
    @GET("/messages")
    suspend fun getPrivateMessages(
        @Header("Authorization") token: String,
    ): GetAllMessagesResponse

    @GET("/priv_messages")
    suspend fun getRecipientMessages(
        @Header("Authorization") token: String,
        @Query ("recipientUserId") recipientUserId: String
    ): GetAllMessagesResponse

    @GET("/chat_list")
    suspend fun getChatList(
        @Header("Authorization") token: String,
    ): ChatListResponse
}


