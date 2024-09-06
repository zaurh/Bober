package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.responses.AddMediaResponse
import com.zaurh.bober.data.responses.GetAllUsersResponse
import com.zaurh.bober.data.responses.GetLocationResponse
import com.zaurh.bober.data.responses.GetUserDataResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.UpdateUserResponse
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream

interface UserRepository {

    val userData: StateFlow<UserData?>
    val profileData: StateFlow<UserData?>

    val userListData: StateFlow<List<UserData?>>

    suspend fun getUserData(): GetUserDataResponse
    suspend fun deleteUserData(): MessageResponse
    suspend fun getProfileID(username: String): GetUserDataResponse
    suspend fun clearProfileData()
    suspend fun clearUserData()
    suspend fun updateUserData(userUpdate: UserUpdate, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}): UpdateUserResponse
    suspend fun getAllUsersData(): GetAllUsersResponse
    suspend fun uploadMedia(description: String, inputStream: InputStream, fileName: String): AddMediaResponse
    suspend fun encryptLocation(location: String): GetLocationResponse
    suspend fun decryptLocation(recipientLocation: String): GetLocationResponse

    suspend fun like(recipientId: String): UpdateUserResponse
    suspend fun pass(recipientId: String): UpdateUserResponse
    suspend fun unlike(recipientId: String): UpdateUserResponse
    suspend fun unmatch(recipientId: String): UpdateUserResponse
    suspend fun block(recipientId: String): UpdateUserResponse
    suspend fun report(recipientId: String, reason: String, optional: String): MessageResponse
    suspend fun unblock(recipientId: String): UpdateUserResponse

}