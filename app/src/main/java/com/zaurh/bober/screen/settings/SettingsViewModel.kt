package com.zaurh.bober.screen.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.screen.settings.state.ShowMeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepo: AuthRepo
): ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData


    private val _showMeState = mutableStateOf(ShowMeState(showMe = userDataState.value?.showMe ?: ShowMe.EVERYONE))
    val showMeState: State<ShowMeState> = _showMeState

    private val _deleteAccountState = mutableStateOf(DeleteAccountState())
    val deleteAccountState: State<DeleteAccountState> = _deleteAccountState

    private var countdownJob: Job? = null



    fun onDeleteAccountAlertChange(){
        _deleteAccountState.value = deleteAccountState.value.copy(
            alert = !deleteAccountState.value.alert,
            countdown = 5,
            enabled = false
        )
        onDeleteAccountCountdown()
    }

    private fun onDeleteAccountCountdown() {
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {
            while (deleteAccountState.value.countdown > 0) {
                delay(1000)
                val currentCountdown = deleteAccountState.value.countdown
                _deleteAccountState.value = deleteAccountState.value.copy(
                    countdown = currentCountdown - 1
                )
            }
            _deleteAccountState.value = deleteAccountState.value.copy(
                enabled = true
            )
        }
    }



    fun onShowMeStateChange(change: Boolean) {
        _showMeState.value = showMeState.value.copy(
            state = change
        )
    }

    fun selectShowMe(showMe: ShowMe){
        _showMeState.value = showMeState.value.copy(
            showMe = showMe
        )
    }

    fun onShowMeChange(){
        _showMeState.value = showMeState.value.copy(
            loading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUserData(
                userUpdate = UserUpdate(
                    showMe = showMeState.value.showMe
                ),
                onSuccess = {
                    _showMeState.value = showMeState.value.copy(
                        state = false
                    )
                }
            )
            _showMeState.value = showMeState.value.copy(
                loading = false
            )
        }
    }

    fun updateAgeRange(ageRangeStart: Float, ageRangeEnd: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUserData(
                userUpdate = UserUpdate(
                    ageRangeStart = ageRangeStart,
                    ageRangeEnd = ageRangeEnd
                )
            )
        }
    }

    fun updateMaximumDistance(maximumDistance: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUserData(
                userUpdate = UserUpdate(
                    maximumDistance = maximumDistance
                )
            )
        }
    }

    fun showFullDistance(show: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUserData(
                userUpdate = UserUpdate(
                    showFullDistance = show
                )
            )
        }
    }

//    fun updateOnlineStatus(online: Boolean) {
//        viewModelScope.launch(Dispatchers.IO) {
//            userRepository.updateUserData(
//                userUpdate = UserUpdate(
//                    online = online,
//                    lastSeen = System.currentTimeMillis()
//                )
//            )
//        }
//
//    }

    fun logout(onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            val response = authRepo.logout()
            if (response.success){
                userRepository.clearProfileData()
                userRepository.clearUserData()
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit){
        _deleteAccountState.value = deleteAccountState.value.copy(
            loading = true
        )
        viewModelScope.launch(Dispatchers.IO){
            userRepository.clearProfileData()
            userRepository.clearUserData()

            val response = userRepository.deleteUserData()
            if (response.success){
                authRepo.logout()
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
                _deleteAccountState.value = deleteAccountState.value.copy(
                    loading = false
                )
            }
        }
        _deleteAccountState.value = deleteAccountState.value.copy(
            loading = false
        )
    }

}