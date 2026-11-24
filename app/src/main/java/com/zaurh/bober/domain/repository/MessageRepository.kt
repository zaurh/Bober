package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.message.EmoteSealed
import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.EmotedMessageResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.user.ChatBackground
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.data.user.UserData
import kotlinx.coroutines.flow.StateFlow

interface MessageRepository {

    val chatList: StateFlow<List<ChatData?>>

    suspend fun getPrivateMessages(): GetAllMessagesResponse
    suspend fun getChatList(): ChatListResponse

    /** Applies the background locally right away, then saves it; reverts if the server refuses. */
    suspend fun changeChatBackground(recipientId: String, background: ChatBackground, doodle: Boolean): MessageResponse

    /** Reacts to a message as [currentUser]. The updated message arrives over the chat socket. */
    suspend fun emoteMessage(messageId: String, emote: EmoteSealed, currentUser: UserData): EmotedMessageResponse

    /** Removes the current user's reaction from a message. */
    suspend fun removeEmote(messageId: String): EmotedMessageResponse

    /** Clears all cached data, e.g. on logout. */
    fun clear()

}
