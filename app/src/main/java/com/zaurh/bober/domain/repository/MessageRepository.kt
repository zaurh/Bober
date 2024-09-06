package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.responses.GetAllMessagesResponse

interface MessageRepository {

    suspend fun getPrivateMessages(): GetAllMessagesResponse

}