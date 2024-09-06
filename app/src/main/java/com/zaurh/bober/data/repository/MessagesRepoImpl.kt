package com.zaurh.bober.data.repository

import android.content.SharedPreferences
import com.zaurh.bober.data.message.MessageApi
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.domain.repository.MessageRepository
import javax.inject.Inject

class MessagesRepoImpl @Inject constructor(
    private val api: MessageApi,
    private val prefs: SharedPreferences
) : MessageRepository {


    override suspend fun getPrivateMessages(): GetAllMessagesResponse {
        return try {
            val token = prefs.getString("jwt", null) ?: ""

            val response = api.getPrivateMessages(
                token = "Bearer $token"
            )

            GetAllMessagesResponse(
                success = response.success,
                messageList = response.messageList,
                message = response.message
            )


        } catch (e: Exception) {
            GetAllMessagesResponse(
                success = false,
                message = e.message.toString()
            )
        }
    }


}
