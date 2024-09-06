package com.zaurh.bober.data.repository

import android.content.SharedPreferences
import com.zaurh.bober.data.requests.CheckUsernameRequest
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.data.requests.SignInRequest
import com.zaurh.bober.data.requests.SignUpRequest
import com.zaurh.bober.data.responses.AuthenticateResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.SignInResponse
import com.zaurh.bober.data.responses.SignUpResponse
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.domain.repository.AuthRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val api: UserApi,
    private val prefs: SharedPreferences
) : AuthRepo {

    private val _signedIn = MutableStateFlow(false)
    override val signedIn: StateFlow<Boolean>
        get() = _signedIn

    private val _loading = MutableStateFlow(false)
    override val loading: StateFlow<Boolean>
        get() = _loading


    override suspend fun checkUsername(username: String): MessageResponse {
        return try {
            val response = api.checkUsername(
                CheckUsernameRequest(
                    username = username
                )
            )
            MessageResponse(
                success = response.success,
                message = response.message
            )
        } catch (e: Exception) {
            MessageResponse(
                success = false,
                message = e.cause?.message ?: ""
            )
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO)

    init {
        ioScope.launch {
            authenticate()
        }
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
        return try {
            val response = api.signUp(
                apiRequest = SignUpRequest(
                    username = username,
                    password = password,
                    gender = gender,
                    image = image,
                    dateOfBirth = dateOfBirth,
                    showMe = showMe,
                    interests = interests
                )
            )

            if (response.success) {
                signIn(username,password)
            }

            SignUpResponse(
                success = response.success,
                message = response.message,
                user = response.user,
                passwordError = response.passwordError,
                imageError =response.imageError
            )

        } catch (e: Exception) {
            SignUpResponse(
                success = false,
                message = e.cause?.message ?: ""
            )
        }
    }

    override suspend fun signIn(username: String, password: String): SignInResponse {
        return try {
            val response = api.signIn(
                apiRequest = SignInRequest(
                    username = username,
                    password = password
                )
            )
            prefs.edit()
                .putString("jwt", response.token)
                .apply()


            if (response.success) {
                _signedIn.value = true
            }

            SignInResponse(
                success = response.success,
                message = response.message,
                user = response.user
            )
        } catch (e: Exception) {

            SignInResponse(
                success = false,
                message = e.localizedMessage ?: ""
            )
        }
    }

    override suspend fun authenticate(): AuthenticateResponse {
        return try {
            _loading.value = true
            val token = prefs.getString("jwt", null)
            val response = api.authenticate("Bearer $token")

            if (response.success) {
                _signedIn.value = true
                _loading.value = false
            }else{
                _loading.value = false
            }

            AuthenticateResponse(
                success = response.success,
                message = response.message
            )


        } catch (e: Exception) {
            _loading.value = false
            AuthenticateResponse(
                success = false,
                message = e.message ?: ""
            )
        }
    }

    override suspend fun logout(): AuthenticateResponse {
        return try {
            prefs.edit().clear().apply()

            _signedIn.value = false

            AuthenticateResponse(
                success = true,
                message = "Logged out."
            )

        } catch (e: Exception) {
            AuthenticateResponse(
                success = false,
                message = "Couldn't log out."
            )
        }
    }


}