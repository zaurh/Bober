package com.zaurh.bober.data.message

import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.responses.GetLastMessagesResponse
import com.zaurh.bober.data.responses.UnreadMessagesSizeResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MessageApi {
    @GET("/priv_messages")
    suspend fun getRecipientMessages(
        @Header("Authorization") token: String,
        @Query("recipientUserId") recipientUserId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): GetAllMessagesResponse

    @GET("/chat_list")
    suspend fun getChatList(
        @Header("Authorization") token: String,
    ): ChatListResponse

    @GET("/last_messages")
    suspend fun getLastMessages(
        @Header("Authorization") token: String
    ): GetLastMessagesResponse

    @GET("/get_unread_messages_size")
    suspend fun getUnreadMessagesSize(
        @Header("Authorization") token: String
    ): UnreadMessagesSizeResponse

}


