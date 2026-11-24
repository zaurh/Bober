package com.zaurh.bober.screen.auth.forgot_password

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.domain.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepo: AuthRepo
): ViewModel() {

    private val _fpState = mutableStateOf(FpState())
    val fpState: State<FpState> = _fpState

    init {
        startTemp()
    }

    fun startTemp(){
        Log.d("Viewmodelstates", "Forgot password Started")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Viewmodelstates", "Forgot password Ended")

    }

    fun onEmailChange(email: String){
        _fpState.value = fpState.value.copy(
            emailText = email,
            message = ""
        )
    }

    fun onEmailError(){

    }

    fun resetPassword(){
        viewModelScope.launch(Dispatchers.IO) {
            _fpState.value = fpState.value.copy(
                requestLoading = true
            )
            val response = authRepo.resetPassword(fpState.value.emailText)
            if (response.success){
                _fpState.value = fpState.value.copy(
                    requestLoading = false,
                    requestError = false,
                    requestSuccess = true,
                    message = response.message
                )
            }else{
                _fpState.value = fpState.value.copy(
                    requestLoading = false,
                    requestError = true,
                    requestSuccess = false,
                    message = response.message
                )
            }
        }
    }

}