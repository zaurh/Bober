package com.zaurh.bober.screen.who_likes

import android.util.Log
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

    val userDataState: StateFlow<UserData?> = userRepository.userData
//    val whoLikesState: StateFlow<List<WhoLikesData?>> = userRepository.whoLikesList

    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    init {
        startTemp()
    }

    fun startTemp(){
        Log.d("Viewmodelstates", "WhoLikes Started")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Viewmodelstates", "WhoLikes Ended")

    }

//    fun getWhoLikesList(){
//        viewModelScope.launch(Dispatchers.IO) {
//            userRepository.getWhoLikes()
//        }
//    }

    fun onSearch(value: String) {
        _searchText.value = value
    }

//    fun newGotLikedToOld(matchUserId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val whoLikes = whoLikesState.value
//            val updatedGotLiked = whoLikes.map { whoLikesData ->
//                if (whoLikesData?.id == matchUserId) {
//                    whoLikesData.copy(new = false)
//                } else {
//                    whoLikesData
//                }
//            }
//            userRepository.updateUserData(
//                userUpdate = UserUpdate(
//                    gotLiked = updatedGotLiked
//                )
//            )
//
//        }
//    }



}