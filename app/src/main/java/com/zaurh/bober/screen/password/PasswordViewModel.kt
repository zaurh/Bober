package com.zaurh.bober.screen.password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.domain.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MIN_PASSWORD_LENGTH = 6

/** "Forgot password?" on sign-in: ask for the account's email and send a reset link. */
data class ForgotPasswordState(
    val open: Boolean = false,
    val email: String = "",
    val sending: Boolean = false,
    val sent: Boolean = false,
    val message: String? = null,
)

/** Opened from the emailed `…/reset-password?token=…` link. */
data class ResetPasswordState(
    val token: String? = null,
    val checking: Boolean = false,
    /** False once the server says the link is used up or expired. */
    val tokenValid: Boolean = true,
    val password: String = "",
    val confirm: String = "",
    val saving: Boolean = false,
    val done: Boolean = false,
    val message: String? = null,
)

/** Settings → Change password (signed in). */
data class ChangePasswordState(
    val open: Boolean = false,
    val current: String = "",
    val password: String = "",
    val confirm: String = "",
    val saving: Boolean = false,
    val done: Boolean = false,
    val message: String? = null,
)

/** Client-side check before calling the server; null when the new password is acceptable. */
fun newPasswordError(password: String, confirm: String): String? = when {
    password.length < MIN_PASSWORD_LENGTH -> "Use at least $MIN_PASSWORD_LENGTH characters."
    confirm.isNotEmpty() && password != confirm -> "Passwords don't match."
    else -> null
}

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val authRepo: AuthRepo,
) : ViewModel() {

    private val _forgot = mutableStateOf(ForgotPasswordState())
    val forgot: State<ForgotPasswordState> = _forgot

    private val _reset = mutableStateOf(ResetPasswordState())
    val reset: State<ResetPasswordState> = _reset

    private val _change = mutableStateOf(ChangePasswordState())
    val change: State<ChangePasswordState> = _change

    // --- Forgot password ---

    fun openForgot() {
        _forgot.value = ForgotPasswordState(open = true)
    }

    fun closeForgot() {
        _forgot.value = ForgotPasswordState()
    }

    fun onForgotEmailChange(email: String) {
        _forgot.value = forgot.value.copy(email = email, message = null)
    }

    fun sendResetLink() {
        val email = forgot.value.email.trim()
        if (email.isEmpty() || forgot.value.sending) return
        _forgot.value = forgot.value.copy(sending = true, message = null)
        viewModelScope.launch {
            val response = authRepo.sendPasswordReset(email)
            _forgot.value = forgot.value.copy(sending = false, sent = response.success, message = response.message)
        }
    }

    // --- Reset from link ---

    /** Opens the reset dialog for a link token and checks it's still usable. */
    fun openReset(token: String) {
        _reset.value = ResetPasswordState(token = token, checking = true)
        viewModelScope.launch {
            val response = authRepo.validateResetToken(token)
            _reset.value = reset.value.copy(
                checking = false,
                tokenValid = response.success,
                message = if (response.success) null else "This reset link has expired or was already used. Request a new one from the sign-in screen."
            )
        }
    }

    fun closeReset() {
        _reset.value = ResetPasswordState()
    }

    fun onResetPasswordChange(password: String) {
        _reset.value = reset.value.copy(password = password, message = null)
    }

    fun onResetConfirmChange(confirm: String) {
        _reset.value = reset.value.copy(confirm = confirm, message = null)
    }

    fun saveResetPassword() {
        val state = reset.value
        val token = state.token ?: return
        if (state.saving) return
        newPasswordError(state.password, state.confirm.ifEmpty { " " })?.let {
            _reset.value = state.copy(message = it)
            return
        }
        _reset.value = state.copy(saving = true, message = null)
        viewModelScope.launch {
            val response = authRepo.resetPassword(token, state.password, state.confirm)
            _reset.value = reset.value.copy(saving = false, done = response.success, message = response.message)
        }
    }

    // --- Change password ---

    fun openChange() {
        _change.value = ChangePasswordState(open = true)
    }

    fun closeChange() {
        _change.value = ChangePasswordState()
    }

    fun onCurrentChange(value: String) {
        _change.value = change.value.copy(current = value, message = null)
    }

    fun onChangePasswordChange(value: String) {
        _change.value = change.value.copy(password = value, message = null)
    }

    fun onChangeConfirmChange(value: String) {
        _change.value = change.value.copy(confirm = value, message = null)
    }

    fun saveChangedPassword() {
        val state = change.value
        if (state.saving) return
        if (state.current.isEmpty()) {
            _change.value = state.copy(message = "Enter your current password.")
            return
        }
        newPasswordError(state.password, state.confirm.ifEmpty { " " })?.let {
            _change.value = state.copy(message = it)
            return
        }
        _change.value = state.copy(saving = true, message = null)
        viewModelScope.launch {
            val response = authRepo.changePassword(state.current, state.password, state.confirm)
            _change.value = change.value.copy(saving = false, done = response.success, message = response.message)
        }
    }
}
