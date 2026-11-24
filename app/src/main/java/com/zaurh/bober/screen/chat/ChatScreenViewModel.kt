package com.zaurh.bober.screen.chat

import java.util.UUID
import com.zaurh.bober.data.message.formatTimestampToTime
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.message.EmoteSealed
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.user.ChatBackground
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.websocket.SocketEvent
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.domain.repository.TenorRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.screen.match.GotMatchState
import com.zaurh.bober.screen.settings.notifications.notifyForSocketEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val webSocketRepository: WebSocketRepository,
    private val userRepository: UserRepository,
    private val tenorRepo: TenorRepo
) : ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val profileDataState: StateFlow<UserData?> = userRepository.profileData
    val chatList: StateFlow<List<ChatData?>> = messageRepository.chatList

    /** Message being replied to; shown above the input and sent with the next message. */
    private val _replyingTo = mutableStateOf<MessageData?>(null)
    val replyingTo: State<MessageData?> = _replyingTo

    /** Long-pressed message whose actions sheet (react / reply / copy) is open. */
    private val _actionsFor = mutableStateOf<MessageData?>(null)
    val actionsFor: State<MessageData?> = _actionsFor

    /** Message whose reactions list is open. */
    private val _reactionsFor = mutableStateOf<MessageData?>(null)
    val reactionsFor: State<MessageData?> = _reactionsFor

    private val _backgroundSheet = mutableStateOf(false)
    val backgroundSheet: State<Boolean> = _backgroundSheet

    private val _getNotificationState = mutableStateOf(GetNotificationState())

    private val _gifState = mutableStateOf(false)
    val gifState: State<Boolean> = _gifState

    private val _gotMatch = mutableStateOf(GotMatchState())
    val gotMatch: State<GotMatchState> = _gotMatch

    private val _modalSheetState: MutableState<Boolean> = mutableStateOf(false)
    val modalSheetState: State<Boolean> = _modalSheetState

    private val _gifUrls = MutableStateFlow<List<String>>(emptyList())
    val gifUrls: StateFlow<List<String>> get() = _gifUrls

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _tenorQuery = mutableStateOf("")
    val tenorQuery: State<String> = _tenorQuery

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    /** Messages typed while offline, oldest first. Sent in order once the socket is back. */
    private data class Outgoing(val localId: String, val text: String, val replyId: String?, val recipientId: String)
    private val outbox = ArrayDeque<Outgoing>()

    private var typingJob: Job? = null
    private var searchGifJob: Job? = null
    private var socketJobs: List<Job> = emptyList()
    private var appContext: Context? = null

    private val currentUserId: String? get() = userDataState.value?.id

    /**
     * Connects to the chat socket and (re)loads messages. Safe to call on every resume: the socket
     * connection is shared and idempotent, and events are only subscribed to once.
     */
    fun connectToChat(context: Context) {
        appContext = context.applicationContext
        webSocketRepository.connect()
        getAllMessages()
        // Holds per-chat settings like the background; may be empty when opened from a notification.
        if (messageRepository.chatList.value.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) { messageRepository.getChatList() }
        }

        if (socketJobs.any { it.isActive }) return
        _state.value = state.value.copy(socketIsObserving = true)

        val eventsJob = webSocketRepository.events
            .onEach(::handleEvent)
            .launchIn(viewModelScope)

        // Messages sent while the socket was down are only available over REST: resync on reconnect.
        var wasConnected = false
        val reconnectJob = webSocketRepository.isConnected
            .onEach { connected ->
                if (connected) flushOutbox()
                if (connected && wasConnected) getAllMessages()
                if (connected) wasConnected = true
            }
            .launchIn(viewModelScope)

        socketJobs = listOf(eventsJob, reconnectJob)
    }

    fun switchRecipient(recipientUserId: String) {
        if (recipientUserId.isNotEmpty()) webSocketRepository.switchRecipient(recipientUserId)
    }

    fun onRouteChange(onChatScreen: Boolean, recipientId: String) {
        _getNotificationState.value = GetNotificationState(
            onChatScreen = onChatScreen,
            recipientId = recipientId
        )
    }

    // --- Socket events ---

    private suspend fun handleEvent(event: SocketEvent) {
        when (event) {
            is SocketEvent.NewMessage -> onNewMessage(event)
            is SocketEvent.Typing -> onTyping(event.senderUserId)
            is SocketEvent.Delivered -> onDelivered(event.recipientUserId)
            is SocketEvent.Read -> onRead(event)
            is SocketEvent.Presence -> _state.value = state.value.copy(
                recipientUserId = event.userId,
                recipientOnlineStatus = event.online,
                recipientLastSeen = event.lastSeen
            )

            is SocketEvent.LikeReceived, is SocketEvent.LikeRemoved -> {
                viewModelScope.launch(Dispatchers.IO) { userRepository.getWhoLikes() }
            }

            is SocketEvent.Matched -> onMatched(event)
            is SocketEvent.Unmatched -> onRelationshipEnded(event.command.otherThan(currentUserId))
            is SocketEvent.Blocked -> onRelationshipEnded(event.command.otherThan(currentUserId))
            is SocketEvent.ReactionChanged -> event.message?.let { onReactionChanged(it.toMessageData()) }
            SocketEvent.NoLongerMatched -> toast("You are no longer matched.")
            is SocketEvent.Unknown -> Log.d(TAG, "Unhandled socket frame: ${event.raw}")
        }
        showNotificationIfNeeded(event)
    }

    private fun onNewMessage(event: SocketEvent.NewMessage) {
        val message = event.message.toMessageData()
        // The server's echo of a queued message replaces its local "sending" placeholder.
        if (message.senderUserId == currentUserId) removePlaceholderFor(message)
        val messages = state.value.messages
        if (message.id != null && messages.any { it.id == message.id }) return
        _state.value = state.value.copy(messages = listOf(message) + messages)

        // A first message creates the chat on the server: refresh the chat list so it shows up.
        val partnerId = if (message.senderUserId == currentUserId) message.recipientUserId else message.senderUserId
        if (messageRepository.chatList.value.none { it?.recipientId == partnerId }) {
            viewModelScope.launch(Dispatchers.IO) { messageRepository.getChatList() }
        }
    }

    /** Reactions update an existing message in place; they are never new messages. */
    private fun onReactionChanged(updated: MessageData) {
        val id = updated.id ?: return
        updateMessages(shouldUpdate = { it.id == id }, update = { it.copy(emoted = updated.emoted) })
        _reactionsFor.value?.takeIf { it.id == id }?.let { open ->
            _reactionsFor.value = if (updated.emoted.isEmpty()) null else open.copy(emoted = updated.emoted)
        }
    }

    private fun onTyping(senderUserId: String) {
        typingJob?.cancel()
        typingJob = viewModelScope.launch {
            _state.value = state.value.copy(recipientUserId = senderUserId, recipientIsTyping = true)
            delay(2000)
            _state.value = state.value.copy(recipientUserId = senderUserId, recipientIsTyping = false)
        }
    }

    /** Messages the current user sent to [recipientUserId] reached their device. */
    private fun onDelivered(recipientUserId: String) {
        updateMessages(
            shouldUpdate = { it.recipientUserId == recipientUserId && it.status == MessageStatus.SENT },
            update = { it.copy(status = MessageStatus.DELIVERED) }
        )
    }

    /** [SocketEvent.Read.readerUserId] read everything [SocketEvent.Read.otherUserId] sent them. */
    private fun onRead(event: SocketEvent.Read) {
        updateMessages(
            shouldUpdate = {
                it.senderUserId == event.otherUserId &&
                    it.recipientUserId == event.readerUserId &&
                    it.status != MessageStatus.READ
            },
            update = { it.copy(status = MessageStatus.READ) }
        )
    }

    private fun onMatched(event: SocketEvent.Matched) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getMatchedUsers()
            messageRepository.getChatList()
        }
        // The user whose like created the match sees the animation; the other one gets a notification.
        if (event.match.senderUserId != currentUserId) return
        // Stays up until the user picks "Send a message" or "Keep swiping".
        _gotMatch.value = GotMatchState(
            gotMatch = true,
            recipientId = event.match.recipientUserId,
            recipientImage = event.match.recipientImage.firstOrNull().orEmpty(),
            recipientUsername = event.match.recipientUsername,
        )
    }

    fun dismissMatch() {
        _gotMatch.value = gotMatch.value.copy(gotMatch = false)
    }

    /** Unmatched or blocked (by either side): the server already deleted the conversation. */
    private fun onRelationshipEnded(otherUserId: String) {
        _state.value = state.value.copy(
            messages = state.value.messages.filterNot {
                it.senderUserId == otherUserId || it.recipientUserId == otherUserId
            }
        )
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getMatchedUsers()
            messageRepository.getChatList()
        }
    }

    private suspend fun showNotificationIfNeeded(event: SocketEvent) {
        // The socket only runs in the foreground; in the background, push notifications take over.
        val context = appContext ?: return
        val notificationState = _getNotificationState.value
        withContext(Dispatchers.IO) {
            notifyForSocketEvent(
                context = context,
                event = event,
                currentUserId = currentUserId,
                openChatWith = notificationState.recipientId.takeIf { notificationState.onChatScreen == true }
            )
        }
    }

    private inline fun updateMessages(shouldUpdate: (MessageData) -> Boolean, update: (MessageData) -> MessageData) {
        val messages = state.value.messages
        if (messages.none(shouldUpdate)) return
        _state.value = state.value.copy(messages = messages.map { if (shouldUpdate(it)) update(it) else it })
    }

    // --- User actions ---

    fun sendMessage() {
        val text = messageText.value
        if (text.isBlank()) return
        val replyId = replyingTo.value?.id
        val sent = webSocketRepository.isConnected.value && send(text, replyId)
        if (!sent) queue(text, replyId) ?: return
        _messageText.value = ""
        _gifState.value = false
        _replyingTo.value = null
    }

    private fun send(text: String, replyId: String?): Boolean =
        if (replyId != null) webSocketRepository.sendReply(replyId, text) else webSocketRepository.sendMessage(text)

    /** Offline: shows the message with a clock icon and sends it on reconnect. Null if there's no open chat. */
    private fun queue(text: String, replyId: String?): Unit? {
        val me = userDataState.value ?: return null
        val recipient = profileDataState.value ?: return null
        val recipientId = recipient.id?.takeIf { it.isNotEmpty() } ?: return null
        val localId = "local-${UUID.randomUUID()}"
        outbox.addLast(Outgoing(localId, text, replyId, recipientId))
        val placeholder = MessageData(
            id = localId,
            text = text,
            senderUsername = me.username,
            senderUserId = me.id,
            recipientUserId = recipientId,
            recipientUsername = recipient.username,
            timestamp = formatTimestampToTime(System.currentTimeMillis()),
            status = MessageStatus.SENDING,
            replyingMessageId = replyId,
        )
        _state.value = state.value.copy(messages = listOf(placeholder) + state.value.messages)
        return Unit
    }

    /**
     * Sends queued messages in order. The backend delivers to the "current recipient", so each one is
     * sent to its own chat, then the open chat is selected again.
     */
    private fun flushOutbox() {
        if (outbox.isEmpty()) return
        val openChat = profileDataState.value?.id
        while (outbox.isNotEmpty()) {
            val next = outbox.first()
            webSocketRepository.switchRecipient(next.recipientId)
            if (!send(next.text, next.replyId)) break // Dropped again; retry on the next reconnect.
            outbox.removeFirst()
        }
        openChat?.takeIf { it.isNotEmpty() }?.let(webSocketRepository::switchRecipient)
    }

    private fun removePlaceholderFor(echo: MessageData) {
        val messages = state.value.messages
        val placeholder = messages.lastOrNull {
            it.status == MessageStatus.SENDING && it.text == echo.text && it.recipientUserId == echo.recipientUserId
        } ?: return
        _state.value = state.value.copy(messages = messages - placeholder)
    }

    fun onMessageChange(message: String) {
        _messageText.value = message
        webSocketRepository.sendTyping()
    }

    /** Tells the backend the current user has read the open conversation. */
    fun markMessagesRead() {
        webSocketRepository.markMessagesRead()
    }

    fun onGifStateChange() {
        _tenorQuery.value = ""
        _gifState.value = !gifState.value
        if (gifState.value) {
            trendingGifs()
        }
    }

    private fun trendingGifs() {
        viewModelScope.launch(Dispatchers.IO) {
            _gifUrls.value = tenorRepo.trendingGifs()
        }
    }

    fun onTenorQueryChange(query: String) {
        _tenorQuery.value = query
        searchGifJob?.cancel()
        searchGifJob = viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            _gifUrls.value = tenorRepo.searchGifs(tenorQuery.value)
        }
    }

    fun getProfileData(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getProfileID(username)
        }
    }

    private fun getAllMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = state.value.copy(isLoading = true)
            val result = messageRepository.getPrivateMessages()
            _state.value = if (result.success) {
                // Keep messages still waiting in the outbox; the server doesn't know about them yet.
                val pending = state.value.messages.filter { it.status == MessageStatus.SENDING }
                state.value.copy(messages = pending + result.messageList.map { it.toMessageData() }, isLoading = false)
            } else {
                // Keep what is on screen rather than wiping the conversation on a network error.
                state.value.copy(isLoading = false)
            }
        }
    }

    fun unMatch(recipientUserId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.unmatch(recipientUserId)
            withContext(Dispatchers.Main) {
                if (response.success) onSuccess() else toast(response.message)
            }
        }
    }

    fun block(recipientUserId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.block(recipientUserId)
            withContext(Dispatchers.Main) {
                if (response.success) onSuccess() else toast(response.message)
            }
        }
    }

    // --- Message actions: reply, react, copy ---

    fun openMessageActions(message: MessageData) {
        // Queued messages have no server id yet, so they can't be reacted or replied to.
        if (message.id != null && message.status != MessageStatus.SENDING) _actionsFor.value = message
    }

    fun closeMessageActions() {
        _actionsFor.value = null
    }

    fun startReply(message: MessageData) {
        _actionsFor.value = null
        if (message.status == MessageStatus.SENDING) return
        _replyingTo.value = message
    }

    fun cancelReply() {
        _replyingTo.value = null
    }

    /** Picking the reaction the user already left removes it; anything else adds or replaces it. */
    fun react(message: MessageData, emote: EmoteSealed) {
        _actionsFor.value = null
        val messageId = message.id ?: return
        val currentUser = userDataState.value ?: return
        val mine = message.emoted.firstOrNull { it.senderId == currentUser.id }?.emoteSealed
        viewModelScope.launch(Dispatchers.IO) {
            val response = if (mine == emote) {
                messageRepository.removeEmote(messageId)
            } else {
                messageRepository.emoteMessage(messageId, emote, currentUser)
            }
            if (!response.success) toast(response.message)
        }
    }

    fun removeMyReaction(message: MessageData) {
        val messageId = message.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val response = messageRepository.removeEmote(messageId)
            if (!response.success) toast(response.message)
        }
    }

    fun openReactions(message: MessageData) {
        if (message.emoted.isNotEmpty()) _reactionsFor.value = message
    }

    fun closeReactions() {
        _reactionsFor.value = null
    }

    // --- Chat background ---

    fun openBackgroundSheet() {
        _modalSheetState.value = false
        _backgroundSheet.value = true
    }

    fun closeBackgroundSheet() {
        _backgroundSheet.value = false
    }

    fun changeBackground(recipientId: String, background: ChatBackground, doodle: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = messageRepository.changeChatBackground(recipientId, background, doodle)
            if (!response.success) toast(response.message)
        }
    }

    fun openModalSheet() {
        _modalSheetState.value = true
    }

    fun closeModalSheet() {
        _modalSheetState.value = false
    }

    fun clearProfileData() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.clearProfileData()
        }
    }

    fun initializeChatState(
        recipientUserId: String,
        recipientOnlineStatus: Boolean,
        recipientLastSeen: Long
    ) {
        _state.value = state.value.copy(
            recipientUserId = recipientUserId,
            recipientOnlineStatus = recipientOnlineStatus,
            recipientLastSeen = recipientLastSeen
        )
    }

    private fun toast(message: String) {
        val context = appContext ?: return
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private companion object {
        const val TAG = "ChatScreenViewModel"
    }
}
