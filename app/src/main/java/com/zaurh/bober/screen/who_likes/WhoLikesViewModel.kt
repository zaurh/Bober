package com.zaurh.bober.screen.who_likes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WhoLikesViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText


    fun onSearch(value: String) {
        _searchText.value = value
    }

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val userListDataState: StateFlow<List<UserData?>> = userRepository.userListData

}