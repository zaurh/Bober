package com.zaurh.bober.screen.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.BoberData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val matchListState: StateFlow<List<BoberData?>> = userRepository.boberDataList


    fun getBobers(){
        viewModelScope.launch(Dispatchers.IO) {
            val a = userRepository.getBobers()
        }
    }

    fun sendLike(recipientId: String){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.like(recipientId)
        }
    }

    fun pass(recipientId: String){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.pass(recipientId)
        }
    }
    fun encryptLocation(location: String){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.encryptLocation(location)
        }
    }


}

