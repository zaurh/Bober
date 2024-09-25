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
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.domain.repository.TenorRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.screen.match.GotMatchState
import com.zaurh.bober.screen.settings.notifications.NotificationType
import com.zaurh.bober.screen.settings.notifications.preferences.NotificationPreferences
import com.zaurh.bober.screen.settings.notifications.showNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val webSocketRepository: WebSocketRepository,
    private val userRepository: UserRepository,
    private val tenorRepo: TenorRepo
) : ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData

    private val _getNotificationState = mutableStateOf(GetNotificationState())
    private val getNotificationState: State<GetNotificationState> = _getNotificationState

    private val _gifState = mutableStateOf(false)
    val gifState: State<Boolean> = _gifState

    private val _gotMatch = mutableStateOf(GotMatchState())
    val gotMatch: State<GotMatchState> = _gotMatch

    private val _modalSheetState: MutableState<Boolean> = mutableStateOf(false)
    val modalSheetState: State<Boolean> = _modalSheetState

    private val _gifUrls = MutableStateFlow<List<String>>(emptyList())
    val gifUrls: StateFlow<List<String>> get() = _gifUrls

    val profileDataState: StateFlow<UserData?> = userRepository.profileData

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _tenorQuery = mutableStateOf("")
    val tenorQuery: State<String> = _tenorQuery

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private var typingJob: Job? = null
    private var searchGifJob: Job? = null



    fun connectToChat(context: Context) {
        val dataStore = NotificationPreferences(context)

        viewModelScope.launch {
            _state.value = state.value.copy(
                socketIsObserving = true
            )
            webSocketRepository.initSession()
            getAllMessages()
            webSocketRepository.observeMessages()
                .onEach { message ->
                    val messageText = message.text ?: ""
                    when (messageText) {
                        "switched to:" -> {}
                        "typing:" -> {
                            typingCommand(message)
                        }
                        "no_longer_matched:" -> {
                            noLongerMatchedCommand(context)
                        }

                        "like_sent:" -> {
                            likeSentCommand(dataStore, context)
                        }

                        "got_match:" -> {
                            gotMatchCommand(message, dataStore, context)
                        }

                        "deliveredMessage:" -> {
                            deliveredCommand(message)
                        }

                        "readMessages:" -> {
                            readMessagesCommand(message)
                        }

                        "triggerOnline:" -> {
                            triggerOnlineCommand(message)
                        }

                        else -> {
                            sendMessageCommand(message, context)
                        }
                    }
                }.launchIn(viewModelScope)

        }
    }

    fun onRouteChange(onChatScreen: Boolean, recipientId: String) {
        _getNotificationState.value = getNotificationState.value.copy(
            onChatScreen = onChatScreen,
            recipientId = recipientId
        )
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

    fun getProfileData(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getProfileID(username)
        }
    }

    fun messageIsRead(messageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketRepository.messageIsRead(messageId)
        }
    }


    //Commands

    private fun deliveredCommand(messageData: MessageData) {
        val recipientId = messageData.recipientUserId

        val messageList = state.value.messages

        val unDeliveredMessages = messageList.filter {
            it.status == MessageStatus.SENT && it.recipientUserId == recipientId
        }

        if (unDeliveredMessages.isNotEmpty()) {
            // Update the status of undelivered messages
            val updatedMessages = state.value.messages.map { message ->
                if (message.recipientUserId == recipientId && message.status == MessageStatus.SENT) {
                    message.copy(status = MessageStatus.DELIVERED)
                } else {
                    message
                }
            }

            _state.value = state.value.copy(
                messages = updatedMessages
            )
        }
    }

    private fun readMessagesCommand(messageData: MessageData) {
        val recipientId = messageData.recipientUserId

        val messageListofRecipient =
            state.value.messages.filter { it.recipientUserId == recipientId }

        val unReadMessages = messageListofRecipient.filter {
            it.status != MessageStatus.READ && it.recipientUserId == recipientId
        }

        if (unReadMessages.isNotEmpty()) {
            // Update the status of undelivered messages
            val updatedMessages = state.value.messages.map { message ->
                if (message.recipientUserId == recipientId && message.status == MessageStatus.DELIVERED) {
                    message.copy(status = MessageStatus.READ)
                } else {
                    message
                }
            }
            _state.value = state.value.copy(
                messages = updatedMessages
            )
        }
    }

    private fun triggerOnlineCommand(messageData: MessageData) {
        val recipientLastSeen =
            messageData.recipientLastSeen ?: profileDataState.value?.lastSeen
        val recipientUserId = messageData.recipientUserId

        _state.value = state.value.copy(
            recipientUserId = recipientUserId,
            recipientOnlineStatus = messageData.recipientOnlineStatus,
            recipientLastSeen = recipientLastSeen
        )
    }

    private fun sendMessageCommand(messageData: MessageData, context: Context) {
        val recipientId = getNotificationState.value.recipientId
        val dataStore = NotificationPreferences(context)
        val messageSenderIsCurrent = messageData.senderUserId == userDataState.value?.id
        val chatScreenWithRecipient =
            (recipientId == messageData.senderUserId || recipientId == messageData.recipientUserId) && getNotificationState.value.onChatScreen == true

        if (!messageSenderIsCurrent && !chatScreenWithRecipient) {
            viewModelScope.launch {
                val text =
                    if (messageData.text?.endsWith(".gif") == true) "Sent GIF" else messageData.text
                        ?: ""
                dataStore.getMessagesStatus.collect {
                    if (it) {
                        showNotification(
                            channelId = "chat",
                            context = context,
                            text = text,
                            title = messageData.senderUsername ?: "",
                            notificationType = NotificationType.CHAT
                        )
                    }
                }

            }
        }

        val newList = state.value.messages.toMutableList().apply {
            add(0, messageData)
        }
        _state.value = state.value.copy(
            messages = newList
        )
    }

    private fun typingCommand(messageData: MessageData) {
        val recipientId = messageData.senderUserId

        typingJob?.cancel()
        typingJob = viewModelScope.launch {
            Log.d("typingBober", "${_state.value.recipientIsTyping}")
            _state.value = state.value.copy(
                recipientUserId = recipientId,
                recipientIsTyping = true
            )
            Log.d("typingBober", "${_state.value.recipientIsTyping}")
            delay(2000)
            _state.value = state.value.copy(
                recipientUserId = recipientId,
                recipientIsTyping = false
            )
            Log.d("typingBober", "${_state.value.recipientIsTyping}")
        }
    }

    private suspend fun likeSentCommand(
        dataStore: NotificationPreferences,
        context: Context
    ) {
        dataStore.getLikesStatus.collect {
            if (it) {
                showNotification(
                    channelId = "like",
                    context = context,
                    title = "Bober",
                    text = "Someone liked you!",
                    notificationType = NotificationType.LIKE
                )
            }
        }
    }

    private suspend fun gotMatchCommand(
        messageData: MessageData,
        dataStore: NotificationPreferences,
        context: Context
    ) {
        val recipientId = messageData.recipientUserId ?: ""
        if (recipientId != userDataState.value?.id) {

            _gotMatch.value = gotMatch.value.copy(
                gotMatch = true,
                recipientId = recipientId
            )

            delay(3000)
            _gotMatch.value = gotMatch.value.copy(
                gotMatch = false
            )

        } else {
            dataStore.getMatchStatus.collect {
                if (it) {
                    showNotification(
                        channelId = "match",
                        context = context,
                        title = "Bober",
                        text = "You've got a new match!",
                        notificationType = NotificationType.MATCH
                    )
                }
            }
        }
    }

    private fun noLongerMatchedCommand(context: Context){
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, "You are no longer matched.", Toast.LENGTH_SHORT).show()
        }
    }


    fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isNotBlank()) {
                webSocketRepository.sendMessage(messageText.value)
                _messageText.value = ""
                _gifState.value = false
            }
        }
    }

    fun switchRecipient(recipientUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketRepository.switchRecipient(
                newRecipientUserId = recipientUserId
            )
        }
    }


    fun onMessageChange(message: String) {
        _messageText.value = message
        viewModelScope.launch(Dispatchers.IO) {
            webSocketRepository.typingMessage()
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


    private fun getAllMessages() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            val result = messageRepository.getPrivateMessages()
            _state.value = state.value.copy(
                messages = result.messageList?.map { it.toMessageData() } ?: listOf(),
                isLoading = false
            )
        }
    }

    fun unMatch(recipientUserId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.unmatch(recipientUserId)
            viewModelScope.launch(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    fun block(recipientUserId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.block(recipientUserId)
            viewModelScope.launch(Dispatchers.Main) {
                onSuccess()
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
    ){
        _state.value = state.value.copy(
            recipientUserId = recipientUserId,
            recipientOnlineStatus = recipientOnlineStatus,
            recipientLastSeen = recipientLastSeen
        )
    }
}