package com.zaurh.bober.data.repository

import android.content.SharedPreferences
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.data.responses.AddMediaResponse
import com.zaurh.bober.data.responses.GetAllUsersResponse
import com.zaurh.bober.data.responses.GetLocationResponse
import com.zaurh.bober.data.responses.GetUserDataResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.UpdateUserResponse
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import com.zaurh.bober.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val api: UserApi,
    private val prefs: SharedPreferences
) : UserRepository {

    private val _userData = MutableStateFlow<UserData?>(null)
    override val userData: StateFlow<UserData?> get() = _userData

    private val _profileData = MutableStateFlow<UserData?>(null)
    override val profileData: StateFlow<UserData?> get() = _profileData

    private val _userListData = MutableStateFlow<List<UserData?>>(listOf())
    override val userListData: StateFlow<List<UserData?>>
        get() = _userListData



    override suspend fun getUserData(): GetUserDataResponse {
        return try {

            val token = prefs.getString("jwt", null) ?: return GetUserDataResponse(
                success = false,
                message = "Token retrieve failed."
            )

            val response = api.getUserData("Bearer $token")

            if (response.success){
                _userData.value = response.user
            }

            GetUserDataResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )

        } catch (e: Exception) {
            GetUserDataResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun deleteUserData(): MessageResponse {
        return try {
            val token = prefs.getString("jwt", null) ?: return MessageResponse(
                success = false,
                message = "Token retrieve failed."
            )
            val response = api.deleteUserData("Bearer $token")
            MessageResponse(
                success = response.success,
                message = response.message
            )

        } catch (e: Exception) {
            MessageResponse(success = false, message = e.message ?: "")
        }
    }


    override suspend fun getProfileID(username: String): GetUserDataResponse {
        return try {
            val response = api.getProfileID(username)

            if (response.success){
                _profileData.value = response.user
            }

            GetUserDataResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )
        } catch (e: Exception) {
            GetUserDataResponse(
                success = false,
                message = e.message.toString()
            )
        }
    }

    override suspend fun clearProfileData() {
        _profileData.value = null
    }

    override suspend fun clearUserData() {
        _userData.value = null
    }


    override suspend fun updateUserData(userUpdate: UserUpdate, onSuccess: () -> Unit, onFailure: () -> Unit): UpdateUserResponse {
        val token = prefs.getString("jwt", null) ?: return UpdateUserResponse(
            success = false,
            message = "Token retrieve failed."
        )
        return try {
            val response = api.updateUserData(
                token = "Bearer $token",
                userUpdate = userUpdate
            )

            if (response.success){
                onSuccess()
                _userData.value = response.user

            }else{
                onFailure()
            }
            UpdateUserResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )
        } catch (e: Exception) {
            UpdateUserResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun getAllUsersData(): GetAllUsersResponse {
        return try {
            val token = prefs.getString("jwt", null) ?: return GetAllUsersResponse(
                success = false,
                message = "Token retrieve failed."
            )
            val response = api.getAllUsersData("Bearer $token")
            if (response.success){
                _userListData.value = response.userList
            }
            GetAllUsersResponse(
                success = response.success,
                userList = response.userList,
                message = response.message
            )
        } catch (e: Exception) {
            GetAllUsersResponse(success = false, message = e.message ?: "")
        }
    }


    override suspend fun uploadMedia(
        description: String,
        inputStream: InputStream,
        fileName: String
    ): AddMediaResponse {
        val requestFile = inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", fileName, requestFile)
        val descriptionPart = description.toRequestBody(MultipartBody.FORM)

        return try {
            val response = api.uploadMedia(descriptionPart, imagePart)

            AddMediaResponse(
                success = response.success,
                fileLink = response.fileLink,
                message = response.message
            )

        } catch (e: Exception) {
            AddMediaResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun like(recipientId: String): UpdateUserResponse {
        val token = prefs.getString("jwt", null) ?: return UpdateUserResponse(
            success = false,
            message = "Token retrieve failed."
        )
        return try {
            val response = api.like(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success){
                _userData.value = response.user
            }
            UpdateUserResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )
        } catch (e: Exception) {
            UpdateUserResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun pass(recipientId: String): UpdateUserResponse {
        val token = prefs.getString("jwt", null) ?: return UpdateUserResponse(
            success = false,
            message = "Token retrieve failed."
        )
        return try {
            val response = api.pass(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success){
                _userData.value = response.user
            }
            UpdateUserResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )
        } catch (e: Exception) {
            UpdateUserResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun unlike(recipientId: String): UpdateUserResponse {
        val token = prefs.getString("jwt", null) ?: return UpdateUserResponse(
            success = false,
            message = "Token retrieve failed."
        )
        return try {
            val response = api.unlike(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success){
                _userData.value = response.user
            }
            UpdateUserResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )
        } catch (e: Exception) {
            UpdateUserResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun unmatch(recipientId: String): UpdateUserResponse {
        val token = prefs.getString("jwt", null) ?: return UpdateUserResponse(
            success = false,
            message = "Token retrieve failed."
        )
        return try {
            val response = api.unMatch(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success){
                _userData.value = response.user
            }
            UpdateUserResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )
        } catch (e: Exception) {
            UpdateUserResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun block(recipientId: String): UpdateUserResponse {
        val token = prefs.getString("jwt", null) ?: return UpdateUserResponse(
            success = false,
            message = "Token retrieve failed."
        )
        return try {
            val response = api.block(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success){
                _userData.value = response.user
            }
            UpdateUserResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )
        } catch (e: Exception) {
            UpdateUserResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun report(recipientId: String, reason: String, optional: String): MessageResponse {
        val token = prefs.getString("jwt", null) ?: return MessageResponse(
            success = false,
            message = "Token retrieve failed."
        )
        return try {
            val response = api.report(
                token = "Bearer $token",
                recipientId = recipientId,
                reason = reason,
                optional = optional
            )

            MessageResponse(
                success = response.success,
                message = response.message
            )
        } catch (e: Exception) {
            MessageResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun unblock(recipientId: String): UpdateUserResponse {
        val token = prefs.getString("jwt", null) ?: return UpdateUserResponse(
            success = false,
            message = "Token retrieve failed."
        )
        return try {
            val response = api.unblock(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success){
                _userData.value = response.user
            }
            UpdateUserResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )
        } catch (e: Exception) {
            UpdateUserResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun encryptLocation(location: String): GetLocationResponse {
        return try {
            val token = prefs.getString("jwt", null) ?: return GetLocationResponse(
                success = false,
                message = "Token retrieve failed."
            )
            val response = api.encryptLocation(token = "Bearer $token", location = location)

            if (response.success) {
                _userData.value = userData.value?.copy(
                    encryptedLocation = response.message
                )
            }

            GetLocationResponse(
                success = response.success,
                message = response.message
            )

        } catch (e: Exception) {
            GetLocationResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun decryptLocation(recipientLocation: String): GetLocationResponse {
        return try {
            val token = prefs.getString("jwt", null) ?: return GetLocationResponse(
                success = false,
                message = "Token retrieve failed."
            )
            val response = api.decryptLocation(token = "Bearer $token", recipientLocation = recipientLocation)

            GetLocationResponse(
                success = response.success,
                distance = response.distance,
                message = response.message
            )

        } catch (e: Exception) {
            GetLocationResponse(success = false, message = e.message ?: "")
        }
    }

}