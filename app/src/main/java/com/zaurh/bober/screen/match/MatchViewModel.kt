package com.zaurh.bober.screen.match

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
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

    private val _distance: MutableState<Int> = mutableIntStateOf(0)
    val distance: State<Int> = _distance


    fun getBobers(){
        viewModelScope.launch(Dispatchers.IO) {
            val a = userRepository.getBobers()
            Log.d("getBobers", "${a.boberDataList}")
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

    fun decryptLocation(recipientLocation: String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.decryptLocation(recipientLocation)
            _distance.value = response.distance
            Log.d("dskjldslkjsd", "$response")
        }
    }


}

