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
import com.zaurh.bober.data.message.room.toMessageData
import com.zaurh.bober.data.repository.MessageEvent
import com.zaurh.bober.data.responses.MatchedUserData
import com.zaurh.bober.data.responses.WhoLikesData
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
import com.zaurh.bober.model.toLastMessageData
import com.zaurh.bober.model.toMessageEntity
import com.zaurh.bober.screen.chat.state.ChatListState
import com.zaurh.bober.screen.chat.state.ChatState
import com.zaurh.bober.screen.chat.state.EmptyGifState
import com.zaurh.bober.screen.chat.state.MatchListState
import com.zaurh.bober.screen.chat.state.NewLikesState
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
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _emptyChatGif = mutableStateOf(EmptyGifState())
    val emptyChatGif: State<EmptyGifState> = _emptyChatGif

    private val _gotMatch = mutableStateOf(GotMatchState())
    val gotMatch: State<GotMatchState> = _gotMatch


    private val _modalSheetState: MutableState<Boolean> = mutableStateOf(false)
    val modalSheetState: State<Boolean> = _modalSheetState

    val profileDataState: StateFlow<UserData?> = userRepository.profileData

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _tenorQuery = mutableStateOf("")
    val tenorQuery: State<String> = _tenorQuery

    private val _chatState = mutableStateOf(ChatState())
    val chatState: State<ChatState> = _chatState

    private val _chatListState = mutableStateOf(ChatListState())
    val chatListState: State<ChatListState> = _chatListState

    private val _matchListState = mutableStateOf(MatchListState())
    val matchListState: State<MatchListState> = _matchListState

    private val _newLikesState = mutableStateOf(NewLikesState())
    val newLikesState: State<NewLikesState> = _newLikesState

    private val _unreadMessagesState = mutableStateOf(UnreadMessagesState())
    val unreadMessagesState: State<UnreadMessagesState> = _unreadMessagesState

    private var typingJob: Job? = null
    private var searchGifJob: Job? = null


    init {
        observeNetworkChanges()
    }

    fun getMatchList() {
        Log.d("boberFunction", "Get match list called")
        _matchListState.value = matchListState.value.copy(
            loading = true,
            empty = false
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

    fun getChatList() {
        Log.d("boberFunction", "Get chat list called")

        _chatListState.value = chatListState.value.copy(
            loading = true,
            empty = false
        )
        viewModelScope.launch(Dispatchers.IO) {
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


    fun getLastMessages() {
        if (chatState.value.lastMessages.isEmpty()) {
            _chatState.value = chatState.value.copy(
                isLoading = true
            )
            viewModelScope.launch(Dispatchers.IO) {
                val response = messageRepository.getLastMessages()
                if (response.success) {
                    _chatState.value = chatState.value.copy(
                        lastMessages = response.lastMessages,
                        isLoading = false
                    )
                } else {
                    _chatState.value = chatState.value.copy(
                        lastMessages = userDataState.value?.lastMessages ?: listOf(),
                        isLoading = false
                    )
                }
            }
        }

    }

    private fun observeNetworkChanges() {
        viewModelScope.launch {
            connectivityObserver.observeNetworkStatus().collectLatest { isConnected ->
                if (isConnected) {
                    Log.d("webSocketBober", "Internet connected")
                    if (!chatState.value.socketIsObserving && chatState.value.internetClosed && chatState.value.initialized) {
                        connectToChat()
                        Log.d("websocketbober", "1212")
                        delay(1000)
                        connectToChat()
                        Log.d("websocketbober", "22222")

                        Log.d("websocketbober", "${switchedRecipient.value}")

                    }
                } else {
                    Log.d("webSocketBober", "Internet disconnected")
                    webSocketRepository.closeSession()
                    _chatState.value = chatState.value.copy(
                        socketIsObserving = false,
                        internetClosed = true
                    )
                    Log.d("webSocketBober", "Websocket closed")
                }
            }
        }
    }

    fun closeSession() {
        _chatState.value = chatState.value.copy(
            socketIsObserving = false
        )
        viewModelScope.launch {
            webSocketRepository.closeSession()
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

    fun switchRecipientInState(recipientId: String) {
        _chatState.value = chatState.value.copy(
            switchedRecipientId = recipientId
        )
    }

    suspend fun sendPendingMessages() {
        Log.d("webSocketBober", "Resourse success websocket")
        val localMessages = localMessageRepository.getPendingMessages()
        val pendingMessages = localMessages.filter { it.status == MessageStatus.SENDING }
        Log.d("pendingBober", "${localMessageRepository.getPendingMessages()}")
        pendingMessages.forEach {
            webSocketRepository.switchRecipient(it.recipientUserId ?: "")
            val a = webSocketRepository.sendMessage(it.text ?: "")
            if (a is Resource.Success) {
                localMessageRepository.deleteSentMessage()
            }
        }
        Log.d("pendingBober", "${localMessageRepository.getPendingMessages()}")

    }

    fun connectToChat() {
        val dataStore = NotificationPreferences(context)

        Log.d("webSocketBober", "Connected Websocket")
        viewModelScope.launch {
            webSocketRepository.closeSession()
            val response = webSocketRepository.initSession()
            when (response) {
                is Resource.Success -> {
                    sendPendingMessages()
                    _chatState.value = chatState.value.copy(lastMessages = listOf())
                    getLastMessages()

                    if (switchedRecipient.value != "") {
                        webSocketRepository.switchRecipient(switchedRecipient.value)
                    }
                    _chatState.value = chatState.value.copy(socketIsObserving = true)

                    webSocketRepository.observeMessages()
                        .onEach { event ->
                            when (event) {
                                is MessageEvent.SwitchedRecipient -> {}
                                is MessageEvent.Typing -> typingCommand(event.senderUserId)
                                is MessageEvent.NoLongerMatched -> noLongerMatchedCommand(context)
                                is MessageEvent.LikeSent -> likeSentCommand(
                                    newLikeCommand = event.data,
                                    dataStore = dataStore,
                                    context = context
                                )

                                is MessageEvent.UnlikeSent -> unlikeCommand(
                                    unlikeCommand = event.data
                                )

                                is MessageEvent.UnMatchSent -> unMatchCommand(
                                    unMatchCommand = event.data
                                )

                                is MessageEvent.BlockSent -> blockCommand(
                                    blockCommand = event.data
                                )

                                is MessageEvent.GotMatch -> gotMatchCommand(
                                    event.data,
                                    dataStore,
                                    context
                                )

                                is MessageEvent.DeliveredMessage -> deliveredCommand(event.messageData)
                                is MessageEvent.ReadMessages -> readMessagesCommand(
                                    event.messageData
                                )

                                is MessageEvent.TriggerOnline -> triggerOnlineCommand(
                                    recipientId = event.recipientUserId,
                                    lastSeen = event.lastSeen,
                                    onlineStatus = event.onlineStatus
                                )

                                is MessageEvent.GenericMessage -> sendMessageCommand(
                                    event.messageData,
                                    context
                                )
                            }
                        }
                        .launchIn(viewModelScope)
                }

                is Resource.Error -> { /* Handle error */
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

    fun loadChatData(recipientUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val getMessagesJob = async { getRecipientMessages(recipientUserId) }
            val getProfileJob = async { getProfileData(recipientUserId) }

            getMessagesJob.await()
            getProfileJob.await()
        }
    }


    var getProfileDataJob: Job? = null

    fun getProfileData(recipientId: String) {

        getProfileDataJob = viewModelScope.launch(Dispatchers.IO) {
            _chatState.value = chatState.value.copy(
                recipientDataIsLoading = true
            )
            val response = userRepository.getProfileID(recipientId)
            if (response.success) {
                _chatState.value = chatState.value.copy(
                    recipientDataIsLoading = false,
                    recipientDataError = false
                )
            } else {
                if (getProfileDataJob?.isActive == true) {
                    _chatState.value = chatState.value.copy(
                        recipientDataIsLoading = false,
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


    fun tempGetRoomMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pendingMessages = localMessageRepository.getPendingMessages()
                Log.d("PendingMessages", "$pendingMessages")


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
            updateLastMessages(messageData = messageData, status = MessageStatus.DELIVERED)


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

            Log.d("sdakldsaljk", "unread: $unReadMessages")
            Log.d("sdakldsaljk", "senderId: $senderId")
            Log.d("sdakldsaljk", "recipientId: $unReadMessages")

            _unreadMessagesState.value = unreadMessagesState.value.copy(
                size = unreadMessagesState.value.size - (unReadMessages?.size ?: 0)
            )

            val lastMessage =
                chatState.value.lastMessages.find { it.recipientUserId == messageData.senderUserId || it.senderUserId == messageData.senderUserId }


            updateLastMessages(
                messageData = messageData,
                status = MessageStatus.READ
            )

            if (unReadMessages?.isNotEmpty() == true) {
                // Update the status of undelivered messages
                val recipientMessages = chatState.value.recipientMessages ?: listOf()
                val filteredMessages =
                    recipientMessages.filter { it.senderUserId == recipientId }
                val updatedMessages = recipientMessages.map { message ->
                    if (message.status == MessageStatus.DELIVERED && message.senderUserId == recipientId) {
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


    fun updateLastMessages(
        messageData: MessageData,
        status: MessageStatus
    ) {
        val currentUserId = userDataState.value?.id ?: ""
        val senderId = messageData.senderUserId ?: ""
        val recipientUserId = messageData.recipientUserId ?: ""
//        Log.d("UpdateLastMessage", "This function first: ${messageData}")

        if (recipientUserId.isNotEmpty()) {
//            Log.d("UpdateLastMessage", "This function called: ${messageData}")
            Log.d("UpdateLastMessage", "senderId: $currentUserId")

            val lastMessageList = chatState.value.lastMessages

            val lastMessage = lastMessageList.find {
                it.recipientUserId == recipientUserId && it.senderUserId == senderId ||
                        it.recipientUserId == senderId && it.senderUserId == recipientUserId
            }

            lastMessage?.let {

                Log.d("sdkaljdjlksa", "$messageData")
                val mutableLastMessages = lastMessageList.toMutableList()
                mutableLastMessages.remove(it)
                when {
                    messageData.text?.startsWith("switched to:") == true || messageData.text == "deliveredMessage:" || messageData.text?.startsWith(
                        "readMessages:"
                    ) == true -> {
                        mutableLastMessages.add(
                            it.copy(
                                messageStatus = status
                            )
                        )
                    }

                    else -> {
//                        Log.d("asdjsaddjhskadhjsadkjhas:", "$messageData")
                        mutableLastMessages.add(
                            it.copy(
                                senderUserId = senderId,
                                recipientUserId = recipientUserId,
                                messageText = messageData.text ?: "",
                                messageTime = System.currentTimeMillis().toString(),
                                messageStatus = messageData.status
                            )
                        )
                    }
                }



                _chatState.value = chatState.value.copy(
                    lastMessages = mutableLastMessages
                )
            }

        }

    }

    private fun sendMessageCommand(messageData: MessageData, context: Context) {
        val recipientId = getNotificationState.value.recipientId
        val dataStore = NotificationPreferences(context)
        val messageSenderIsCurrent = messageData.senderUserId == userDataState.value?.id
        val chatScreenWithRecipient =
            (recipientId == messageData.senderUserId || recipientId == messageData.recipientUserId) && getNotificationState.value.onChatScreen == true

        val typing = messageData.text == "typing..."
        if (!messageSenderIsCurrent && !chatScreenWithRecipient && !typing) {
            viewModelScope.launch(Dispatchers.IO) {
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

        val recipientMessages = chatState.value.recipientMessages ?: listOf()
        val newList = recipientMessages.toMutableList().apply {
            val existingMessageIndex =
                indexOfFirst { it.text == messageData.text && it.status == MessageStatus.SENDING } // Check by ID
            if (existingMessageIndex != -1) {
                // Update the status of the existing message
                this[existingMessageIndex] =
                    this[existingMessageIndex].copy(status = messageData.status)
            } else {
                add(0, messageData)
            }
        }

        updateLastMessages(
            messageData = messageData,
            status = messageData.status
        )

        val chatList = chatListState.value.chatList
        val senderUser =
            matchListState.value.matchList.find { it.id == messageData.senderUserId }


        val mutableChatList = chatList.toMutableList()
        val mutableMatchList = matchListState.value.matchList.toMutableList()
        Log.d("dasljasdldsa", "$mutableMatchList")

        if (messageData.senderUserId != (userDataState.value?.id ?: "")) {
            if (messageData.senderUserId in chatList.map { it.recipientId }) {
                mutableChatList.removeIf { it.recipientId == messageData.senderUserId }
            }
            val index = mutableMatchList.indexOfFirst { it.id == messageData.senderUserId }
            Log.d("dasljasdldsa", "$index")

            if (index != -1){
                val updatedMatchData = mutableMatchList[index].copy(
                    new = false
                )
                mutableMatchList[index] = updatedMatchData
                _matchListState.value = matchListState.value.copy(
                    matchList = mutableMatchList
                )
            }
            Log.d("dasljasdldsa", "$mutableMatchList")


            mutableChatList.add(
                0,
                ChatData(
                    recipientId = messageData.senderUserId ?: "",
                    recipientImage = senderUser?.image ?: "",
                    recipientUsername = senderUser?.username ?: "",
                    lastUpdated = System.currentTimeMillis()
                )
            )
            _chatListState.value = chatListState.value.copy(
                chatList = mutableChatList
            )

        }

        if (messageData.senderUserId != (userDataState.value?.id ?: "")) {
            _unreadMessagesState.value = unreadMessagesState.value.copy(
                size = unreadMessagesState.value.size + 1
            )
        }

        viewModelScope.launch {
            localMessageRepository.deleteMessage(messageData.text ?: "")
            localMessageRepository.insertMessage(messageData.toMessageEntity())
        }

        if (getNotificationState.value.onChatScreen == true) {
            _chatState.value = chatState.value.copy(
                recipientMessages = newList
            )
        }


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

        if (unMatchCommand.recipientUserId == currentId) {
            newChatListMutable.removeIf { it.recipientId == unMatchCommand.senderUserId }
            newMatchListMutable.removeIf { it.id == unMatchCommand.senderUserId }
            _chatListState.value = chatListState.value.copy(
                chatList = newChatListMutable
            )
            _matchListState.value = matchListState.value.copy(
                matchList = newMatchListMutable
            )
        }
        else if (unMatchCommand.senderUserId == currentId){
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

        if (blockCommand.recipientUserId == currentId) {
            newChatListMutable.removeIf { it.recipientId == blockCommand.senderUserId }
            newMatchListMutable.removeIf { it.id == blockCommand.senderUserId }
            _chatListState.value = chatListState.value.copy(
                chatList = newChatListMutable
            )
            _matchListState.value = matchListState.value.copy(
                matchList = newMatchListMutable
            )
        }
        else if (blockCommand.senderUserId == currentId){

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
            val updatedGotLiked = whoLikes.map { whoLikesData ->
                if (whoLikesData.id == matchUserId) {
                    whoLikesData.copy(new = false)
                } else {
                    whoLikesData
                }
            }
            userRepository.updateUserData(
                userUpdate = UserUpdate(
                    gotLiked = updatedGotLiked
                )
            )
            _newLikesState.value = newLikesState.value.copy(
                newLikes = updatedGotLiked
            )

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
            if (!senderIsCurrent) {

                _gotMatch.value = gotMatch.value.copy(
                    gotMatch = true,
                    recipientId = recipientId ?: ""
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

    }

    private fun noLongerMatchedCommand(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, "You are no longer matched.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendMessage() {
        _emptyChatGif.value = emptyChatGif.value.copy(
            enabled = false
        )

        val recipientUserId = chatState.value.switchedRecipientId
        val newMessage = MessageData(
            text = messageText.value,
            senderUsername = userDataState.value?.username ?: "",
            senderUserId = userDataState.value?.id ?: "",
            recipientUserId = recipientUserId,
            status = MessageStatus.SENDING,
            timestamp = System.currentTimeMillis().toString()
        )

        if (newMessage.text != "") {
            if (recipientUserId in matchListState.value.matchList.map { it.id }) {

                viewModelScope.launch(Dispatchers.IO) {
                    localMessageRepository.insertMessage(newMessage.toMessageEntity())
                }

                val chatList = chatListState.value.chatList
                val recipientUser =
                    matchListState.value.matchList.find { it.id == newMessage.recipientUserId }
                val chatUserId = chatList.map { it.recipientId }

                val mutableChatList = chatList.toMutableList()
                if (newMessage.recipientUserId in chatUserId || newMessage.senderUserId in chatUserId) {
                    mutableChatList.removeIf { it.recipientId == newMessage.senderUserId || it.recipientId == newMessage.recipientUserId }
                }

                mutableChatList.add(
                    0,
                    ChatData(
                        recipientId = newMessage.recipientUserId ?: "",
                        recipientImage = recipientUser?.image ?: "",
                        recipientUsername = recipientUser?.username ?: "",
                        lastUpdated = System.currentTimeMillis()
                    )
                )
                _chatListState.value = chatListState.value.copy(
                    chatList = mutableChatList
                )


                val recipientMessages = chatState.value.recipientMessages ?: listOf()
                val newList = recipientMessages.toMutableList().apply {
                    add(0, newMessage)
                }

                val mutableLastMessages = chatState.value.lastMessages.toMutableList()
                mutableLastMessages.removeIf { it.recipientUserId == recipientUserId || it.senderUserId == recipientUserId }
                mutableLastMessages.add(newMessage.toLastMessageData())

                _chatState.value = chatState.value.copy(
                    recipientMessages = newList,
                    lastMessages = mutableLastMessages
                )
            }

            viewModelScope.launch(Dispatchers.IO) {
                if (messageText.value.isNotBlank()) {
                    val result = webSocketRepository.sendMessage(messageText.value)
                    if (result is Resource.Success) {
                        Log.d("dsaljdsdasdsda", "message sent successfully")
                    } else if (result is Resource.Error) {
                        connectToChat()
                    }
                    _messageText.value = ""
                    _gifState.value = false
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

    var getRecipientMessagesJob: Job? = null

    fun clearRecipientMessages() {
        getRecipientMessagesJob?.cancel()
        _chatState.value = chatState.value.copy(
            recipientMessages = listOf(),
            currentPage = 0
        )
    }

    fun getRecipientMessages(recipientUserId: String) {
//        getRecipientMessagesJob?.cancel()
        _chatState.value = chatState.value.copy(
            recipientMessagesLoading = true,
            extendMessagesLoading = true
        )

        getRecipientMessagesJob = viewModelScope.launch(Dispatchers.IO) {
            localMessageRepository.deleteSentMessage()
            val result = messageRepository.getRecipientMessages(
                recipientUserId = recipientUserId,
                page = chatState.value.currentPage,
                pageSize = chatState.value.pageSize
            )
            val localMessages = localMessageRepository.getPendingMessages().reversed()
            val pendingMessages =
                localMessages.filter { it.status == MessageStatus.SENDING && it.recipientUserId == recipientUserId }

            if (result.success && result.messageList == null) {
                _chatState.value = chatState.value.copy(
                    recipientMessagesEmpty = true,
                    recipientMessagesLoading = false,
                    extendMessagesLoading = false
                )
            } else if (result.success) {
                _chatState.value = chatState.value.copy(
                    currentPage = chatState.value.currentPage + 1, // Increment current page
                    recipientMessages =
                    pendingMessages.map { it.toMessageData() } +
                            (chatState.value.recipientMessages
                                ?: listOf()) +
                            (result.messageList?.map { it.toMessageData() }
                                ?: listOf()), // Add pending messages
                    recipientMessagesEmpty = false,
                    recipientMessagesLoading = false,
                    extendMessagesLoading = false
                )
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