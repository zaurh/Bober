package com.zaurh.bober.screen.liked_users

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.screen.liked_users.state.LikedUsersState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikedUsersViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
//    val likedUserList: StateFlow<List<LikedUserData?>> = userRepository.likedUserList

    private val _likedUsersState = mutableStateOf(LikedUsersState())
    val likedUsersState: State<LikedUsersState> = _likedUsersState


    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    fun getLikedUsers(){
        viewModelScope.launch(Dispatchers.IO) {

            _likedUsersState.value = likedUsersState.value.copy(
                loading = true,
                empty = false
            )
            val response = userRepository.getLikedUsers()
            if (response.success){
                _likedUsersState.value = likedUsersState.value.copy(
                    loading = false,
                    likedUsers = response.likedUserList,
                    error = ""
                )
            }else{
                _likedUsersState.value = likedUsersState.value.copy(
                    loading = false,
                    likedUsers = emptyList(),
                    error = response.message,
                    empty = false
                )
            }
        }
    }

    fun onSearch(value: String) {
        _searchText.value = value
    }



}