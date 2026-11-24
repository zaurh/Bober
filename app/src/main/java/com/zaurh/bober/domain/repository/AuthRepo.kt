package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.responses.AuthenticateResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.SignInResponse
import com.zaurh.bober.data.responses.SignUpResponse
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.ShowMe
import kotlinx.coroutines.flow.StateFlow

interface AuthRepo {
    val signedIn: StateFlow<Boolean>
    val loading: StateFlow<Boolean>

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
    suspend fun signIn(username: String, password: String): SignInResponse
    suspend fun signInWithGoogle(idToken: String): SignInResponse
    suspend fun authenticate(): AuthenticateResponse
    suspend fun logout(): AuthenticateResponse

    // --- Passwords ---

    /** Emails a reset link to the account with [email]. */
    suspend fun sendPasswordReset(email: String): MessageResponse

    /** Whether the token from a reset link is still valid (unused and not expired). */
    suspend fun validateResetToken(token: String): MessageResponse

    /** Sets a new password using the token from a reset link. */
    suspend fun resetPassword(token: String, password: String, confirmPassword: String): MessageResponse

    /** Signed-in password change: verifies [currentPassword] first. */
    suspend fun changePassword(currentPassword: String, password: String, confirmPassword: String): MessageResponse
}