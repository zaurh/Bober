package com.zaurh.bober.screen.reset_password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepo: AuthRepo
) : ViewModel() {

    val signedInState: StateFlow<Boolean> = authRepo.signedIn

    val userDataState: StateFlow<UserData?> = userRepository.userData

    private val _passwordState = mutableStateOf(RpPasswordState())
    val passwordState: State<RpPasswordState> = _passwordState

    fun onNewPasswordChange(password: String){
        _passwordState.value = passwordState.value.copy(
            newPassword = password,
            updatePasswordMessage = ""
        )
    }

    fun onNewPasswordVisibilityChange() {
        _passwordState.value = passwordState.value.copy(
            newPasswordVisible = !passwordState.value.newPasswordVisible
        )
    }

    fun onNewPasswordAgainChange(password: String){
        _passwordState.value = passwordState.value.copy(
            newPasswordAgain = password,
            updatePasswordMessage = ""
        )
    }

    fun onNewPasswordAgainVisibilityChange() {
        _passwordState.value = passwordState.value.copy(
            newPasswordAgainVisible = !passwordState.value.newPasswordAgainVisible
        )
    }

    fun onPasswordError(){
        if (passwordState.value.newPassword != passwordState.value.newPasswordAgain){
            _passwordState.value = passwordState.value.copy(
                error = true
            )
        }
    }

    fun updatePassword(
        userId: String,
        onSuccess: () -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            _passwordState.value = passwordState.value.copy(
                updatePasswordLoading = true
            )
            val response = authRepo.updatePassword(
                userId = userId,
                password = passwordState.value.newPassword,
                confirmPassword = passwordState.value.newPasswordAgain
            )
            if (response.success){
                _passwordState.value = passwordState.value.copy(
                    updatePasswordLoading = false,
                    updatePasswordMessage = response.message
                )
                viewModelScope.launch(Dispatchers.Main){
                    onSuccess()
                }
            }else{
                _passwordState.value = passwordState.value.copy(
                    updatePasswordLoading = false,
                    updatePasswordMessage = response.message
                )
            }
        }
    }

    fun validateToken(token: String){
        viewModelScope.launch(Dispatchers.IO){
            _passwordState.value = passwordState.value.copy(
                validateTokenLoading = true
            )
            val response = authRepo.validatePasswordToken(token)
            if (response.success){
                _passwordState.value = passwordState.value.copy(
                    validationSuccess = true,
                    validateTokenLoading = false
                )
            }else{
                _passwordState.value = passwordState.value.copy(
                    validationSuccess = false,
                    validateTokenLoading = false
                )
            }
        }
    }

}