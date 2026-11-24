package com.zaurh.bober.data.repository

import android.util.Log
import com.zaurh.bober.data.requests.CheckCurrentPasswordRequest
import com.zaurh.bober.data.requests.CheckUsernameRequest
import com.zaurh.bober.data.requests.FCMTokenRequest
import com.zaurh.bober.data.requests.GoogleTokenRequest
import com.zaurh.bober.data.requests.SendEmailRequest
import com.zaurh.bober.data.requests.SignInRequest
import com.zaurh.bober.data.requests.SignUpRequest
import com.zaurh.bober.data.requests.UpdatePasswordRequest
import com.zaurh.bober.data.requests.ValidateTokenRequest
import com.zaurh.bober.data.responses.AuthenticateResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.SignInResponse
import com.zaurh.bober.data.responses.SignUpResponse
import com.zaurh.bober.data.responses.ValidateEmailResponse
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.data.user.UserUpdate
import com.zaurh.bober.data.user.room.UserDao
import com.zaurh.bober.data.user.toUserEntity
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.preferences.BoberPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val api: UserApi,
    private val boberPreferences: BoberPreferences,
    private val userDao: UserDao
) : AuthRepo {

    private val _signedIn = MutableStateFlow(false)
    override val signedIn: StateFlow<Boolean>
        get() = _signedIn

    private val _loading = MutableStateFlow(false)
    override val loading: StateFlow<Boolean>
        get() = _loading

    private val ioScope = CoroutineScope(Dispatchers.IO)

    init {
        ioScope.launch {
            val signedIn = getSignedIn()
            if (signedIn == true){
                _signedIn.value = true
            }else{
                authenticate()
            }
        }
    }

    suspend fun getToken(): String? {
        return boberPreferences.getJwtKey.first()
    }

    suspend fun getSignedIn(): Boolean? {
        return boberPreferences.getSignedIn.first()
    }

    override suspend fun storeFCMToken(fcmToken: String): MessageResponse {
//        val token = prefs.getString("jwt", null) ?: return MessageResponse(
//            success = false,
//            message = "Token retrieve failed."
//        )
        val token = getToken()
        return try {
            val response = api.storeFCMToken(
                token = "Bearer $token",
                fcmTokenRequest = FCMTokenRequest(
                    fcmToken = fcmToken
                )
            )
            Log.d("dfsksdkf", "$response")
            MessageResponse(
                success = response.success,
                message = response.message
            )
        } catch (e: Exception) {
            Log.d("dfsksdkf", "${e.message}")

            MessageResponse(
                success = false,
                message = e.cause?.message ?: ""
            )
        }
    }


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
                signIn(
                    username = username,
                    password = password
                )
            }

            SignUpResponse(
                success = response.success,
                message = response.message,
                user = response.user,
                passwordError = response.passwordError,
                imageError = response.imageError
            )

        } catch (e: Exception) {
            SignUpResponse(
                success = false,
                message = e.cause?.message ?: ""
            )
        }
    }

    override suspend fun googleVerify(token: String): SignInResponse {
        return try {
            val response = api.googleVerify(
                googleTokenRequest = GoogleTokenRequest(
                    token = token
                )
            )

            if (response.success){
                _signedIn.value = true
                boberPreferences.saveSignedIn(true)
                boberPreferences.saveJwtKey(response.token ?: "")
                boberPreferences.saveCurrentUserId(response.user?.id ?: "")
                response.user?.let {
                    userDao.insertUser(it.toUserEntity())
                }
            }

            SignInResponse(
                success = response.success,
                message = response.message,
                user = response.user
            )

        } catch (e: Exception) {
            SignInResponse(
                success = false,
                message = e.cause?.message ?: ""
            )
        }

    }

    override suspend fun signIn(
        userId: String?,
        username: String,
        password: String
    ): SignInResponse {
        return try {
            val response = api.signIn(
                apiRequest = SignInRequest(
                    userId = userId,
                    username = username,
                    password = password
                )
            )

            if (response.success){
                _signedIn.value = true
                boberPreferences.saveSignedIn(true)
                boberPreferences.saveJwtKey(response.token ?: "")
                boberPreferences.saveCurrentUserId(response.user?.id ?: "")
                response.user?.let {
                    userDao.insertUser(it.toUserEntity())
                }
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

    override suspend fun resetPassword(email: String): MessageResponse {
        return try {
            val response = api.resetPassword(
                sendEmailRequest = SendEmailRequest(
                    email = email
                )
            )
            MessageResponse(
                success = response.success,
                message = response.message
            )
        } catch (e: Exception) {
            MessageResponse(
                success = false,
                message = e.localizedMessage ?: ""
            )
        }
    }

    override suspend fun checkPassword(password: String): MessageResponse {
//        val token = prefs.getString("jwt", null) ?: return MessageResponse(
//            success = false,
//            message = "Token retrieve failed."
//        )
        val token = getToken()
        return try {
            val response = api.checkPassword(
                token = "Bearer $token",
                checkCurrentPasswordRequest = CheckCurrentPasswordRequest(
                    password = password
                )
            )
            Log.d("asdkjalsdk", "success response: $response")
            Log.d("asdkjalsdk", "token: $token")

            MessageResponse(
                success = response.success,
                message = response.message
            )
        } catch (e: Exception) {
            Log.d("asdkjalsdk", "error response: ${e.message}")

            MessageResponse(
                success = false,
                message = e.localizedMessage ?: ""
            )
        }
    }

    override suspend fun updatePassword(
        userId: String,
        password: String,
        confirmPassword: String
    ): MessageResponse {
        return try {
            val response = api.updatePassword(
                updatePasswordRequest = UpdatePasswordRequest(
                    userId = userId,
                    password = password,
                    confirmPassword = confirmPassword
                )
            )
            if (response.success) {
                signIn(
                    userId = userId,
                    username = "",
                    password = password
                )
            }
            MessageResponse(
                success = response.success,
                message = response.message
            )
        } catch (e: Exception) {
            MessageResponse(
                success = false,
                message = e.localizedMessage ?: ""
            )
        }
    }

    override suspend fun validatePasswordToken(token: String): MessageResponse {
        return try {
            val response = api.validatePasswordToken(
                validateTokenRequest = ValidateTokenRequest(
                    token = token
                )
            )
            MessageResponse(
                success = response.success,
                message = response.message
            )
        } catch (e: Exception) {
            MessageResponse(
                success = false,
                message = e.localizedMessage ?: ""
            )
        }
    }

    override suspend fun sendEmailVerification(email: String): MessageResponse {
//        val token = prefs.getString("jwt", null) ?: return MessageResponse(
//            success = false,
//            message = "Token retrieve failed."
//        )
        val token = getToken()

        return try {
            val response = api.sendEmailVerification(
                token = "Bearer $token",
                sendEmailRequest = SendEmailRequest(
                    email = email
                )
            )
            MessageResponse(
                success = response.success,
                message = response.message
            )
        } catch (e: Exception) {
            MessageResponse(
                success = false,
                message = e.localizedMessage ?: ""
            )
        }
    }

    override suspend fun validateEmailToken(emailToken: String): ValidateEmailResponse {
        val token = getToken()

        return try {
            val response = api.validateEmailToken(
                token = "Bearer $token",
                validateTokenRequest = ValidateTokenRequest(
                    token = emailToken
                )
            )
            Log.d("dalkjdsakl", "repo:$response")
            ValidateEmailResponse(
                success = response.success,
                message = response.message,
                email = response.email
            )
        } catch (e: Exception) {
            Log.d("dalkjdsakl", "${e.message}")

            ValidateEmailResponse(
                success = false,
                message = e.localizedMessage ?: ""
            )
        }
    }

    override suspend fun authenticate(): AuthenticateResponse {
        return try {
            _loading.value = true
            val token = getToken()

            val response = api.authenticate("Bearer $token")

            if (response.success) {
//                boberPreferences.saveSignedIn(true)
                _signedIn.value = true
                _loading.value = false
            } else {
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
        val token = getToken()

        Log.d("sdaldsaklsa", "logout, token: $token")

        return try {

            val response = api.updateUserData(
                token = "Bearer $token",
                userUpdate = UserUpdate(
                    fcmToken = ""
                )
            )

            Log.d("sdaldsaklsa", "$response")

            _signedIn.value = false
            boberPreferences.saveJwtKey("")
            boberPreferences.saveSignedIn(false)

            AuthenticateResponse(
                success = true,
                message = "Logged out."
            )

        } catch (e: Exception) {
            Log.d("sdaldsaklsa", "${e.message}")

            AuthenticateResponse(
                success = false,
                message = "Couldn't log out."
            )
        }
    }


}