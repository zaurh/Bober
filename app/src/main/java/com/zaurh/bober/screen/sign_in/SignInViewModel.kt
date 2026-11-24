package com.zaurh.bober.screen.sign_in

import com.zaurh.bober.util.Constants
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.CredentialManager
import android.util.Log
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

    private val _googleLoading = mutableStateOf(false)
    val googleLoading: State<Boolean> = _googleLoading

    /**
     * Shows Google's account picker, then signs in to the backend with the ID token.
     * [activityContext] must be an Activity: Credential Manager draws its UI over it.
     */
    fun signInWithGoogle(activityContext: Context) {
        if (googleLoading.value) return
        viewModelScope.launch {
            _googleLoading.value = true
            val message = try {
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(GetSignInWithGoogleOption.Builder(Constants.GOOGLE_WEB_CLIENT_ID).build())
                    .build()
                val credential = CredentialManager.create(activityContext).getCredential(activityContext, request).credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val response = withContext(Dispatchers.IO) { authRepo.signInWithGoogle(idToken) }
                    if (response.success) null else response.message
                } else {
                    "Google sign-in returned an unexpected credential."
                }
            } catch (e: GetCredentialCancellationException) {
                null // The user closed the picker.
            } catch (e: NoCredentialException) {
                "No Google account found on this device. Add one in Settings and try again."
            } catch (e: GetCredentialException) {
                Log.w(TAG, "Google sign-in failed", e)
                "Google sign-in failed. Please try again."
            } catch (e: GoogleIdTokenParsingException) {
                Log.w(TAG, "Bad Google ID token", e)
                "Google sign-in failed. Please try again."
            }
            _googleLoading.value = false
            message?.let { Toast.makeText(activityContext, it, Toast.LENGTH_LONG).show() }
        }
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


    private companion object {
        const val TAG = "SignInViewModel"
    }
}
