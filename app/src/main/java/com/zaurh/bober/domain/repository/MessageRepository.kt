package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.message.EmoteData
import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.EmotedMessageResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.UnreadMessagesSizeResponse
import com.zaurh.bober.data.user.BackgroundColor
import com.zaurh.bober.screen.chat.state.ChatListState
import kotlinx.coroutines.flow.StateFlow

interface MessageRepository {

    val chatList: StateFlow<ChatListState>

    suspend fun getUnreadMessagesSize(): UnreadMessagesSizeResponse
    suspend fun getRecipientMessages(recipientUserId: String, page: Int, pageSize: Int): GetAllMessagesResponse
    suspend fun getChatList(): ChatListResponse
    suspend fun emoteMessage(
        senderUserId: String,
        recipientId: String,
        messageId: String,
        messageSenderId: String,
        emoteData: EmoteData
    ): EmotedMessageResponse

    suspend fun changeChatBackground(
        recipientId: String,
        backgroundColor: BackgroundColor,
        doodle: Boolean
    ): MessageResponse

    suspend fun removeEmote(
        senderUserId: String,
        recipientId: String,
        messageId: String,
    ): EmotedMessageResponse

}