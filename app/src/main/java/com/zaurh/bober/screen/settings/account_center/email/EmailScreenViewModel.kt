package com.zaurh.bober.screen.settings.account_center.email

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.screen.settings.account_center.email.state.EmailValidationState
import com.zaurh.bober.screen.settings.account_center.email.state.EmailVerificationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepo: AuthRepo
): ViewModel() {
    val userDataState: StateFlow<UserData?> = userRepository.userData

    private val _emailText = mutableStateOf(userDataState.value?.email ?: "")
    val emailText: State<String> = _emailText

    private val _emailVerificationState = mutableStateOf(EmailVerificationState())
    val emailVerificationState: State<EmailVerificationState> = _emailVerificationState

    private val _emailValidationState = mutableStateOf(EmailValidationState())
    val emailValidationState: State<EmailValidationState> = _emailValidationState


    fun getUserData(){
        viewModelScope.launch(Dispatchers.IO){
            val response = userRepository.getUserData()
            Log.d("dsaldsjaklad", "$response" )
        }
    }

    fun onEmailChange(email: String) {
        _emailText.value = email
        _emailVerificationState.value = emailVerificationState.value.copy(
            message = ""
        )
    }

    fun validateEmailToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _emailValidationState.value = emailValidationState.value.copy(
                loading = true,
                success = false,
                message = ""
            )
            val response = authRepo.validateEmailToken(token)
            _emailValidationState.value = emailValidationState.value.copy(
                loading = false,
                success = response.success,
                message = response.message,
                email = response.email ?: ""
            )
            _emailText.value = response.email ?: ""
            getUserData()
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch(Dispatchers.IO) {
            _emailVerificationState.value = emailVerificationState.value.copy(
                loading = true,
                message = "",
                success = false
            )
            val response = authRepo.sendEmailVerification(emailText.value)
            _emailVerificationState.value = emailVerificationState.value.copy(
                loading = false,
                message = response.message,
                success = response.success
            )
        }
    }
}