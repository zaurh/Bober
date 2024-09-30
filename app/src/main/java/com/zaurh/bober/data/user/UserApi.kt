package com.zaurh.bober.data.user

import com.zaurh.bober.data.requests.CheckUsernameRequest
import com.zaurh.bober.data.requests.SignInRequest
import com.zaurh.bober.data.requests.SignUpRequest
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
import com.zaurh.bober.data.responses.WhoLikesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApi {

    @POST("/check_username")
    suspend fun checkUsername(
        @Body username: CheckUsernameRequest
    ): MessageResponse

    @POST("/sign_up")
    suspend fun signUp(
        @Body apiRequest: SignUpRequest
    ): SignUpResponse

    @POST("/sign_in")
    suspend fun signIn(
        @Body apiRequest: SignInRequest
    ): SignInResponse

    @GET("/get_user_data")
    suspend fun getUserData(
        @Header("Authorization") token: String
    ): GetUserDataResponse

    @DELETE("/delete_user_data")
    suspend fun deleteUserData(
        @Header("Authorization") token: String
    ): MessageResponse

    @GET("/users/{username}")
    suspend fun getProfileID(
        @Path("username") username: String
    ): GetUserDataResponse

    @PUT("/update_user")
    suspend fun updateUserData(
        @Header("Authorization") token: String,
        @Body userUpdate: UserUpdate
    ): UpdateUserResponse

    @GET("/bobers")
    suspend fun getBobers(
        @Header("Authorization") token: String
    ): GetBobersResponse

    @GET("/liked_users")
    suspend fun getLikedUsers(
        @Header("Authorization") token: String
    ): LikedUsersResponse

    @GET("/blocked_users")
    suspend fun getBlockedUsers(
        @Header("Authorization") token: String
    ): BlockedUsersResponse

    @GET("/who_likes")
    suspend fun getWhoLikes(
        @Header("Authorization") token: String
    ): WhoLikesResponse

    @GET("/matched_users")
    suspend fun getMatchedUsers(
        @Header("Authorization") token: String
    ): MatchedUserResponse



    @GET("/authenticate")
    suspend fun authenticate(
        @Header("Authorization") token: String
    ): AuthenticateResponse

    @Multipart
    @POST("/add_media")
    suspend fun uploadMedia(
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part,
    ): AddMediaResponse

    @PUT("/like/{recipientId}")
    suspend fun like(
        @Header("Authorization") token: String,
        @Path("recipientId") recipientId: String
    ): UpdateUserResponse

    @PUT("/pass/{recipientId}")
    suspend fun pass(
        @Header("Authorization") token: String,
        @Path("recipientId") recipientId: String
    ): UpdateUserResponse

    @PUT("/unlike/{recipientId}")
    suspend fun unlike(
        @Header("Authorization") token: String,
        @Path("recipientId") recipientId: String
    ): UpdateUserResponse

    @PUT("/unmatch/{recipientId}")
    suspend fun unMatch(
        @Header("Authorization") token: String,
        @Path("recipientId") recipientId: String
    ): UpdateUserResponse

    @PUT("/block/{recipientId}")
    suspend fun block(
        @Header("Authorization") token: String,
        @Path("recipientId") recipientId: String
    ): UpdateUserResponse

    @POST("/report/{recipientId}/{reason}/{optional}")
    suspend fun report(
        @Header("Authorization") token: String,
        @Path("recipientId") recipientId: String,
        @Path("reason") reason: String,
        @Path("optional") optional: String,
    ): MessageResponse

    @PUT("/unblock/{recipientId}")
    suspend fun unblock(
        @Header("Authorization") token: String,
        @Path("recipientId") recipientId: String
    ): UpdateUserResponse

    @PUT("/encrypt/{location}")
    suspend fun encryptLocation(
        @Header("Authorization") token: String,
        @Path("location") location: String
    ): GetLocationResponse

    @GET("/decrypt/{recipientLocation}")
    suspend fun decryptLocation(
        @Header("Authorization") token: String,
        @Path("recipientLocation") recipientLocation: String
    ): GetLocationResponse

    @PUT("/premium")
    suspend fun boberium(
        @Header("Authorization") token: String,
    ): UpdateUserResponse

}