package com.zaurh.bober.screen.blocked_users

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockedUsersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val userListDataState: StateFlow<List<UserData?>> = userRepository.userListData

    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText


    fun onSearch(value: String) {
        _searchText.value = value
    }

    fun unBlockUser(recipientId: String){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.unblock(recipientId)
        }
    }

}