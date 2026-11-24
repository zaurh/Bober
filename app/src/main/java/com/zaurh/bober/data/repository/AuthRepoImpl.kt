package com.zaurh.bober.data.repository

import com.zaurh.bober.data.push.PushTokenRegistrar
import com.zaurh.bober.data.remote.SessionEvents
import com.zaurh.bober.data.remote.TokenStore
import com.zaurh.bober.data.remote.apiCall
import com.zaurh.bober.data.requests.CheckCurrentPasswordRequest
import com.zaurh.bober.data.requests.CheckUsernameRequest
import com.zaurh.bober.data.requests.GoogleTokenRequest
import com.zaurh.bober.data.requests.SendEmailRequest
import com.zaurh.bober.data.requests.UpdatePasswordRequest
import com.zaurh.bober.data.requests.ValidateTokenRequest
import com.zaurh.bober.data.requests.SignInRequest
import com.zaurh.bober.data.requests.SignUpRequest
import com.zaurh.bober.data.responses.AuthenticateResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.SignInResponse
import com.zaurh.bober.data.responses.SignUpResponse
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepoImpl @Inject constructor(
    private val api: UserApi,
    private val tokenStore: TokenStore,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val webSocketRepository: WebSocketRepository,
    private val pushTokenRegistrar: PushTokenRegistrar,
    sessionEvents: SessionEvents,
) : AuthRepo {

    private val _signedIn = MutableStateFlow(false)
    override val signedIn: StateFlow<Boolean> get() = _signedIn

    // Starts true: the splash screen stays up until the saved session has been checked.
    private val _loading = MutableStateFlow(true)
    override val loading: StateFlow<Boolean> get() = _loading

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch { authenticate() }
        scope.launch { sessionEvents.expired.collect { if (_signedIn.value) logout() } }
    }

    override suspend fun checkUsername(username: String): MessageResponse =
        apiCall({ MessageResponse(success = false, message = it) }) {
            api.checkUsername(CheckUsernameRequest(username = username))
        }

    override suspend fun signUp(
        username: String,
        password: String,
        gender: Gender,
        image: String,
        dateOfBirth: String,
        showMe: ShowMe,
        interests: List<Interests>
    ): SignUpResponse {
        val response = apiCall({ SignUpResponse(success = false, message = it) }) {
            api.signUp(
                SignUpRequest(
                    username = username,
                    password = password,
                    gender = gender,
                    image = image,
                    dateOfBirth = dateOfBirth,
                    showMe = showMe,
                    interests = interests
                )
            )
        }
        // Sign-up doesn't return a token; sign in with the new credentials to get one.
        if (response.success) signIn(username, password)
        return response
    }

    override suspend fun signIn(username: String, password: String): SignInResponse =
        startSession(
            apiCall({ SignInResponse(success = false, message = it) }) {
                api.signIn(SignInRequest(username = username, password = password))
            }
        )

    override suspend fun signInWithGoogle(idToken: String): SignInResponse =
        startSession(
            apiCall({ SignInResponse(success = false, message = it) }) {
                api.signInWithGoogle(GoogleTokenRequest(idToken))
            }
        )

    /** Saves the JWT from a successful sign-in and marks the user signed in. Never hands the token out. */
    private fun startSession(response: SignInResponse): SignInResponse {
        val token = response.token
        if (response.success && !token.isNullOrBlank()) {
            tokenStore.save(token)
            _signedIn.value = true
            scope.launch { pushTokenRegistrar.register() }
        }
        return response.copy(token = null)
    }

    override suspend fun authenticate(): AuthenticateResponse {
        if (!tokenStore.hasToken) {
            _signedIn.value = false
            _loading.value = false
            return AuthenticateResponse(success = false, message = "Not signed in.")
        }

        _loading.value = true
        var rejected = false
        val response = try {
            api.authenticate()
        } catch (e: HttpException) {
            // The token was rejected (expired, or signed with a rotated secret): sign in again.
            rejected = e.code() == HttpURLConnection.HTTP_UNAUTHORIZED
            if (rejected) tokenStore.clear()
            AuthenticateResponse(success = false, message = "Session expired. Please sign in again.")
        } catch (e: Exception) {
            AuthenticateResponse(success = false, message = e.message ?: "Couldn't reach the server.")
        }

        // Offline or server hiccup: keep the saved session instead of bouncing to sign-in.
        _signedIn.value = response.success || (!rejected && tokenStore.hasToken)
        _loading.value = false
        // FCM tokens can rotate while the app is closed; re-sync on every confirmed session.
        if (response.success) scope.launch { pushTokenRegistrar.register() }
        return response
    }

    override suspend fun logout(): AuthenticateResponse {
        webSocketRepository.disconnect()
        scope.launch { pushTokenRegistrar.unregister() }
        tokenStore.clear()
        userRepository.clear()
        messageRepository.clear()
        _signedIn.value = false
        return AuthenticateResponse(success = true, message = "Logged out.")
    }

    override suspend fun sendPasswordReset(email: String): MessageResponse =
        apiCall({ MessageResponse(success = false, message = it) }) {
            api.sendPasswordReset(SendEmailRequest(email.trim()))
        }

    override suspend fun validateResetToken(token: String): MessageResponse =
        apiCall({ MessageResponse(success = false, message = it) }) {
            api.validatePasswordToken(ValidateTokenRequest(token))
        }

    override suspend fun resetPassword(token: String, password: String, confirmPassword: String): MessageResponse {
        val userId = ResetToken.userId(token)
            ?: return MessageResponse(success = false, message = "This reset link is invalid.")
        return apiCall({ MessageResponse(success = false, message = it) }) {
            api.updatePassword(UpdatePasswordRequest(userId, password, confirmPassword, token))
        }
    }

    override suspend fun changePassword(currentPassword: String, password: String, confirmPassword: String): MessageResponse {
        val userId = userRepository.userData.value?.id
            ?: return MessageResponse(success = false, message = "Couldn't load your account. Please try again.")
        val check = apiCall({ MessageResponse(success = false, message = it) }) {
            api.checkCurrentPassword(CheckCurrentPasswordRequest(currentPassword))
        }
        if (!check.success) return check
        return apiCall({ MessageResponse(success = false, message = it) }) {
            api.updatePassword(UpdatePasswordRequest(userId, password, confirmPassword))
        }
    }
}

/** Reset tokens are `<36-char UUID><userId><13-digit issued-at millis>` (backend `EmailTokens`). */
internal object ResetToken {
    private const val PREFIX_LENGTH = 36
    private const val TIMESTAMP_LENGTH = 13

    fun userId(token: String): String? =
        if (token.length > PREFIX_LENGTH + TIMESTAMP_LENGTH) token.drop(PREFIX_LENGTH).dropLast(TIMESTAMP_LENGTH) else null
}
