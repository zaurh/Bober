package com.zaurh.bober.screen.settings.email

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Email address entry in Settings: asking the backend to send a verification link. */
data class EmailDialogState(
    val open: Boolean = false,
    val email: String = "",
    val sending: Boolean = false,
    /** The backend's answer to the last send, shown under the field. */
    val message: String? = null,
    val sent: Boolean = false,
)

/** Result of opening a verification link from the email. */
data class EmailLinkState(
    val verifying: Boolean = false,
    val success: Boolean = false,
    val message: String? = null,
)

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _dialog = mutableStateOf(EmailDialogState())
    val dialog: State<EmailDialogState> = _dialog

    private val _link = mutableStateOf(EmailLinkState())
    val link: State<EmailLinkState> = _link

    fun openDialog() {
        _dialog.value = EmailDialogState(open = true, email = userRepository.userData.value?.email.orEmpty())
    }

    fun closeDialog() {
        _dialog.value = EmailDialogState()
    }

    fun onEmailChange(email: String) {
        _dialog.value = _dialog.value.copy(email = email, message = null, sent = false)
    }

    fun sendVerification() {
        val email = _dialog.value.email.trim()
        if (_dialog.value.sending) return
        _dialog.value = _dialog.value.copy(sending = true, message = null)
        viewModelScope.launch {
            val response = userRepository.sendEmailVerification(email)
            _dialog.value = _dialog.value.copy(sending = false, message = response.message, sent = response.success)
        }
    }

    /** Called with the token from a `https://bober.zaurhasanov.com/verify_email?token=…` link. Requires a signed-in user. */
    fun verifyLinkToken(token: String) {
        if (_link.value.verifying) return
        _link.value = EmailLinkState(verifying = true)
        viewModelScope.launch {
            val response = userRepository.verifyEmail(token)
            _link.value = EmailLinkState(success = response.success, message = response.message)
        }
    }

    fun dismissLinkResult() {
        _link.value = EmailLinkState()
    }
}
