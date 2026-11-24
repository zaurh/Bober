package com.zaurh.bober.screen.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.report.Reason
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Which confirmation is open on a profile (or on the chat screen, which reuses these). */
enum class ProfileDialog { Unmatch, Block, Report }

data class ReportForm(
    val reason: Reason? = null,
    val details: String = "",
)

data class ProfileUiState(
    /** The profile shown on this screen; null until loaded. */
    val user: UserData? = null,
    val loading: Boolean = false,
    val error: String? = null,
    /** A like / pass / unmatch / block / report request is in flight. */
    val busy: Boolean = false,
    val dialog: ProfileDialog? = null,
    val report: ReportForm = ReportForm(),
)

/**
 * Another user's profile and the relationship actions on it. Each profile screen gets its own
 * instance, so opening one profile from another never shows stale data or photo positions.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val currentUserDataState: StateFlow<UserData?> = userRepository.userData

    private val _state = mutableStateOf(ProfileUiState())
    val state: State<ProfileUiState> = _state

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    /** One-off messages for a toast (errors, "reported"). */
    val messages: SharedFlow<String> = _messages

    private fun update(transform: ProfileUiState.() -> ProfileUiState) {
        _state.value = _state.value.transform()
    }

    fun load(username: String) {
        if (state.value.user?.username == username) return
        update { copy(loading = true, error = null) }
        viewModelScope.launch {
            val response = userRepository.getProfileID(username)
            update {
                copy(
                    loading = false,
                    user = if (response.success) response.user else user,
                    error = if (response.success) null else response.message
                )
            }
        }
    }

    // --- Relationship actions. [onSuccess] runs on the main thread, typically to navigate away. ---

    fun like(recipientId: String, onSuccess: () -> Unit) = action(onSuccess) { userRepository.like(recipientId) }

    fun pass(recipientId: String, onSuccess: () -> Unit) = action(onSuccess) { userRepository.pass(recipientId) }

    fun unLike(recipientId: String, onSuccess: () -> Unit) = action(onSuccess) { userRepository.unlike(recipientId) }

    fun unblock(recipientId: String, onSuccess: () -> Unit = {}) = action(onSuccess) { userRepository.unblock(recipientId) }

    fun unMatch(recipientId: String, onSuccess: () -> Unit) =
        action({ closeDialog(); onSuccess() }) { userRepository.unmatch(recipientId) }

    fun block(recipientId: String, onSuccess: () -> Unit) =
        action({ closeDialog(); onSuccess() }) { userRepository.block(recipientId) }

    fun report(recipientId: String) {
        val form = state.value.report
        val reason = form.reason ?: return
        action({
            closeDialog()
            _messages.tryEmit("Thanks, we'll review this report.")
        }) {
            userRepository.report(
                recipientId = recipientId,
                reason = reason.displayName,
                optional = form.details.trim().ifEmpty { "No optional" }
            )
        }
    }

    private fun action(onSuccess: () -> Unit, call: suspend () -> MessageResponse) {
        if (state.value.busy) return
        update { copy(busy = true) }
        viewModelScope.launch {
            val response = call()
            update { copy(busy = false) }
            if (response.success) onSuccess() else _messages.emit(response.message)
        }
    }

    // --- Dialogs ---

    fun openDialog(dialog: ProfileDialog) {
        update { copy(dialog = dialog, report = if (dialog == ProfileDialog.Report) ReportForm() else report) }
    }

    fun closeDialog() {
        update { copy(dialog = null) }
    }

    fun selectReason(reason: Reason) {
        update { copy(report = report.copy(reason = reason)) }
    }

    fun onReportDetailsChange(details: String) {
        if (details.length <= 300) update { copy(report = report.copy(details = details)) }
    }
}
