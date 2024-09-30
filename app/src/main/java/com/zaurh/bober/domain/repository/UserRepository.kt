package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.responses.AddMediaResponse
import com.zaurh.bober.data.responses.BlockedUserData
import com.zaurh.bober.data.responses.BlockedUsersResponse
import com.zaurh.bober.data.responses.GetBobersResponse
import com.zaurh.bober.data.responses.GetLocationResponse
import com.zaurh.bober.data.responses.GetUserDataResponse
import com.zaurh.bober.data.responses.LikedUserData
import com.zaurh.bober.data.responses.LikedUsersResponse
import com.zaurh.bober.data.responses.MatchedUserData
import com.zaurh.bober.data.responses.MatchedUserResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.UpdateUserResponse
import com.zaurh.bober.data.responses.WhoLikesData
import com.zaurh.bober.data.responses.WhoLikesResponse
import com.zaurh.bober.data.user.BoberData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream

interface UserRepository {

    val userData: StateFlow<UserData?>
    val profileData: StateFlow<UserData?>

    val boberDataList: StateFlow<List<BoberData?>>
    val likedUserList: StateFlow<List<LikedUserData?>>
    val blockedUserList: StateFlow<List<BlockedUserData?>>
    val whoLikesList: StateFlow<List<WhoLikesData>>
    val matchedUserList: StateFlow<List<MatchedUserData>>

    suspend fun getUserData(): GetUserDataResponse
    suspend fun deleteUserData(): MessageResponse
    suspend fun getProfileID(username: String): GetUserDataResponse
    suspend fun clearProfileData()
    suspend fun clearUserData()
    suspend fun updateUserData(userUpdate: UserUpdate, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}): UpdateUserResponse

    suspend fun getBobers(): GetBobersResponse
    suspend fun getLikedUsers(): LikedUsersResponse
    suspend fun getBlockedUsers(): BlockedUsersResponse
    suspend fun getWhoLikes(): WhoLikesResponse
    suspend fun getMatchedUsers(): MatchedUserResponse

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

    suspend fun boberium(): UpdateUserResponse

}