package com.zaurh.bober.screen.account

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.responses.WhoLikesData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** The Boberium card dialog: open after activating (with congratulations) or when viewing the card. */
data class BoberiumDialogState(val open: Boolean = false, val justActivated: Boolean = false)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData
    val whoLikesState: StateFlow<List<WhoLikesData>> = userRepository.whoLikesList

    private val _activating = mutableStateOf(false)
    val activating: State<Boolean> = _activating

    private val _boberiumDialog = mutableStateOf(BoberiumDialogState())
    val boberiumDialog: State<BoberiumDialogState> = _boberiumDialog

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    /** Activates the free Boberium card (30 days of premium). */
    fun boberium() {
        if (activating.value) return
        _activating.value = true
        viewModelScope.launch {
            val response = userRepository.boberium()
            _activating.value = false
            if (response.success) {
                _boberiumDialog.value = BoberiumDialogState(open = true, justActivated = true)
            } else {
                _error.value = response.message
            }
        }
    }

    fun openBoberiumCard() {
        _boberiumDialog.value = BoberiumDialogState(open = true)
    }

    fun closeBoberiumDialog() {
        _boberiumDialog.value = BoberiumDialogState()
    }

    fun errorShown() {
        _error.value = null
    }

    fun getWhoLikes(){
        viewModelScope.launch {
            userRepository.getWhoLikes()
        }
    }
}
