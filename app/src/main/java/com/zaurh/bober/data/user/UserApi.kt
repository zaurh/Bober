package com.zaurh.bober.data.user

import com.zaurh.bober.data.requests.CheckCurrentPasswordRequest
import com.zaurh.bober.data.requests.CheckUsernameRequest
import com.zaurh.bober.data.requests.GoogleTokenRequest
import com.zaurh.bober.data.requests.SendEmailRequest
import com.zaurh.bober.data.requests.UpdatePasswordRequest
import com.zaurh.bober.data.requests.FcmTokenRequest
import com.zaurh.bober.data.requests.PremiumCardRequest
import com.zaurh.bober.data.requests.SignInRequest
import com.zaurh.bober.data.requests.SignUpRequest
import com.zaurh.bober.data.requests.ValidateTokenRequest
import com.zaurh.bober.data.requests.VerifyEmailRequest
import com.zaurh.bober.data.responses.AddMediaResponse
import com.zaurh.bober.data.responses.AuthenticateResponse
import com.zaurh.bober.data.responses.BlockedUsersResponse
import com.zaurh.bober.data.responses.GetBobersResponse
import com.zaurh.bober.data.responses.GetLocationResponse
import com.zaurh.bober.data.responses.GetUserDataResponse
import com.zaurh.bober.data.responses.LikedUsersResponse
import com.zaurh.bober.data.responses.MatchedUserResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.SignInResponse
import com.zaurh.bober.data.responses.SignUpResponse
import com.zaurh.bober.data.responses.UpdateUserResponse
import com.zaurh.bober.data.responses.ValidateEmailResponse
import com.zaurh.bober.data.responses.WhoLikesResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * User and auth endpoints of the Ktor backend (`com.example.ktor-sample-jwt`).
 *
 * Authentication is added by [com.zaurh.bober.data.remote.AuthInterceptor]; the backend identifies the
 * caller from the JWT, so no endpoint takes the current user's id.
 */
interface UserApi {

    // --- Auth (public) ---

    @POST("/check_username")
    suspend fun checkUsername(@Body request: CheckUsernameRequest): MessageResponse

    @POST("/sign_up")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

    @POST("/sign_in")
    suspend fun signIn(@Body request: SignInRequest): SignInResponse

    /** Signs in with a Google ID token, creating the account on first use. Same response as /sign_in. */
    @POST("/google_verify")
    suspend fun signInWithGoogle(@Body request: GoogleTokenRequest): SignInResponse

    /** Public on purpose: sign-up uploads the profile picture before the account exists. */
    @Multipart
    @POST("/add_media")
    suspend fun uploadMedia(@Part image: MultipartBody.Part): AddMediaResponse

    // --- Password reset (public; the emailed token authorizes the change) ---

    /** Emails a reset link (`…/reset-password?token=…`) to the account with this verified email. */
    @POST("/reset-password")
    suspend fun sendPasswordReset(@Body request: SendEmailRequest): MessageResponse

    @POST("/validate_password_token")
    suspend fun validatePasswordToken(@Body request: ValidateTokenRequest): MessageResponse

    /** Works with a reset token, or with the JWT of the user whose password changes. */
    @POST("/update_password")
    suspend fun updatePassword(@Body request: UpdatePasswordRequest): MessageResponse

    // --- Auth (JWT) ---

    @POST("/check_current_password")
    suspend fun checkCurrentPassword(@Body request: CheckCurrentPasswordRequest): MessageResponse

    @GET("/authenticate")
    suspend fun authenticate(): AuthenticateResponse

    /** Emails a verification link to [VerifyEmailRequest.email]. Validation errors come back as success = false. */
    @POST("/send_email_verification")
    suspend fun sendEmailVerification(@Body request: VerifyEmailRequest): MessageResponse

    /** Confirms the token from the emailed link; the backend then saves the email as verified. */
    @POST("/validate_email_token")
    suspend fun validateEmailToken(@Body request: ValidateTokenRequest): ValidateEmailResponse

    // --- Current user ---

    @GET("/get_user_data")
    suspend fun getUserData(): GetUserDataResponse

    @PUT("/update_user")
    suspend fun updateUserData(@Body userUpdate: UserUpdate): UpdateUserResponse

    @DELETE("/delete_user_data")
    suspend fun deleteUserData(): MessageResponse

    /** Saves this device's FCM token so the backend can push while the app is closed. */
    @PUT("/store_fcm_token")
    suspend fun storeFcmToken(@Body request: FcmTokenRequest): MessageResponse

    @PUT("/premium")
    suspend fun activatePremium(@Body request: PremiumCardRequest): UpdateUserResponse

    @PUT("/encrypt/{location}")
    suspend fun encryptLocation(@Path("location") location: String): GetLocationResponse

    @GET("/decrypt/{recipientLocation}")
    suspend fun decryptLocation(@Path("recipientLocation") recipientLocation: String): GetLocationResponse

    // --- Lists ---

    @GET("/bobers")
    suspend fun getBobers(): GetBobersResponse

    @GET("/matched_users")
    suspend fun getMatchedUsers(): MatchedUserResponse

    @GET("/liked_users")
    suspend fun getLikedUsers(): LikedUsersResponse

    @GET("/who_likes")
    suspend fun getWhoLikes(): WhoLikesResponse

    @GET("/blocked_users")
    suspend fun getBlockedUsers(): BlockedUsersResponse

    // --- Other users (public profiles) ---

    @GET("/users/{username}")
    suspend fun getProfileByUsername(@Path("username") username: String): GetUserDataResponse

    // --- Relationship actions. These return only success + message, never the updated user. ---

    @PUT("/like/{recipientId}")
    suspend fun like(@Path("recipientId") recipientId: String): MessageResponse

    @PUT("/unlike/{recipientId}")
    suspend fun unlike(@Path("recipientId") recipientId: String): MessageResponse

    @PUT("/pass/{recipientId}")
    suspend fun pass(@Path("recipientId") recipientId: String): MessageResponse

    @PUT("/unmatch/{recipientId}")
    suspend fun unmatch(@Path("recipientId") recipientId: String): MessageResponse

    @PUT("/block/{recipientId}")
    suspend fun block(@Path("recipientId") recipientId: String): MessageResponse

    @PUT("/unblock/{recipientId}")
    suspend fun unblock(@Path("recipientId") recipientId: String): MessageResponse

    @POST("/report/{recipientId}/{reason}/{optional}")
    suspend fun report(
        @Path("recipientId") recipientId: String,
        @Path("reason") reason: String,
        @Path("optional") optional: String,
    ): MessageResponse
}
