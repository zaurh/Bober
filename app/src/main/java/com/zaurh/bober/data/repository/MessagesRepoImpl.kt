package com.zaurh.bober.data.repository

import com.zaurh.bober.data.message.EmoteData
import com.zaurh.bober.data.message.EmoteSealed
import com.zaurh.bober.data.message.MessageApi
import com.zaurh.bober.data.remote.apiCall
import com.zaurh.bober.data.requests.ChangeChatBackgroundRequest
import com.zaurh.bober.data.requests.EmoteMessageRequest
import com.zaurh.bober.data.requests.RemoveEmoteRequest
import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.EmotedMessageResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.user.ChatBackground
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepoImpl @Inject constructor(
    private val api: MessageApi
) : MessageRepository {

    private val _chatList = MutableStateFlow<List<ChatData?>>(listOf())
    override val chatList: StateFlow<List<ChatData?>> get() = _chatList

    override suspend fun getPrivateMessages(): GetAllMessagesResponse =
        apiCall({ GetAllMessagesResponse(success = false, message = it) }) {
            api.getPrivateMessages()
        }

    override suspend fun getChatList(): ChatListResponse =
        apiCall({ ChatListResponse(success = false, message = it) }) {
            api.getChatList().also { if (it.success) _chatList.value = it.chatList }
        }

    override suspend fun changeChatBackground(
        recipientId: String,
        background: ChatBackground,
        doodle: Boolean
    ): MessageResponse {
        val previous = _chatList.value.firstOrNull { it?.recipientId == recipientId }
        updateChat(recipientId) { it.copy(backgroundColor = background, doodleEnabled = doodle) }

        val response = apiCall({ MessageResponse(success = false, message = it) }) {
            api.changeChatBackground(ChangeChatBackgroundRequest(recipientId, background, doodle))
        }
        if (!response.success && previous != null) updateChat(recipientId) { previous }
        return response
    }

    override suspend fun emoteMessage(messageId: String, emote: EmoteSealed, currentUser: UserData): EmotedMessageResponse =
        apiCall({ EmotedMessageResponse(success = false, message = it) }) {
            api.emoteMessage(
                EmoteMessageRequest(
                    messageId = messageId,
                    emoteData = EmoteData(
                        emoteSealed = emote,
                        senderId = currentUser.id.orEmpty(),
                        senderUsername = currentUser.username.orEmpty(),
                        senderImage = currentUser.imageUrl?.firstOrNull().orEmpty(),
                    )
                )
            )
        }

    override suspend fun removeEmote(messageId: String): EmotedMessageResponse =
        apiCall({ EmotedMessageResponse(success = false, message = it) }) {
            api.removeEmote(RemoveEmoteRequest(messageId))
        }

    private fun updateChat(recipientId: String, transform: (ChatData) -> ChatData) {
        _chatList.update { list -> list.map { if (it?.recipientId == recipientId) transform(it) else it } }
    }

    override fun clear() {
        _chatList.value = listOf()
    }
}
