package com.zaurh.bober.data.repository

import com.zaurh.bober.data.remote.apiCall
import com.zaurh.bober.data.requests.PremiumCardRequest
import com.zaurh.bober.data.requests.ValidateTokenRequest
import com.zaurh.bober.data.requests.VerifyEmailRequest
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
import com.zaurh.bober.data.responses.ValidateEmailResponse
import com.zaurh.bober.data.responses.WhoLikesData
import com.zaurh.bober.data.responses.WhoLikesResponse
import com.zaurh.bober.data.user.BoberData
import com.zaurh.bober.data.user.PremiumCards
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import com.zaurh.bober.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepoImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {

    private val _userData = MutableStateFlow<UserData?>(null)
    override val userData: StateFlow<UserData?> get() = _userData

    private val _profileData = MutableStateFlow<UserData?>(null)
    override val profileData: StateFlow<UserData?> get() = _profileData

    private val _boberDataList = MutableStateFlow<List<BoberData?>>(listOf())
    override val boberDataList: StateFlow<List<BoberData?>> get() = _boberDataList

    private val _likedUserList = MutableStateFlow<List<LikedUserData?>>(listOf())
    override val likedUserList: StateFlow<List<LikedUserData?>> get() = _likedUserList

    private val _blockedUserList = MutableStateFlow<List<BlockedUserData?>>(listOf())
    override val blockedUserList: StateFlow<List<BlockedUserData?>> get() = _blockedUserList

    private val _whoLikesList = MutableStateFlow<List<WhoLikesData>>(listOf())
    override val whoLikesList: StateFlow<List<WhoLikesData>> get() = _whoLikesList

    private val _matchedUserList = MutableStateFlow<List<MatchedUserData>>(listOf())
    override val matchedUserList: StateFlow<List<MatchedUserData>> get() = _matchedUserList

    // --- Current user ---

    override suspend fun getUserData(): GetUserDataResponse =
        apiCall({ GetUserDataResponse(success = false, message = it) }) {
            api.getUserData().also { if (it.success) _userData.value = it.user }
        }

    override suspend fun deleteUserData(): MessageResponse =
        apiCall({ MessageResponse(success = false, message = it) }) {
            api.deleteUserData()
        }

    override suspend fun updateUserData(
        userUpdate: UserUpdate,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ): UpdateUserResponse {
        val response = apiCall({ UpdateUserResponse(success = false, message = it) }) {
            api.updateUserData(userUpdate)
        }
        if (response.success) {
            response.user?.let { _userData.value = it }
            onSuccess()
        } else {
            onFailure()
        }
        return response
    }

    override suspend fun sendEmailVerification(email: String): MessageResponse =
        apiCall({ MessageResponse(success = false, message = it) }) {
            api.sendEmailVerification(VerifyEmailRequest(email.trim()))
        }

    override suspend fun verifyEmail(token: String): ValidateEmailResponse {
        val response = apiCall({ ValidateEmailResponse(success = false, message = it) }) {
            api.validateEmailToken(ValidateTokenRequest(token))
        }
        if (response.success) getUserData()
        return response
    }

    override suspend fun boberium(): UpdateUserResponse =
        apiCall({ UpdateUserResponse(success = false, message = it) }) {
            api.activatePremium(PremiumCardRequest(PremiumCards.BOBERIUM)).also { response ->
                if (response.success) response.user?.let { _userData.value = it }
            }
        }

    override suspend fun clearUserData() {
        _userData.value = null
    }

    // --- Other users ---

    override suspend fun getProfileID(username: String): GetUserDataResponse =
        apiCall({ GetUserDataResponse(success = false, message = it) }) {
            api.getProfileByUsername(username).also { if (it.success) _profileData.value = it.user }
        }

    override suspend fun clearProfileData() {
        _profileData.value = null
    }

    // --- Lists ---

    override suspend fun getBobers(): GetBobersResponse =
        apiCall({ GetBobersResponse(success = false, message = it) }) {
            api.getBobers().also { if (it.success) _boberDataList.value = it.boberDataList }
        }

    override suspend fun getMatchedUsers(): MatchedUserResponse =
        apiCall({ MatchedUserResponse(success = false, message = it) }) {
            api.getMatchedUsers().also { response ->
                if (response.success) {
                    _matchedUserList.value = response.matchedUserList
                    _userData.update { it?.copy(matchList = response.matchedUserList) }
                }
            }
        }

    override suspend fun getLikedUsers(): LikedUsersResponse =
        apiCall({ LikedUsersResponse(success = false, message = it) }) {
            api.getLikedUsers().also { response ->
                if (response.success) {
                    _likedUserList.value = response.likedUserList
                    _userData.update { user ->
                        user?.copy(likedUsers = response.likedUserList.mapNotNull { it.id })
                    }
                }
            }
        }

    override suspend fun getBlockedUsers(): BlockedUsersResponse =
        apiCall({ BlockedUsersResponse(success = false, message = it) }) {
            api.getBlockedUsers().also { response ->
                if (response.success) {
                    _blockedUserList.value = response.blockList
                    _userData.update { user ->
                        user?.copy(blockList = response.blockList.mapNotNull { it.id })
                    }
                }
            }
        }

    override suspend fun getWhoLikes(): WhoLikesResponse =
        apiCall({ WhoLikesResponse(success = false, message = it) }) {
            api.getWhoLikes().also { response ->
                if (response.success) {
                    _whoLikesList.value = response.whoLikesList
                    _userData.update { it?.copy(gotLiked = response.whoLikesList) }
                }
            }
        }

    // --- Media & location ---

    override suspend fun uploadMedia(inputStream: InputStream, fileName: String): AddMediaResponse =
        apiCall({ AddMediaResponse(success = false, message = it) }) {
            val bytes = inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
            // The server stores the file under its own generated name; fileName is informational.
            api.uploadMedia(MultipartBody.Part.createFormData("image", fileName, bytes))
        }

    override suspend fun encryptLocation(location: String): GetLocationResponse =
        apiCall({ GetLocationResponse(success = false, message = it) }) {
            api.encryptLocation(location).also { response ->
                // On success the backend returns the stored encrypted location as the message.
                if (response.success) _userData.update { it?.copy(encryptedLocation = response.message) }
            }
        }

    override suspend fun decryptLocation(recipientLocation: String): GetLocationResponse =
        apiCall({ GetLocationResponse(success = false, message = it) }) {
            api.decryptLocation(recipientLocation)
        }

    // --- Relationship actions ---

    override suspend fun like(recipientId: String): MessageResponse =
        relationshipAction({ api.like(recipientId) }) {
            _boberDataList.update { list -> list.filterNot { it?.userId == recipientId } }
            // Likes cost a token and may create a match; both live on the user.
            getUserData()
        }

    override suspend fun unlike(recipientId: String): MessageResponse =
        relationshipAction({ api.unlike(recipientId) }) {
            _likedUserList.update { list -> list.filterNot { it?.id == recipientId } }
            _userData.update { user -> user?.copy(likedUsers = user.likedUsers.orEmpty() - recipientId) }
        }

    // The swipe deck advances on its own, and the server drops passed users from the next /bobers.
    override suspend fun pass(recipientId: String): MessageResponse =
        relationshipAction({ api.pass(recipientId) }) {
            _boberDataList.update { list -> list.filterNot { it?.userId == recipientId } }
        }

    override suspend fun unmatch(recipientId: String): MessageResponse =
        relationshipAction({ api.unmatch(recipientId) }) {
            removeRelationshipLocally(recipientId)
        }

    override suspend fun block(recipientId: String): MessageResponse =
        relationshipAction({ api.block(recipientId) }) {
            removeRelationshipLocally(recipientId)
            _userData.update { user -> user?.copy(blockList = user.blockList.orEmpty() + recipientId) }
        }

    override suspend fun unblock(recipientId: String): MessageResponse =
        relationshipAction({ api.unblock(recipientId) }) {
            _blockedUserList.update { list -> list.filterNot { it?.id == recipientId } }
            _userData.update { user -> user?.copy(blockList = user.blockList.orEmpty() - recipientId) }
        }

    override suspend fun report(recipientId: String, reason: String, optional: String): MessageResponse =
        apiCall({ MessageResponse(success = false, message = it) }) {
            api.report(recipientId = recipientId, reason = reason, optional = optional)
        }

    override fun clear() {
        _userData.value = null
        _profileData.value = null
        _boberDataList.value = listOf()
        _likedUserList.value = listOf()
        _blockedUserList.value = listOf()
        _whoLikesList.value = listOf()
        _matchedUserList.value = listOf()
    }

    /** Mirrors the backend's `relationshipRemoved` (unmatch / block) on the cached state. */
    private fun removeRelationshipLocally(otherId: String) {
        _matchedUserList.update { list -> list.filterNot { it.id == otherId } }
        _likedUserList.update { list -> list.filterNot { it?.id == otherId } }
        _whoLikesList.update { list -> list.filterNot { it.id == otherId } }
        _userData.update { user ->
            user?.copy(
                likedUsers = user.likedUsers.orEmpty() - otherId,
                gotLiked = user.gotLiked?.filterNot { it.id == otherId },
                matchList = user.matchList.orEmpty().filterNot { it.id == otherId },
                chatList = user.chatList?.filterNot { it.recipientId == otherId },
            )
        }
    }

    private suspend inline fun relationshipAction(
        call: () -> MessageResponse,
        onSuccess: () -> Unit
    ): MessageResponse {
        val response = apiCall({ MessageResponse(success = false, message = it) }) { call() }
        if (response.success) onSuccess()
        return response
    }
}
