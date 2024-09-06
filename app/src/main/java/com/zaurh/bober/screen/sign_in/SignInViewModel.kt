package com.zaurh.bober.screen.sign_in

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.domain.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepo: AuthRepo
): ViewModel() {

    val signedInState: StateFlow<Boolean> = authRepo.signedIn
    val loading: StateFlow<Boolean> = authRepo.loading

    private val _signInLoading = mutableStateOf(false)
    val signInLoading: State<Boolean> = _signInLoading

    private val _usernameState = mutableStateOf("")
    val usernameState: State<String> = _usernameState

    private val _passwordState = mutableStateOf("")
    val passwordState: State<String> = _passwordState

    private val _passwordVisible = mutableStateOf(false)
    val passwordVisible: State<Boolean> = _passwordVisible

    fun onUsernameChange(username: String) {
        _usernameState.value = username
    }

    fun onPasswordChange(password: String) {
        _passwordState.value = password
    }

    fun onPasswordVisibilityChange() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun signIn(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _signInLoading.value = true
            val response = authRepo.signIn(usernameState.value, passwordState.value)
            if (response.success) {
                _signInLoading.value = false
            } else {
                _signInLoading.value = false
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}