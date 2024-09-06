package com.zaurh.bober.data.message

import com.zaurh.bober.data.responses.GetAllMessagesResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface MessageApi {
    @GET("/messages")
    suspend fun getPrivateMessages(
        @Header("Authorization") token: String,
    ): GetAllMessagesResponse
}

