package com.zaurh.bober.screen.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
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

    fun storeFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result
                viewModelScope.launch(Dispatchers.IO){
                    val response = authRepo.storeFCMToken(fcmToken)
                    Log.d("fcmTokenRequest", "tokenResponse: $response")
                }
            }else{
                Log.d("fcmTokenRequest", "task not successfull")
            }
        }
    }

    fun getMatchedUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            val matchedUsers = userRepository.getMatchedUsers()
            Log.d("sdfljsdflk", "$matchedUsers")
        }
    }

    fun getUserData(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.getUserData()
            if (response.user == null){
                authRepo.logout()
            }
        }
    }

    fun getChatList(){
        viewModelScope.launch(Dispatchers.IO) {
            val a = messageRepository.getChatList()
            Log.d("dsjlkdl", "$a")
        }
    }

    fun switchRecipient(recipientUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketRepository.switchRecipient(
                newRecipientUserId = recipientUserId
            )
        }
    }

    fun newMatchToOld(matchUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allMatches = matchListState.value
            val updatedMatches = allMatches.map { match ->
                if (match?.id == matchUserId) {
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