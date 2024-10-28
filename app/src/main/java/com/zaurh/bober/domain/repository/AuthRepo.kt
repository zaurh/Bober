package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.responses.AuthenticateResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.SignInResponse
import com.zaurh.bober.data.responses.SignUpResponse
import com.zaurh.bober.data.responses.ValidateEmailResponse
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.ShowMe
import kotlinx.coroutines.flow.StateFlow

interface AuthRepo {
    val signedIn: StateFlow<Boolean>
    val loading: StateFlow<Boolean>

    suspend fun storeFCMToken(fcmToken: String): MessageResponse
    suspend fun checkUsername(username: String): MessageResponse

    suspend fun signUp(
        username: String,
        password: String,
        gender: Gender,
        image: String,
        dateOfBirth: String,
        showMe: ShowMe,
        interests: List<Interests>
    ): SignUpResponse
    suspend fun signIn(userId: String? = null,username: String, password: String): SignInResponse
    suspend fun resetPassword(email: String): MessageResponse
    suspend fun checkPassword(password: String): MessageResponse
    suspend fun updatePassword(userId: String,password: String, confirmPassword: String): MessageResponse
    suspend fun validatePasswordToken(token: String): MessageResponse
    suspend fun sendEmailVerification(email: String): MessageResponse
    suspend fun validateEmailToken(token: String): ValidateEmailResponse
    suspend fun authenticate(): AuthenticateResponse
    suspend fun logout(): AuthenticateResponse
}