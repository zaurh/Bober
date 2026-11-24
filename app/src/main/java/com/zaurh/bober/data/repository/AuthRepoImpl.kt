package com.zaurh.bober.data.repository

import com.zaurh.bober.data.remote.SessionEvents
import com.zaurh.bober.data.remote.TokenStore
import com.zaurh.bober.data.remote.apiCall
import com.zaurh.bober.data.requests.CheckUsernameRequest
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
    sessionEvents: SessionEvents,
) : AuthRepo {

    private val _signedIn = MutableStateFlow(false)
    override val signedIn: StateFlow<Boolean> get() = _signedIn

    // Starts true: the splash screen stays up until the saved session has been checked.
    private val _loading = MutableStateFlow(true)
    override val loading: StateFlow<Boolean> get() = _loading

    init {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
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

    override suspend fun signIn(username: String, password: String): SignInResponse {
        val response = apiCall({ SignInResponse(success = false, message = it) }) {
            api.signIn(SignInRequest(username = username, password = password))
        }
        val token = response.token
        if (response.success && !token.isNullOrBlank()) {
            tokenStore.save(token)
            _signedIn.value = true
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
        return response
    }

    override suspend fun logout(): AuthenticateResponse {
        webSocketRepository.disconnect()
        tokenStore.clear()
        userRepository.clear()
        messageRepository.clear()
        _signedIn.value = false
        return AuthenticateResponse(success = true, message = "Logged out.")
    }
}
