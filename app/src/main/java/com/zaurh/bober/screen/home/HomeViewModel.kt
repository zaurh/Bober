package com.zaurh.bober.screen.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.zaurh.bober.data.requests.PremiumCardRequest
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.screen.pager.state.PremiumCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val webSocketRepository: WebSocketRepository,
    private val authRepo: AuthRepo
): ViewModel() {
    var isRefreshing = mutableStateOf(false)

    val userDataState: StateFlow<UserData?> = userRepository.userData

    private val _premiumCardState = mutableStateOf(PremiumCardState())
    val premiumCardState: State<PremiumCardState> = _premiumCardState

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _connectionEnabled = mutableStateOf(false)
    val connectionEnabled: State<Boolean> = _connectionEnabled


    init {
        startedTemp()
    }

    private fun startedTemp(){
        Log.d("Viewmodelstates", "Home Started")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Viewmodelstates", "Home Ended")
    }


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

    fun openPremiumAlert(
        expiryDate: Int
    ){
        _premiumCardState.value = premiumCardState.value.copy(
            alertState = true,
            congsText = false,
            expiryDate = expiryDate
        )
    }

    fun closePremiumAlert(){
        _premiumCardState.value = _premiumCardState.value.copy(
            alertState = false
        )
    }

//    fun getMatchedUsers(){
//        viewModelScope.launch(Dispatchers.IO) {
//            val matchedUsers = userRepository.getMatchedUsers()
//        }
//    }

    fun takePremiumCard(
        premiumCardRequest: PremiumCardRequest
    ){
        _premiumCardState.value = premiumCardState.value.copy(
            loading = true,
            error = ""
        )
        viewModelScope.launch(Dispatchers.IO){
            val result = userRepository.takePremiumCard(premiumCardRequest)
            if (result.success){
                _premiumCardState.value = premiumCardState.value.copy(
                    alertState = false,
                    loading = false,
                    error = ""
                )
            }else{
                _premiumCardState.value = premiumCardState.value.copy(
                    loading = false,
                    error = result.message
                )
            }
        }
    }

    fun getUserData(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.getUserData()
            if (response.user != null){
                if (response.user.premiumCards?.any { it.received } == true){
                    _premiumCardState.value = premiumCardState.value.copy(
                        congsText = true,
                        alertState = true,
                        error = "",
                        loading = false
                    )
                }
            }
            if (response.user == null){
                authRepo.logout()
            }
        }
    }

//    fun getChatList(){
//        viewModelScope.launch(Dispatchers.IO) {
//            val a = messageRepository.getChatList()
//            Log.d("dsjlkdl", "$a")
//        }
//    }

    fun switchRecipient(recipientUserId: String) {
        Log.d("webSocketBober", "switchedTO: $recipientUserId")
        viewModelScope.launch(Dispatchers.IO) {
            webSocketRepository.switchRecipient(
                newRecipientUserId = recipientUserId
            )
        }
    }



    fun onSearchChange(query: String){
        _searchQuery.value = query
    }


}