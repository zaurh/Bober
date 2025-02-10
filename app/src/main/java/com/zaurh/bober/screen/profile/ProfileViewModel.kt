package com.zaurh.bober.screen.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.report.Reason
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val currentUserDataState: StateFlow<UserData?> = userRepository.userData

    val profileDataState: StateFlow<UserData?> = userRepository.profileData

    private val _unMatchLoading = mutableStateOf(false)
    val unMatchLoading: State<Boolean> = _unMatchLoading

    private val _unLikeLoading = mutableStateOf(false)
    val unLikeLoading: State<Boolean> = _unLikeLoading

    private val _reportState = mutableStateOf(ReportState())
    val reportState: State<ReportState> = _reportState

    private val _imageIndex: MutableState<Int?> = mutableStateOf(null)
    val imageIndex: State<Int?> = _imageIndex

    private val _unmatchAlertState = mutableStateOf(false)
    val unmatchAlertState: State<Boolean> = _unmatchAlertState

    init {
        startedTemp()
    }

    private fun startedTemp(){
        Log.d("Viewmodelstates", "Profile Started")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Viewmodelstates", "Profile Ended")
    }
    fun updateIndex(index: Int) {
        _imageIndex.value = index
    }


    var getProfileJob: Job? = null

    fun cancelGetProfileJob(){
        getProfileJob?.cancel()
    }

    fun getProfileID(recipientId: String) {
        getProfileJob = viewModelScope.launch(Dispatchers.IO) {
            userRepository.getProfileID(recipientId)
        }
    }


    fun clearProfileData() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.clearProfileData()
        }
    }

    fun like(recipientId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.like(recipientId)
            if (response.success){
                clearProfileData()
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            }
        }
    }

    fun unLike(recipientId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.unlike(recipientId)
            if (response.success) {
                clearProfileData()
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            }
        }
    }

    fun pass(recipientId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.pass(recipientId)
            Log.d("dslajkdsk", "$response")
            if (response.success){
                clearProfileData()
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            }
            else if (response.message == "User already passed"){
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            }
        }
    }

    fun onUnMatchStateChange() {
        Log.d("dsalkdajklasdklsa", "changed")
        _unmatchAlertState.value = !unmatchAlertState.value
    }

    fun unMatch(recipientId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.unmatch(recipientId)
            if (response.success) {
                _unmatchAlertState.value = false
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            }
        }
    }



//    fun block(recipientId: String, onSuccess: () -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val response = userRepository.block(recipientId)
//            Log.d("sdjakldslajkjklasd", "$response")
//            if (response.success) {
//                _blockAlertState.value = false
//                viewModelScope.launch(Dispatchers.Main) {
//                    onSuccess()
//                }
//            }
//        }
//    }

    fun report(recipientId: String, context: Context) {
        _reportState.value = reportState.value.copy(
            loading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.report(
                recipientId = recipientId,
                reason = reportState.value.selectedReason.displayName,
                optional = reportState.value.optionalReason.ifEmpty { "No optional" }
            )
            if (response.success) {
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, "User has been reported successfully", Toast.LENGTH_SHORT)
                        .show()
                }
                _reportState.value = reportState.value.copy(
                    reportState = false,
                    loading = false,
                    optionalReason = "",
                    selectedReason = Reason.NOT_SELECTED
                )
            } else {
                _reportState.value = reportState.value.copy(
                    error = response.message,
                    loading = false
                )
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun unblock(recipientId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.unblock(recipientId)
            if (response.success) {
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            }
        }
    }

    fun onReportStateChange() {
        _reportState.value = reportState.value.copy(
            reportState = !reportState.value.reportState
        )
    }

    fun onDropdownChange() {
        _reportState.value = reportState.value.copy(
            reasonDropdown = !reportState.value.reasonDropdown
        )
    }

    fun selectReason(reason: Reason) {
        _reportState.value = reportState.value.copy(
            selectedReason = reason
        )
    }

    fun onOptionalChange(reason: String) {
        _reportState.value = reportState.value.copy(
            optionalReason = reason
        )
    }

}