package com.zaurh.bober.data.message

import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import retrofit2.http.GET

/** Chat REST endpoints. Live messaging goes through the chat socket (see WebSocketRepository). */
interface MessageApi {
    @GET("/messages")
    suspend fun getPrivateMessages(): GetAllMessagesResponse

    @GET("/chat_list")
    suspend fun getChatList(): ChatListResponse
}
