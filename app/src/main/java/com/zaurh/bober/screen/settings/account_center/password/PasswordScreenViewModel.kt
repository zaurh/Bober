package com.zaurh.bober.screen.settings.account_center.password

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.screen.settings.account_center.password.state.PasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepo: AuthRepo
): ViewModel() {
    val userDataState: StateFlow<UserData?> = userRepository.userData

    private val _passwordState = mutableStateOf(PasswordState())
    val passwordState: State<PasswordState> = _passwordState


    fun onCurrentPasswordChange(password: String) {
        _passwordState.value = passwordState.value.copy(
            currentPassword = password
        )
    }

    fun onCurrentPasswordVisibilityChange() {
        _passwordState.value = passwordState.value.copy(
            currentPasswordVisible = !passwordState.value.currentPasswordVisible
        )
    }

    fun onNewPasswordChange(password: String) {
        _passwordState.value = passwordState.value.copy(
            newPassword = password
        )
    }

    fun onNewPasswordVisibilityChange() {
        _passwordState.value = passwordState.value.copy(
            newPasswordVisible = !passwordState.value.newPasswordVisible
        )
    }


    fun checkCurrentPassword() {
        viewModelScope.launch(Dispatchers.IO) {
            _passwordState.value = passwordState.value.copy(
                updatePasswordLoading = true,
                updatePasswordMessage = "",
                updatePasswordSuccess = false
            )
            val response = authRepo.checkPassword(passwordState.value.currentPassword)
            if (response.success) {
                val updateResponse = authRepo.updatePassword(
                    userId = userDataState.value?.id ?: "",
                    password = passwordState.value.newPassword,
                    confirmPassword = passwordState.value.newPassword
                )
                if (updateResponse.success) {
                    Log.d("asdkjalsdk", "updateresponse: $updateResponse")
                    _passwordState.value = passwordState.value.copy(
                        updatePasswordMessage = updateResponse.message,
                        updatePasswordSuccess = true,
                        updatePasswordLoading = false,
                        currentPassword = "",
                        newPassword = ""
                    )
                }else{
                    Log.d("asdkjalsdk", "updateresponse: $updateResponse")
                    _passwordState.value = passwordState.value.copy(
                        updatePasswordMessage = updateResponse.message,
                        updatePasswordSuccess = false,
                        updatePasswordLoading = false
                    )
                }
            } else {
                _passwordState.value = passwordState.value.copy(
                    updatePasswordSuccess = false,
                    updatePasswordLoading = false,
                    updatePasswordMessage = response.message
                )
            }
        }
    }

    fun resetPassword(){
        viewModelScope.launch(Dispatchers.IO) {
            val email = userDataState.value?.email ?: "not_verified"

            _passwordState.value = passwordState.value.copy(
                resetPasswordLoading = true,
                resetPasswordMessage = "",
                resetPasswordSuccess = false
            )
            val response = authRepo.resetPassword(email)
            if (response.success){
                _passwordState.value = passwordState.value.copy(
                    resetPasswordLoading = false,
                    resetPasswordMessage = response.message,
                    resetPasswordSuccess = true,
                )
            }else{
                _passwordState.value = passwordState.value.copy(
                    resetPasswordLoading = false,
                    resetPasswordMessage = response.message,
                    resetPasswordSuccess = false,
                )
            }
        }
    }


}