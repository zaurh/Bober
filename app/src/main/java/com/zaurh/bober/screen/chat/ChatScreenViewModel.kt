package com.zaurh.bober.screen.chat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.websocket.SocketEvent
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.domain.repository.TenorRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.screen.match.GotMatchState
import com.zaurh.bober.screen.settings.notifications.notifyForSocketEvent
import com.zaurh.bober.services.RunningService
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

        if (socketJobs.any { it.isActive }) return
        _state.value = state.value.copy(socketIsObserving = true)

        val eventsJob = webSocketRepository.events
            .onEach(::handleEvent)
            .launchIn(viewModelScope)

        // Messages sent while the socket was down are only available over REST: resync on reconnect.
        var wasConnected = false
        val reconnectJob = webSocketRepository.isConnected
            .onEach { connected ->
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
            // Reactions aren't shown in this app yet; they must not be treated as new messages.
            is SocketEvent.ReactionChanged -> Unit
            SocketEvent.NoLongerMatched -> toast("You are no longer matched.")
            is SocketEvent.Unknown -> Log.d(TAG, "Unhandled socket frame: ${event.raw}")
        }
        showNotificationIfNeeded(event)
    }

    private fun onNewMessage(event: SocketEvent.NewMessage) {
        val message = event.message.toMessageData()
        val messages = state.value.messages
        if (message.id != null && messages.any { it.id == message.id }) return
        _state.value = state.value.copy(messages = listOf(message) + messages)

        // A first message creates the chat on the server: refresh the chat list so it shows up.
        val partnerId = if (message.senderUserId == currentUserId) message.recipientUserId else message.senderUserId
        if (messageRepository.chatList.value.none { it?.recipientId == partnerId }) {
            viewModelScope.launch(Dispatchers.IO) { messageRepository.getChatList() }
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
        // While the app is in the background RunningService shows notifications instead.
        if (RunningService.isRunning) return
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
        if (webSocketRepository.sendMessage(text)) {
            _messageText.value = ""
            _gifState.value = false
        } else {
            toast("Not connected. Please try again in a moment.")
        }
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
                state.value.copy(messages = result.messageList.map { it.toMessageData() }, isLoading = false)
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
