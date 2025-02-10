package com.zaurh.bober.screen.chat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.message.EmoteData
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.message.room.toMessageData
import com.zaurh.bober.data.repository.MessageEvent
import com.zaurh.bober.data.responses.MatchedUserData
import com.zaurh.bober.data.responses.WhoLikesData
import com.zaurh.bober.data.user.BackgroundColor
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import com.zaurh.bober.data.websocket.BlockCommand
import com.zaurh.bober.data.websocket.GotMatchCommand
import com.zaurh.bober.data.websocket.NewLikeCommand
import com.zaurh.bober.data.websocket.UnMatchCommand
import com.zaurh.bober.data.websocket.UnlikeCommand
import com.zaurh.bober.domain.ConnectivityObserver
import com.zaurh.bober.domain.repository.LocalMessageRepository
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.domain.repository.TenorRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.model.toMessageEntity
import com.zaurh.bober.screen.chat.state.ChatListState
import com.zaurh.bober.screen.chat.state.ChatState
import com.zaurh.bober.screen.chat.state.EmoteModalState
import com.zaurh.bober.screen.chat.state.EmptyGifState
import com.zaurh.bober.screen.chat.state.MatchListState
import com.zaurh.bober.screen.chat.state.NewLikesState
import com.zaurh.bober.screen.chat.state.ReplyingState
import com.zaurh.bober.screen.home.state.UnreadMessagesState
import com.zaurh.bober.screen.match.GotMatchState
import com.zaurh.bober.screen.settings.notifications.NotificationType
import com.zaurh.bober.screen.settings.notifications.preferences.NotificationPreferences
import com.zaurh.bober.screen.settings.notifications.showNotification
import com.zaurh.bober.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageRepository: MessageRepository,
    private val webSocketRepository: WebSocketRepository,
    private val userRepository: UserRepository,
    private val tenorRepo: TenorRepo,
    private val localMessageRepository: LocalMessageRepository
) : ViewModel() {

    private val connectivityObserver = ConnectivityObserver(context)

    val userDataState: StateFlow<UserData?> = userRepository.userData

    val switchedRecipient: StateFlow<String> = webSocketRepository.switchedRecipient

    private val _getNotificationState = mutableStateOf(GetNotificationState())
    private val getNotificationState: State<GetNotificationState> = _getNotificationState


    private val _gifState = mutableStateOf(false)
    val gifState: State<Boolean> = _gifState

    private val _gifUrls = MutableStateFlow<List<String>>(emptyList())
    val gifUrls: StateFlow<List<String>> get() = _gifUrls

    private val _deepLinkHandled = mutableStateOf(false)
    val deepLinkHandled: State<Boolean> = _deepLinkHandled

    private val _emptyChatGif = mutableStateOf(EmptyGifState())
    val emptyChatGif: State<EmptyGifState> = _emptyChatGif

    private val _gotMatch = mutableStateOf(GotMatchState())
    val gotMatch: State<GotMatchState> = _gotMatch

    private val _selectedMessages: MutableState<List<MessageData>> = mutableStateOf(listOf())
    val selectedMessages: State<List<MessageData>> = _selectedMessages

    private val _backgroundModalSheet: MutableState<Boolean> = mutableStateOf(false)
    val backgroundModalSheet: State<Boolean> = _backgroundModalSheet

    private val _modalSheetState: MutableState<Boolean> = mutableStateOf(false)
    val modalSheetState: State<Boolean> = _modalSheetState

    private val _emoteModalState = mutableStateOf(EmoteModalState())
    val emoteModalState: State<EmoteModalState> = _emoteModalState

    val profileDataState: StateFlow<UserData?> = userRepository.profileData

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _tenorQuery = mutableStateOf("")
    val tenorQuery: State<String> = _tenorQuery

    private val _chatState = mutableStateOf(ChatState())
    val chatState: State<ChatState> = _chatState

    private val _replyingState = mutableStateOf(ReplyingState())
    val replyingState: State<ReplyingState> = _replyingState


    private val _chatListState = mutableStateOf(ChatListState())
    val chatListState: State<ChatListState> = _chatListState

    private val _matchListState = mutableStateOf(MatchListState())
    val matchListState: State<MatchListState> = _matchListState

//    private val _newLikesState = mutableStateOf(NewLikesState())
//    val newLikesState: State<NewLikesState> = _newLikesState

    private val _newLikesState = MutableStateFlow(NewLikesState())
        val newLikesState = _newLikesState.asStateFlow()

    private val _unreadMessagesState = mutableStateOf(UnreadMessagesState())
    val unreadMessagesState: State<UnreadMessagesState> = _unreadMessagesState

    private var typingJob: Job? = null
    private var searchGifJob: Job? = null


    init {
        observeNetworkChanges()
        startedTemp()

    }

    fun ljsadnljasdsa(){
        _newLikesState.value = newLikesState.value.copy(
            newLikes = listOf()
        )
    }

    private fun startedTemp(){
        Log.d("Viewmodelstates", "Chat Started")
    }


    fun selectMessage(messageData: MessageData) {
        if (messageData in selectedMessages.value) {
            _selectedMessages.value = selectedMessages.value.minus(messageData)
        } else {
            _selectedMessages.value = selectedMessages.value.plus(messageData)
        }
    }

    fun clearSelectMessages() {
        _selectedMessages.value = listOf()
    }

    fun handleDeepLink() {
        _deepLinkHandled.value = true
    }


    fun clearMatchList(){
        _matchListState.value = matchListState.value.copy(
            loading = false,
            empty = false,
            matchList = listOf(),
            error = ""
        )
    }
    fun getMatchList() {
        Log.d("boberFunction", "Get match list called")
        _matchListState.value = matchListState.value.copy(
            loading = true,
            empty = false,
            matchList = listOf()
        )
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.getMatchedUsers()
            if (response.success) {
                if (response.matchedUserList.isEmpty()) {
                    _matchListState.value = matchListState.value.copy(
                        loading = false,
                        matchList = listOf(),
                        empty = true
                    )
                } else {
                    _matchListState.value = matchListState.value.copy(
                        loading = false,
                        matchList = response.matchedUserList,
                        empty = false
                    )
                }

            } else {
                _matchListState.value = matchListState.value.copy(
                    loading = false,
                    matchList = listOf(),
                    error = response.message,
                    empty = false
                )
            }
        }
    }

    fun clearChatList(){
        _chatListState.value = chatListState.value.copy(
            loading = false,
            empty = true,
            chatList = listOf(),
            error = ""
        )
    }
    fun getChatList() {
        Log.d("boberFunction", "Get chat list called")
        Log.d("dsaklasdjljks", "1")

        _chatListState.value = chatListState.value.copy(
            loading = true,
            empty = false,
            chatList = listOf()
        )
        viewModelScope.launch(Dispatchers.IO) {
            _chatState.value = chatState.value.copy(
                recipientMessages = listOf()
            )
            val response = messageRepository.getChatList()
            if (response.success) {
                if (response.chatList.isEmpty()) {
                    _chatListState.value = chatListState.value.copy(
                        loading = false,
                        chatList = listOf(),
                        empty = true
                    )
                } else {
                    _chatListState.value = chatListState.value.copy(
                        loading = false,
                        chatList = response.chatList,
                        empty = false
                    )
                    Log.d("dsaklasdjljks", "2")

                    val recipientIds = response.chatList.map { it.recipientId }
                    val recipientIdsString = recipientIds.joinToString(",")
                    Log.d("dsaklasdjljks", "3")

                    getRecipientMessages(recipientIdsString)
                }

            } else {
                _chatListState.value = chatListState.value.copy(
                    loading = false,
                    chatList = listOf(),
                    error = response.message,
                    empty = false
                )
            }
        }
    }

//    fun getChatList(){
//        _chatState.value = chatState.value.copy(
//            chatList = userDataState.value?.chatList ?: listOf()
//        )
//    }

    fun getUnreadMessagesSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = messageRepository.getUnreadMessagesSize()
            if (response.success) {
                _unreadMessagesState.value = unreadMessagesState.value.copy(
                    size = response.size,
                    enabled = true
                )
            }
        }
    }


    private fun observeNetworkChanges() {
        viewModelScope.launch {
            connectivityObserver.observeNetworkStatus().collectLatest { isConnected ->
                if (isConnected) {
                    if (!chatState.value.socketIsObserving && chatState.value.internetClosed && chatState.value.initialized) {
                        repeat(10) {
                            connectToChat()
                            delay(2000)
                            if (chatState.value.socketIsObserving) {
                                return@collectLatest // Exit the entire function
                            }
                        }

                    }
                } else {
                    Log.d("webSocketBober", "Internet disconnected")
                    _chatState.value = chatState.value.copy(
                        socketIsObserving = false,
                        internetClosed = true
                    )
                    Log.d("webSocketBober", "Websocket closed")
                }
            }
        }
    }


    var resetWebsocketJob: Job? = null
    fun webSocketInitialized() {
        resetWebsocketJob?.cancel()
        resetWebsocketJob = viewModelScope.launch {
            _chatState.value = chatState.value.copy(
                initialized = true
            )
            delay(10.minutes)
            _chatState.value = chatState.value.copy(
                initialized = false,
                socketIsObserving = false
            )
        }
    }

    fun switchRecipient(recipientUserId: String) {
        Log.d("webSocketBober", "switchedTO: $recipientUserId")
        viewModelScope.launch(Dispatchers.IO) {
            webSocketRepository.switchRecipient(
                newRecipientUserId = recipientUserId
            )
        }
    }

    fun switchRecipientInState(recipientId: String) {
        _chatState.value = chatState.value.copy(
            switchedRecipientId = recipientId
        )
        Log.d("asdkljsadlasd", "called")
    }

    suspend fun sendPendingMessages() {
        Log.d("webSocketBober", "Resourse success websocket")
        val localMessages = localMessageRepository.getPendingMessages()
        val pendingMessages = localMessages.filter { it.status == MessageStatus.SENDING }
        Log.d("pendingBober", "${localMessageRepository.getPendingMessages()}")
        pendingMessages.forEach {
            webSocketRepository.switchRecipient(it.recipientUserId ?: "")
            val a = webSocketRepository.sendMessage(
                message = it.text ?: "",
            )
            Log.d("sdakjdaslkja", "${a}")

            if (a is Resource.Success) {
                localMessageRepository.deleteSentMessage()
            }
        }
    }


    fun connectToChat() {
        val dataStore = NotificationPreferences(context)

        Log.d("websocketbober", "connecting called")
        viewModelScope.launch {
            webSocketRepository.closeSession() // Ensure previous session is closed

            val response = webSocketRepository.initSession()

            when (response) {
                is Resource.Success -> {
                    Log.d("pendingBober","connected websocket")

                    // Switch to the recipient if specified
                    if (switchedRecipient.value.isNotEmpty()) {
                        webSocketRepository.switchRecipient(switchedRecipient.value)
                    }


                    sendPendingMessages()

                    // Start observing messages
                    webSocketRepository.observeMessages()
                        .onEach { event ->
                            when (event) {
                                is MessageEvent.SwitchedRecipient -> {
                                    // Handle SwitchedRecipient event if needed
                                }

                                is MessageEvent.Typing -> {
                                    typingCommand(event.senderUserId)
                                }

                                is MessageEvent.EmoteMessage -> {
                                    Log.d("dsakljdlasjad", "worked")
                                    sentEmoteCommand(
                                        messageData = event.messageData,
                                        dataStore = dataStore
                                    )
                                }

                                is MessageEvent.RemoveEmote -> {
                                    removeEmoteCommand(
                                        messageData = event.messageData
                                    )
                                }

                                is MessageEvent.NoLongerMatched -> {
                                    noLongerMatchedCommand(context)
                                }

                                is MessageEvent.LikeSent -> {
                                    likeSentCommand(event.data, dataStore, context)
                                }

                                is MessageEvent.UnlikeSent -> {
                                    unlikeCommand(event.data)
                                }

                                is MessageEvent.UnMatchSent -> {
                                    unMatchCommand(event.data)
                                }

                                is MessageEvent.BlockSent -> {
                                    blockCommand(event.data)
                                }

                                is MessageEvent.GotMatch -> {
                                    gotMatchCommand(event.data, dataStore, context)
                                }

                                is MessageEvent.DeliveredMessage -> {
                                    deliveredCommand(event.messageData)
                                    _chatState.value =
                                        chatState.value.copy(socketIsObserving = true)

                                }

                                is MessageEvent.ReadMessages -> {
                                    readMessagesCommand(event.messageData)
                                }

                                is MessageEvent.TriggerOnline -> {
                                    triggerOnlineCommand(
                                        recipientId = event.recipientUserId,
                                        lastSeen = event.lastSeen,
                                        onlineStatus = event.onlineStatus
                                    )
                                }

                                is MessageEvent.GenericMessage -> {
                                    sendMessageCommand(event.messageData, context)
                                }
                            }
                        }

                        .launchIn(viewModelScope)
                }


                is Resource.Error -> {
                    delay(2000L) // Wait before retrying
                }

                else -> {}
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {

            Log.d("webSocketBober", "Websocket closed")
            webSocketRepository.closeSession()
        }
        Log.d("Viewmodelstates", "Chat Ended")
        clearMatchList()
        clearChatList()



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


    fun trendingGifs() {
        viewModelScope.launch(Dispatchers.IO) {
            _gifUrls.value = tenorRepo.trendingGifs()
        }
    }

    var getProfileDataJob: Job? = null

    fun getProfileData(recipientId: String) {

        getProfileDataJob = viewModelScope.launch(Dispatchers.IO) {

            val response = userRepository.getProfileID(recipientId)
            if (response.success) {
                _chatState.value = chatState.value.copy(
                    recipientDataError = false
                )
            } else {
                if (getProfileDataJob?.isActive == true) {
                    _chatState.value = chatState.value.copy(
                        recipientDataError = true
                    )
                }

            }
        }
    }

    fun messageIsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketRepository.messageIsRead()
        }
    }

    fun emoteMessage(
        senderId: String,
        recipientId: String,
        messageId: String,
        emoteData: EmoteData
    ) {
        val recipientMessages = chatState.value.recipientMessages ?: listOf()
        val messageData = recipientMessages.find { it.id == messageId }
        val mutableEmoteData = messageData?.emoted?.toMutableList() ?: mutableListOf()
        val index = mutableEmoteData.indexOfFirst { it.senderId == senderId }

        if (index != -1 && messageData != null) {
            // Update existing emote
            val updatedEmoteData = mutableEmoteData[index].copy(
                emoteSealed = emoteData.emoteSealed
            )
            mutableEmoteData[index] = updatedEmoteData
        } else if (messageData != null) {
            // Add new emote
            mutableEmoteData.add(
                EmoteData(
                    senderId = emoteData.senderId,
                    senderUsername = emoteData.senderUsername,
                    senderImage = emoteData.senderImage,
                    emoteSealed = emoteData.emoteSealed
                )
            )
        }

        if (messageData != null) {
            // Create a new updated message
            val updatedMessage = messageData.copy(emoted = mutableEmoteData)

            // Update the recipientMessages list
            val updatedRecipientMessages = recipientMessages.map {
                if (it.id == messageId) updatedMessage else it
            }

            // Update the chatState with the new list
            _chatState.value = chatState.value.copy(
                recipientMessages = updatedRecipientMessages
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.emoteMessage(
                senderUserId = senderId,
                recipientId = recipientId,
                messageId = messageId,
                messageSenderId = messageData?.senderUserId ?: "",
                emoteData = emoteData
            )
        }
    }

    fun removeEmote(
        senderId: String,
        recipientId: String,
        messageId: String
    ) {
        clearSelectMessages()
        val recipientMessages = chatState.value.recipientMessages ?: listOf()
        val mutableRecipientMessages = recipientMessages.toMutableList()
        val messageData = mutableRecipientMessages.find { it.id == messageId }
        val emoteData = messageData?.emoted?.find { it.senderId == senderId }

        val index = mutableRecipientMessages.indexOfFirst { it.id == messageId }
        if (index != -1) {
            if (emoteData != null) {
                val updatedMessage = mutableRecipientMessages[index].copy(
                    emoted = messageData.emoted.minus(emoteData)
                )
                mutableRecipientMessages[index] = updatedMessage
                _chatState.value = chatState.value.copy(
                    recipientMessages = mutableRecipientMessages
                )
            }

        }


        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.removeEmote(
                senderUserId = senderId,
                recipientId = recipientId,
                messageId = messageId
            )
        }
    }


    fun tempGetRoomMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pendingMessages = localMessageRepository.getPendingMessages()
                Log.d("PendingMessages", "${pendingMessages.map { it.text }}")


            } catch (e: Exception) {
                Log.e("DatabaseError", "Error inserting message", e)
            }
        }
    }


    //Commands

    private fun deliveredCommand(messageData: MessageData) {
        viewModelScope.launch(Dispatchers.IO) {

            val messageList = chatState.value.recipientMessages

            val unDeliveredMessages = messageList?.filter {
                it.status == MessageStatus.SENT && it.recipientUserId == messageData.recipientUserId
            }

            Log.d("dfkljsdflksdf", "worked")


            if (unDeliveredMessages?.isNotEmpty() == true) {
                // Update the status of undelivered messages
                val updatedMessages = chatState.value.recipientMessages?.map { message ->
                    if (message.recipientUserId == messageData.recipientUserId && message.status == MessageStatus.SENT) {
                        localMessageRepository.deleteMessage(message.text ?: "")
                        message.copy(status = MessageStatus.DELIVERED)
                    } else {
                        message
                    }
                }

                _chatState.value = chatState.value.copy(
                    recipientMessages = updatedMessages
                )
            }
        }

    }

    private fun readMessagesCommand(
        messageData: MessageData
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val senderId = messageData.senderUserId
            val recipientId = messageData.recipientUserId

            val unReadMessages = chatState.value.recipientMessages?.filter {
                it.status != MessageStatus.READ && it.senderUserId == recipientId && it.recipientUserId == senderId
            }

            if (senderId == userDataState.value?.id) {
                _unreadMessagesState.value = unreadMessagesState.value.copy(
                    size = unreadMessagesState.value.size - (unReadMessages?.size ?: 0)
                )
            }

            if (unReadMessages?.isNotEmpty() == true) {
                // Update the status of undelivered messages
                val recipientMessages = chatState.value.recipientMessages ?: listOf()

                val updatedMessages = recipientMessages.map { message ->
                    if (message.status == MessageStatus.DELIVERED && message.senderUserId == recipientId && message.recipientUserId == senderId) {
                        message.copy(status = MessageStatus.READ)
                    } else {
                        message
                    }
                }
                _chatState.value = chatState.value.copy(
                    recipientMessages = updatedMessages
                )
            }


        }

    }

    private fun triggerOnlineCommand(
        recipientId: String,
        lastSeen: Long,
        onlineStatus: Boolean,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _chatState.value = chatState.value.copy(
                recipientUserId = recipientId,
                recipientOnlineStatus = onlineStatus,
                recipientLastSeen = lastSeen
            )
        }

    }


    private fun sendMessageCommand(messageData: MessageData, context: Context) {
        val recipientId = getNotificationState.value.recipientId
        val dataStore = NotificationPreferences(context)
        val messageSenderIsCurrent = messageData.senderUserId == userDataState.value?.id
        val chatScreenWithRecipient =
            (recipientId == messageData.senderUserId || recipientId == messageData.recipientUserId) && getNotificationState.value.onChatScreen == true

        val typing = messageData.text == "typing..."

        viewModelScope.launch(Dispatchers.IO) {

            if (!messageSenderIsCurrent && !chatScreenWithRecipient && !typing) {

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
                            notificationType = NotificationType.CHAT,
                            additional = "${messageData.senderUserId}"
                        )
                    }
                }
            }
        }

        val recipientMessages = chatState.value.recipientMessages ?: listOf()
        val newList = recipientMessages.toMutableList().apply {
            val existingMessageIndex =
                indexOfFirst { it.text == messageData.text && it.status == MessageStatus.SENDING } // Check by ID
            if (existingMessageIndex != -1) {
                Log.d("sdakjdsalk", "bura")
                // Update the status of the existing message
                this[existingMessageIndex] =
                    this[existingMessageIndex].copy(
                        status = messageData.status,
                        id = messageData.id
                    )
            } else {
                Log.d("sdakjdsalk", "bura2")
                add(0, messageData)
                Log.d("sdakjdsalk", "$recipientMessages")
                Log.d("sdakjdsalk", "$messageData")
            }
        }


        val chatList = chatListState.value.chatList
        val senderUser =
            matchListState.value.matchList.find { it.id == messageData.senderUserId }

        val mutableChatList = chatList.toMutableList()
        val mutableMatchList = matchListState.value.matchList.toMutableList()
        Log.d("dasljasdldsa", "$mutableMatchList")

        if (messageData.senderUserId != (userDataState.value?.id ?: "")) {

            val chatIndex =
                mutableChatList.indexOfFirst { it.recipientId == messageData.senderUserId }
            val matchIndex = mutableMatchList.indexOfFirst { it.id == messageData.senderUserId }

            Log.d("iqoueuowruwuor", "chatindex = $chatIndex")
            Log.d("iqoueuowruwuor", "matchindex = $matchIndex")
            if (chatIndex != -1) {
                val updatedChatList = mutableChatList[chatIndex].copy(
//                    recipientId = messageData.senderUserId ?: "",
                    lastUpdated = System.currentTimeMillis()
                )
                mutableChatList[chatIndex] = updatedChatList
                _chatListState.value = chatListState.value.copy(
                    chatList = mutableChatList
                )
            } else {
                mutableChatList.add(
                    ChatData(
                        recipientId = senderUser?.id ?: "",
                        lastUpdated = System.currentTimeMillis(),
                        extendedFull = false,
                        recipientUsername = senderUser?.username ?: "",
                        extendPage = 20,
                        recipientImage = senderUser?.image ?: "",
                        backgroundColor = BackgroundColor.DEFAULT,
                        doodleEnabled = true
                    )
                )
                _chatListState.value = chatListState.value.copy(
                    chatList = mutableChatList
                )
            }

            if (matchIndex != -1) {
                val updatedMatchData = mutableMatchList[matchIndex].copy(
                    new = false
                )
                mutableMatchList[matchIndex] = updatedMatchData
                _matchListState.value = matchListState.value.copy(
                    matchList = mutableMatchList
                )
            }
            Log.d("dasljasdldsa", "$mutableMatchList")

        }

        if (messageData.senderUserId != (userDataState.value?.id ?: "")) {
            _unreadMessagesState.value = unreadMessagesState.value.copy(
                size = unreadMessagesState.value.size + 1
            )
        }



        _chatState.value = chatState.value.copy(
            recipientMessages = newList
        )

    }

    private fun typingCommand(senderId: String) {
        typingJob?.cancel()
        typingJob = viewModelScope.launch(Dispatchers.IO) {
            _chatState.value = chatState.value.copy(
                recipientUserId = senderId,
                recipientIsTyping = true
            )

            delay(2000)
            _chatState.value = chatState.value.copy(
                recipientUserId = senderId,
                recipientIsTyping = false
            )
        }
    }

    private suspend fun likeSentCommand(
        newLikeCommand: NewLikeCommand,
        dataStore: NotificationPreferences,
        context: Context
    ) {

        val currentId = userDataState.value?.id

        val newLikesMutable = newLikesState.value.newLikes.toMutableList()
        if (newLikeCommand.recipientUserId == currentId) {
            if (newLikeCommand.senderUserId !in newLikesMutable.map { it.id }) {
                newLikesMutable.add(
                    WhoLikesData(
                        id = newLikeCommand.senderUserId,
                        image = newLikeCommand.senderImage.firstOrNull() ?: "",
                        username = newLikeCommand.senderUsername,
                        age = newLikeCommand.senderAge.toString(),
                        new = true
                    )
                )
                _newLikesState.value = newLikesState.value.copy(
                    newLikes = newLikesMutable
                )
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
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

    }

    private fun unlikeCommand(
        unlikeCommand: UnlikeCommand
    ) {
        val currentId = userDataState.value?.id

        val newLikesMutable = newLikesState.value.newLikes.toMutableList()

        if (unlikeCommand.recipientUserId == currentId) {
            newLikesMutable.removeIf { it.id == unlikeCommand.senderUserId }
            _newLikesState.value = newLikesState.value.copy(
                newLikes = newLikesMutable
            )
        }
    }

    private fun unMatchCommand(
        unMatchCommand: UnMatchCommand
    ) {
        val currentId = userDataState.value?.id
        val newChatListMutable = chatListState.value.chatList.toMutableList()
        val newMatchListMutable = matchListState.value.matchList.toMutableList()

        val unReadMessages = chatState.value.recipientMessages?.filter {
            it.status != MessageStatus.READ &&
                    it.senderUserId == unMatchCommand.senderUserId &&
                    it.recipientUserId == unMatchCommand.recipientUserId
        }

        _unreadMessagesState.value = unreadMessagesState.value.copy(
            size = unreadMessagesState.value.size - (unReadMessages?.size ?: 0)
        )


        val newMessageListMutable = chatState.value.recipientMessages?.toMutableList()
        val filteredList = newMessageListMutable?.filterNot {
            (it.recipientUserId == unMatchCommand.recipientUserId && it.senderUserId == unMatchCommand.senderUserId) ||
                    (it.recipientUserId == unMatchCommand.senderUserId && it.senderUserId == unMatchCommand.recipientUserId)
        }
        _chatState.value = chatState.value.copy(
            recipientMessages = filteredList
        )



        if (unMatchCommand.recipientUserId == currentId) {
            newChatListMutable.removeIf { it.recipientId == unMatchCommand.senderUserId }
            newMatchListMutable.removeIf { it.id == unMatchCommand.senderUserId }
            _chatListState.value = chatListState.value.copy(
                chatList = newChatListMutable
            )
            _matchListState.value = matchListState.value.copy(
                matchList = newMatchListMutable
            )
        } else if (unMatchCommand.senderUserId == currentId) {
            newChatListMutable.removeIf { it.recipientId == unMatchCommand.recipientUserId }
            newMatchListMutable.removeIf { it.id == unMatchCommand.recipientUserId }
            _chatListState.value = chatListState.value.copy(
                chatList = newChatListMutable
            )
            _matchListState.value = matchListState.value.copy(
                matchList = newMatchListMutable
            )

        }
    }

    private fun blockCommand(
        blockCommand: BlockCommand
    ) {
        val currentId = userDataState.value?.id
        val newChatListMutable = chatListState.value.chatList.toMutableList()
        val newMatchListMutable = matchListState.value.matchList.toMutableList()

        val unReadMessages = chatState.value.recipientMessages?.filter {
            it.status != MessageStatus.READ &&
                    it.senderUserId == blockCommand.senderUserId &&
                    it.recipientUserId == blockCommand.recipientUserId
        }

        _unreadMessagesState.value = unreadMessagesState.value.copy(
            size = unreadMessagesState.value.size - (unReadMessages?.size ?: 0)
        )

        val newMessageListMutable = chatState.value.recipientMessages?.toMutableList()
        newMessageListMutable?.removeAll { it.recipientUserId == currentId || it.senderUserId == currentId }
        _chatState.value = chatState.value.copy(
            recipientMessages = newMessageListMutable
        )

        if (blockCommand.recipientUserId == currentId) {
            newChatListMutable.removeIf { it.recipientId == blockCommand.senderUserId }
            newMatchListMutable.removeIf { it.id == blockCommand.senderUserId }
            _chatListState.value = chatListState.value.copy(
                chatList = newChatListMutable
            )
            _matchListState.value = matchListState.value.copy(
                matchList = newMatchListMutable
            )
        } else if (blockCommand.senderUserId == currentId) {

            newChatListMutable.removeIf { it.recipientId == blockCommand.recipientUserId }
            newMatchListMutable.removeIf { it.id == blockCommand.recipientUserId }
            _chatListState.value = chatListState.value.copy(
                chatList = newChatListMutable
            )
            _matchListState.value = matchListState.value.copy(
                matchList = newMatchListMutable
            )
        }
    }


    fun getWhoLikes() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.getWhoLikes()
            if (response.success) {
                _newLikesState.value = newLikesState.value.copy(
                    newLikes = response.whoLikesList
                )
            }
        }
    }

    fun newGotLikedToOld(matchUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val whoLikes = newLikesState.value.newLikes

            val mutableGotLiked = whoLikes.toMutableList()

            val index = mutableGotLiked.indexOfFirst { it.id == matchUserId }

            if (index != -1) { // Ensure the item exists before modifying
                mutableGotLiked[index] = mutableGotLiked[index].copy(new = false)
                _newLikesState.value = newLikesState.value.copy(
                    newLikes = mutableGotLiked
                )
                Log.d("sdkaljdslak", "${newLikesState.value.newLikes}")

                userRepository.updateUserData(
                    userUpdate = UserUpdate(
                        gotLiked = mutableGotLiked
                    )
                )
            }
        }
    }

    private suspend fun gotMatchCommand(
        gotMatchCommand: GotMatchCommand,
        dataStore: NotificationPreferences,
        context: Context
    ) {
        val senderIsCurrent = gotMatchCommand.recipientUserId != userDataState.value?.id
        val recipientId = gotMatchCommand.recipientUserId
        val recipientUsername = gotMatchCommand.recipientUsername
        val recipientImage = gotMatchCommand.recipientImage.firstOrNull() ?: ""

        val senderId = gotMatchCommand.senderUserId
        val senderUsername = gotMatchCommand.senderUsername
        val senderImage = gotMatchCommand.senderImage.firstOrNull() ?: ""

        val mutableMatchList = matchListState.value.matchList.toMutableList()
        mutableMatchList.add(
            MatchedUserData(
                id = if (senderIsCurrent) recipientId else senderId,
                image = if (senderIsCurrent) recipientImage else senderImage,
                username = if (senderIsCurrent) recipientUsername else senderUsername,
                name = "",
                new = true
            )
        )

        val newLikesMutable = newLikesState.value.newLikes.toMutableList()
        if (gotMatchCommand.senderUserId == userDataState.value?.id) {
            newLikesMutable.removeIf { it.id == gotMatchCommand.recipientUserId }
            _newLikesState.value = newLikesState.value.copy(
                newLikes = newLikesMutable
            )
        }

        _matchListState.value = matchListState.value.copy(
            matchList = mutableMatchList
        )

        viewModelScope.launch(Dispatchers.IO) {
            dataStore.getMatchStatus.collect {
                if (it) {
                    showNotification(
                        channelId = "match",
                        context = context,
                        title = "Bober",
                        text = "You've got a new match!",
                        notificationType = NotificationType.MATCH,
                        additional = if (senderIsCurrent) gotMatchCommand.recipientUserId else gotMatchCommand.senderUserId
                    )
                }
            }
        }

    }

    private fun sentEmoteCommand(
        messageData: MessageData,
        dataStore: NotificationPreferences
    ) {
        val recipientMessages = chatState.value.recipientMessages ?: listOf()
        val mutableRecipientMessages = recipientMessages.toMutableList()
        val index = mutableRecipientMessages.indexOfFirst { it.id == messageData.id }
        if (index != -1) {
            val alreadyReacted =
                recipientMessages[index].emoted?.any { it.senderId == messageData.recipientUserId }
            if (messageData.recipientUserId != userDataState.value?.id && getNotificationState.value.onChatScreen == false && messageData.senderUserId == userDataState.value?.id && alreadyReacted == false) {
                viewModelScope.launch {
                    dataStore.getEmoteStatus.collect {
                        if (it) {
                            Log.d("sdajaslkjsa", "Sdjlasjldask")
                            showNotification(
                                channelId = "react",
                                context = context,
                                title = "${messageData.senderUsername}",
                                text = "Reacted ${messageData.recipientUsername} to your message",
                                notificationType = NotificationType.REACT,
                                additional = "${messageData.recipientUserId}"
                            )
                        }
                    }
                }
            }

            val updatedMessage = mutableRecipientMessages[index].copy(
                emoted = messageData.emoted
            )
            mutableRecipientMessages[index] = updatedMessage
            _chatState.value = chatState.value.copy(
                recipientMessages = mutableRecipientMessages
            )
        }
    }

    private fun removeEmoteCommand(
        messageData: MessageData,
    ) {
        val recipientMessages = chatState.value.recipientMessages ?: listOf()
        val mutableRecipientMessages = recipientMessages.toMutableList()
        val index = mutableRecipientMessages.indexOfFirst { it.id == messageData.id }
        if (index != -1) {
            val updatedMessage = mutableRecipientMessages[index].copy(
                emoted = messageData.emoted
            )
            mutableRecipientMessages[index] = updatedMessage
            _chatState.value = chatState.value.copy(
                recipientMessages = mutableRecipientMessages
            )
        }
    }


    private fun noLongerMatchedCommand(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, "You are no longer matched.", Toast.LENGTH_SHORT).show()
        }
    }

    fun enableReply(
        username: String,
        messageId: String,
        messageText: String
    ) {
        clearSelectMessages()
        _replyingState.value = replyingState.value.copy(
            enabled = true,
            username = username,
            messageId = messageId,
            messageText = messageText
        )
    }

    fun closeReply() {
        _replyingState.value = replyingState.value.copy(
            enabled = false,
            username = "",
            messageId = "",
            messageText = ""
        )
    }

    var replyTriggerJob: Job? = null
    fun triggerReplyingMessage(messageId: String) {
        replyTriggerJob?.cancel()
        replyTriggerJob = viewModelScope.launch {
            _replyingState.value = replyingState.value.copy(
                replyingMessageTriggered = true,
                triggeredMessageId = messageId
            )
            Log.d("sdaljdsaljkasd", "${replyingState.value}")
            delay(2000)
            _replyingState.value = replyingState.value.copy(
                replyingMessageTriggered = false,
                triggeredMessageId = ""
            )
            Log.d("sdaljdsaljkasd", "${replyingState.value}")

        }

    }


    fun sendMessage() {
        _emptyChatGif.value = emptyChatGif.value.copy(
            enabled = false
        )
        val recipientUserId = chatState.value.switchedRecipientId
        val replyingMessageId = replyingState.value.messageId

        val replyingMessage =
            if (replyingMessageId.isNotEmpty()) "reply:$replyingMessageId${messageText.value}" else messageText.value
        val newMessage = MessageData(
            text = messageText.value,
            senderUsername = userDataState.value?.username ?: "",
            senderUserId = userDataState.value?.id ?: "",
            recipientUserId = recipientUserId,
            recipientUsername = replyingState.value.username,
            replyingMessageId = replyingMessageId,
            status = MessageStatus.SENDING,
            timestamp = System.currentTimeMillis().toString()
        )

        if (newMessage.text?.isNotBlank() == true) {
            if (recipientUserId in matchListState.value.matchList.map { it.id }) {
                closeReply()
                viewModelScope.launch(Dispatchers.IO) {
                    localMessageRepository.insertMessage(newMessage.toMessageEntity())
                }

                val chatList = chatListState.value.chatList
                val recipientUser =
                    matchListState.value.matchList.find { it.id == newMessage.recipientUserId }

                val mutableChatList = chatList.toMutableList()
                val chatIndex =
                    mutableChatList.indexOfFirst { it.recipientId == newMessage.senderUserId || it.recipientId == newMessage.recipientUserId }

                if (chatIndex != -1) {
                    val updatedChatList = mutableChatList[chatIndex].copy(
                        lastUpdated = System.currentTimeMillis()
                    )
                    mutableChatList[chatIndex] = updatedChatList
                    _chatListState.value = chatListState.value.copy(
                        chatList = mutableChatList
                    )
                } else {
                    mutableChatList.add(
                        ChatData(
                            recipientId = recipientUser?.id ?: "",
                            lastUpdated = System.currentTimeMillis(),
                            extendedFull = false,
                            recipientUsername = recipientUser?.username ?: "",
                            extendPage = 20,
                            recipientImage = recipientUser?.image ?: "",
                            backgroundColor = BackgroundColor.DEFAULT,
                            doodleEnabled = true
                        )
                    )
                    _chatListState.value = chatListState.value.copy(
                        chatList = mutableChatList
                    )
                }


                val recipientMessages = chatState.value.recipientMessages ?: listOf()
                val newList = recipientMessages.toMutableList().apply {
                    add(0, newMessage)
                }

                _chatState.value = chatState.value.copy(
                    recipientMessages = newList,
                )

                viewModelScope.launch(Dispatchers.IO) {
                    if (messageText.value.isNotBlank()) {
                        val result = webSocketRepository.sendMessage(
                            message = replyingMessage
                        )
                        if (result is Resource.Success) {
                            Log.d("dsaljdsdasdsda", "message sent successfully")
                        } else if (result is Resource.Error) {
                            connectToChat()
                        }
                        _messageText.value = ""
                        _gifState.value = false
                    }
                }
            }


        } else {
            Toast.makeText(context, "Can't send empty message", Toast.LENGTH_SHORT).show()
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

    fun onEmptyMessageGif() {
        viewModelScope.launch(Dispatchers.IO) {
            _emptyChatGif.value = emptyChatGif.value.copy(
                gifUrl = tenorRepo.emptyChatGif()
            )
        }
    }


    fun clearEmptyMessages() {
        _chatState.value = chatState.value.copy(
            recipientMessagesEmpty = false
        )
    }



    fun extendMessages(recipientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentPage = chatListState.value.chatList.find { it.recipientId == recipientId }
            val page = currentPage?.extendPage ?: 0
            val recipientUser = chatListState.value.chatList.find { it.recipientId == recipientId }

            val result = messageRepository.getRecipientMessages(
                recipientUserId = recipientId,
                page = page + 1,
                pageSize = chatState.value.pageSize
            )

            if (result.success) {
                val messageList = result.messageList?.map { it.toMessageData() } ?: listOf()
                val mutableChatList = chatListState.value.chatList.toMutableList()
                val index = mutableChatList.indexOfFirst { it.recipientId == recipientId }


                if (result.messageList == null) {
                    mutableChatList[index] = mutableChatList[index].copy(
                        extendedFull = true
                    )
                    _chatListState.value = chatListState.value.copy(
                        chatList = mutableChatList
                    )
                }


                if (recipientUser?.extendedFull == false) {
                    if (index != -1) {
                        mutableChatList[index] = mutableChatList[index].copy(
                            extendPage = page + 1
                        )
                        _chatListState.value = chatListState.value.copy(
                            chatList = mutableChatList
                        )
                    }
                    _chatState.value = chatState.value.copy(
//                    currentPage = chatState.value.currentPage + 1,
                        recipientMessages = chatState.value.recipientMessages?.plus(messageList)
                    )
                }


            }

        }

    }


    fun getPrivMessages(recipientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = messageRepository.getRecipientMessages(
                recipientUserId = recipientId,
                page = 1,
                pageSize = 1
            )
            Log.d("sdlajdlaksdkla", "$response")
            if (response.success) {

                if (response.messageList == null) {
                    _chatState.value = chatState.value.copy(
                        recipientMessagesEmpty = true
                    )
                } else {
                    _chatState.value = chatState.value.copy(
                        recipientMessagesEmpty = false
                    )
                }
            }
        }
    }


    private fun getRecipientMessages(recipientUserId: String) {

        _chatState.value = chatState.value.copy(
            recipientMessagesLoading = true,
            extendMessagesLoading = true
        )

        val chatUser = chatListState.value.chatList.find { it.recipientId == recipientUserId }
        val extendPage = chatUser?.extendPage ?: 0

        viewModelScope.launch(Dispatchers.IO) {
            localMessageRepository.deleteSentMessage()
            val result = messageRepository.getRecipientMessages(
                recipientUserId = recipientUserId,
                page = extendPage,
                pageSize = chatState.value.pageSize
            )
            val localMessages = localMessageRepository.getPendingMessages().reversed()
            val recipientMessages = chatState.value.recipientMessages ?: listOf()
            val pendingMessages =
                localMessages.filter { it.status == MessageStatus.SENDING }
                    .map { it.toMessageData() }

            if (result.success && result.messageList == null) {
                _chatState.value = chatState.value.copy(
                    recipientMessagesLoading = false,
                    extendMessagesLoading = false
                )
            } else if (result.success) {
                Log.d("dsaklasdjljks", "${pendingMessages}")
                val messageList = result.messageList?.map { it.toMessageData() } ?: listOf()

                val messageListResult = result.messageList

//                if (messageListResult?.map { it.id } != null){
//
//                }
//
//                messageListResult?.map { it.toM }

                if (!recipientMessages.any { it.recipientUserId == recipientUserId || it.senderUserId == recipientUserId }) {
                    _chatState.value = chatState.value.copy(
                        recipientMessages =
                        pendingMessages +
                                recipientMessages +
                                messageList,
                        recipientMessagesLoading = false,
                        extendMessagesLoading = false
                    )

                } else {
                    _chatState.value = chatState.value.copy(
                        recipientMessagesLoading = false,
                        extendMessagesLoading = false
                    )
                }

            } else {
                _chatState.value = chatState.value.copy(
                    recipientMessagesEmpty = false,
                    recipientMessagesLoading = false,
                    extendMessagesLoading = false
                )
            }

        }
    }

//    fun onUnMatchStateChange() {
//        _unmatchAlertState.value = !unmatchAlertState.value
//    }

//    fun unMatch(recipientId: String, onSuccess: () -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val response = userRepository.unmatch(recipientId)
//            if (response.success) {
//                val mutableChatList = chatListState.value.chatList.toMutableList()
//                val mutableMatchList = matchListState.value.matchList.toMutableList()
//                mutableChatList.removeIf { it.recipientId == recipientId }
//                mutableMatchList.removeIf { it.id == recipientId }
//
//                _matchListState.value = matchListState.value.copy(
//                    matchList = mutableMatchList
//                )
//                _chatListState.value = chatListState.value.copy(
//                    chatList = mutableChatList
//                )
//                _unmatchAlertState.value = false
//                viewModelScope.launch(Dispatchers.Main) {
//                    onSuccess()
//                }
//            }
//        }
//    }

    fun newMatchToOld(matchUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allMatches = matchListState.value.matchList
            val updatedMatches = allMatches.map { match ->
                if (match.id == matchUserId) {
                    match.copy(new = false)
                } else {
                    match
                }
            }

            userRepository.updateUserData(
                userUpdate = UserUpdate(
                    matchList = updatedMatches
                )
            )
            _matchListState.value = matchListState.value.copy(
                matchList = updatedMatches
            )
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

    fun openEmoteModalSheet(messageData: MessageData) {
        _emoteModalState.value = emoteModalState.value.copy(
            enabled = true,
            messageData = messageData
        )
    }

    fun closeEmoteModalSheet() {
        _emoteModalState.value = emoteModalState.value.copy(
            enabled = false
        )
    }

    fun openBackgroundModalSheet() {
        _backgroundModalSheet.value = true
    }

    fun closeBackgroundModalSheet(
        recipientId: String,
        backgroundColor: BackgroundColor,
        doodle: Boolean
    ) {
        _backgroundModalSheet.value = false
        viewModelScope.launch(Dispatchers.IO) {
            val response = messageRepository.changeChatBackground(
                recipientId = recipientId,
                backgroundColor = backgroundColor,
                doodle = doodle
            )
            Log.d("jklkljasd", "$response")
            Log.d("jklkljasd", "$recipientId, $backgroundColor, $doodle")
        }
    }

    fun changeBackgroundColor(
        backgroundColor: BackgroundColor,
        recipientId: String
    ) {
        val mutableChatList = chatListState.value.chatList.toMutableList()
        val index = mutableChatList.indexOfFirst { it.recipientId == recipientId }

        if (index != -1) {
            mutableChatList[index] = mutableChatList[index].copy(
                backgroundColor = backgroundColor
            )

            _chatListState.value = chatListState.value.copy(
                chatList = mutableChatList
            )
        } else {
            return
        }
    }


    fun changeDoodle(
        doodle: Boolean,
        recipientId: String
    ) {
        val mutableChatList = chatListState.value.chatList.toMutableList()
        val index = mutableChatList.indexOfFirst { it.recipientId == recipientId }
        if (index != -1) {
            mutableChatList[index] = mutableChatList[index].copy(
                doodleEnabled = doodle
            )
            _chatListState.value = chatListState.value.copy(
                chatList = mutableChatList
            )
        }
    }


    fun clearProfileData() {
        getProfileDataJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.clearProfileData()
        }
    }

    fun initializeChatState(
        recipientUserId: String,
        recipientOnlineStatus: Boolean,
        recipientLastSeen: Long
    ) {
        _chatState.value = chatState.value.copy(
            recipientUserId = recipientUserId,
            recipientOnlineStatus = recipientOnlineStatus,
            recipientLastSeen = recipientLastSeen,
        )
    }


}