package com.zaurh.bober.screen.match

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.BoberData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** The discover deck: people to swipe on, and like / pass actions. */
@HiltViewModel
class MatchViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val matchListState: StateFlow<List<BoberData?>> = userRepository.boberDataList

    private val _loading = mutableStateOf(false)
    /** True while the deck is being fetched and there's nothing to show yet. */
    val loading: State<Boolean> = _loading

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    /** Errors from the backend to show to the user, e.g. "You don't have enough like token." */
    val messages: SharedFlow<String> = _messages

    fun getBobers() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = matchListState.value.isEmpty()
            userRepository.getBobers()
            _loading.value = false
        }
    }

    fun sendLike(recipientId: String) {
        if (recipientId.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.like(recipientId)
            if (!response.success) _messages.emit(response.message)
        }
    }

    fun pass(recipientId: String) {
        if (recipientId.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.pass(recipientId)
            if (!response.success) _messages.emit(response.message)
        }
    }
}
