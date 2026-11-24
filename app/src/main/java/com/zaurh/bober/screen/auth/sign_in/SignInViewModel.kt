package com.zaurh.bober.screen.auth.sign_in

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.domain.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

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

    private val _googleOneTap = MutableStateFlow(false)
        val googleOneTap = _googleOneTap.asStateFlow()

    init {
        startedTemp()
    }

    private fun startedTemp(){
        Log.d("Viewmodelstates", "SignIn Started")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Viewmodelstates", "SignIn Ended")
    }

    fun triggerGoogleOneTap(trigger: Boolean){
        _googleOneTap.value = trigger
    }
    fun onUsernameChange(username: String) {
        _usernameState.value = username
    }

    fun onPasswordChange(password: String) {
        _passwordState.value = password
    }

    fun onPasswordVisibilityChange() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun signInGoogle(
        context: Context,
        token: String
    ){
        viewModelScope.launch(Dispatchers.IO) {
            _signInLoading.value = true
            val response = authRepo.googleVerify(token = token)

            if (response.success) {
                _signInLoading.value = false
                Log.d("dsakjdskal", "$response")
            } else {
                _signInLoading.value = false
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun signIn(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _signInLoading.value = true

            val isUserId = usernameState.value.count() == 36
            val response =
                if (isUserId)
                    authRepo.signIn(
                        userId = usernameState.value,
                        username = "",
                        password = passwordState.value
                    )
                else {
                    authRepo.signIn(
                        username = usernameState.value,
                        password = passwordState.value
                    )

                }
            if (response.success) {
                _signInLoading.value = false
            } else {
                _signInLoading.value = false
                Log.d("ddfjklsd", "${response.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}