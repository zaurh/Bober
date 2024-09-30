package com.zaurh.bober.screen.who_likes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
class WhoLikesViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val whoLikesState: StateFlow<List<WhoLikesData?>> = userRepository.whoLikesList

    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    fun getWhoLikesList(){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getWhoLikes()
        }
    }

    fun onSearch(value: String) {
        _searchText.value = value
    }



}