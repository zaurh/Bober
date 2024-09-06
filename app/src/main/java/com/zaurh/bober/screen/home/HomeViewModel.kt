package com.zaurh.bober.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val userListDataState: StateFlow<List<UserData?>> = userRepository.userListData

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery


    fun getUserData(){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserData()
        }
    }

    fun getAllUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getAllUsersData()
        }
    }

    fun newMatchToOld(matchUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allMatches = userDataState.value?.matchList
            val updatedMatches = allMatches?.map { match ->
                if (match.matchUserId == matchUserId) {
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