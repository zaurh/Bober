package com.zaurh.bober.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.responses.WhoLikesData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val whoLikesState: StateFlow<List<WhoLikesData>> = userRepository.whoLikesList

    fun boberium(){
        viewModelScope.launch(Dispatchers.IO){
            userRepository.boberium()
        }
    }

    fun getWhoLikes(){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getWhoLikes()
        }
    }
}