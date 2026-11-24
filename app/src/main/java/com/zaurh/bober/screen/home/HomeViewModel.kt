package com.zaurh.bober.screen.home

import kotlinx.coroutines.coroutineScope
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.responses.MatchedUserData
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val webSocketRepository: WebSocketRepository,
    private val authRepo: AuthRepo
): ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val matchListState: StateFlow<List<MatchedUserData?>> = userRepository.matchedUserList

    val chatList: StateFlow<List<ChatData?>> = messageRepository.chatList

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery


    fun getMatchedUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getMatchedUsers()
        }
    }

    private val _refreshing = mutableStateOf(false)
    val refreshing: State<Boolean> = _refreshing

    /** Pull to refresh: reloads the profile, chats, matches and likes together. */
    fun refresh() {
        if (refreshing.value) return
        _refreshing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            coroutineScope {
                launch { userRepository.getUserData() }
                launch { messageRepository.getChatList() }
                launch { userRepository.getMatchedUsers() }
                launch { userRepository.getWhoLikes() }
            }
            _refreshing.value = false
        }
    }

    fun getUserData(){
        viewModelScope.launch(Dispatchers.IO) {
            // An expired session is handled centrally (AuthInterceptor → AuthRepo.logout);
            // a network error must not sign the user out.
            userRepository.getUserData()
        }
    }

    fun getChatList(){
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.getChatList()
        }
    }

    fun switchRecipient(recipientUserId: String) {
        webSocketRepository.switchRecipient(recipientUserId)
    }

    fun newMatchToOld(matchUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allMatches = matchListState.value.filterNotNull()
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

        }
    }

    fun onSearchChange(query: String){
        _searchQuery.value = query
    }


}