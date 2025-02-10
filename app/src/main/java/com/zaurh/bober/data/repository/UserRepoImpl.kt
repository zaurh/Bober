package com.zaurh.bober.data.repository

import android.util.Log
import com.zaurh.bober.data.repository.state.BoberListState
import com.zaurh.bober.data.requests.PremiumCardRequest
import com.zaurh.bober.data.responses.AddMediaResponse
import com.zaurh.bober.data.responses.BlockedUserData
import com.zaurh.bober.data.responses.BlockedUsersResponse
import com.zaurh.bober.data.responses.GetBobersResponse
import com.zaurh.bober.data.responses.GetLocationResponse
import com.zaurh.bober.data.responses.GetUserDataResponse
import com.zaurh.bober.data.responses.LikedUsersResponse
import com.zaurh.bober.data.responses.MatchedUserData
import com.zaurh.bober.data.responses.MatchedUserResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.UpdateUserResponse
import com.zaurh.bober.data.responses.WhoLikesData
import com.zaurh.bober.data.responses.WhoLikesResponse
import com.zaurh.bober.data.user.BoberData
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import com.zaurh.bober.data.user.room.UserDao
import com.zaurh.bober.data.user.room.UserEntity
import com.zaurh.bober.data.user.room.toUserData
import com.zaurh.bober.data.user.toUserEntity
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.preferences.BoberPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val api: UserApi,
    private val boberPreferences: BoberPreferences,
    private val userDao: UserDao
) : UserRepository {

    private val _userData = MutableStateFlow<UserData?>(null)
    override val userData: StateFlow<UserData?> get() = _userData

    private val _profileData = MutableStateFlow<UserData?>(null)
    override val profileData: StateFlow<UserData?> get() = _profileData

    private val _boberDataList = MutableStateFlow(BoberListState())
    override val boberDataList: StateFlow<BoberListState>
        get() = _boberDataList

//    private val _likedUserList = MutableStateFlow<List<LikedUserData?>>(listOf())
//    override val likedUserList: StateFlow<List<LikedUserData?>>
//        get() = _likedUserList

    private val _blockedUserList = MutableStateFlow<List<BlockedUserData?>>(listOf())
    override val blockedUserList: StateFlow<List<BlockedUserData?>>
        get() = _blockedUserList

    private val _whoLikesList = MutableStateFlow<List<WhoLikesData>>(listOf())
    override val whoLikesList: StateFlow<List<WhoLikesData>>
        get() = _whoLikesList


    private val _matchList = MutableStateFlow<List<MatchedUserData>>(listOf())
    override val matchList: StateFlow<List<MatchedUserData>>
        get() = _matchList

//    private val _matchedUserList = MutableStateFlow<List<MatchedUserData>>(listOf())
//    override val matchedUserList: StateFlow<List<MatchedUserData>>
//        get() = _matchedUserList


    suspend fun getToken(): String? {
        return boberPreferences.getJwtKey.first()
    }

    suspend fun getCurrentUserId(): String? {
        return boberPreferences.getCurrentUserId.first()
    }

    suspend fun insertUserData(userEntity: UserEntity) {
        userDao.insertUser(userEntity)
    }

    suspend fun deleteUserDataFromLocal(userId: String) {
        userDao.deleteUser(userId)
    }

    override suspend fun getUserData(): GetUserDataResponse {
        return try {
            val token = getToken()
            val response = api.getUserData("Bearer $token")

            if (response.success) {
                _userData.value = response.user
                if (response.user != null) {
                    deleteUserDataFromLocal(response.user.id ?: "")
                    insertUserData(response.user.toUserEntity())
                }
            } else {
                Log.d("GetUserData", "Error Getting User data")
            }
            GetUserDataResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )

        } catch (e: Exception) {
            val currentUserId = getCurrentUserId()
            _userData.value = userDao.getUserById(currentUserId ?: "")?.toUserData()
            Log.d("dfklsdjkldsfj", "${userData.value}")
            GetUserDataResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun deleteUserData(): MessageResponse {
        return try {
            val token = getToken()

            val response = api.deleteUserData("Bearer $token")
            MessageResponse(
                success = response.success,
                message = response.message
            )

        } catch (e: Exception) {
            MessageResponse(success = false, message = e.message ?: "")
        }
    }


    override suspend fun getProfileID(recipientId: String): GetUserDataResponse {
        return try {
            val response = api.getProfileID(recipientId)

            if (response.success) {
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


    override suspend fun updateUserData(
        userUpdate: UserUpdate,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ): UpdateUserResponse {
        val token = getToken()

        return try {
            val response = api.updateUserData(
                token = "Bearer $token",
                userUpdate = userUpdate
            )

            if (response.success) {
                onSuccess()
                _userData.value = response.user

            } else {
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


    override suspend fun getBobers(): GetBobersResponse {
        _boberDataList.value = BoberListState(loading = true)
        return try {
            val token = getToken()

            val response = api.getBobers("Bearer $token")
            if (response.success) {
                if (response.boberDataList.isEmpty()) {
                    _boberDataList.value = BoberListState(empty = true)
                } else {
                    _boberDataList.value =
                        BoberListState(boberList = response.boberDataList, empty = false)
                }
            } else {
                _boberDataList.value = BoberListState(error = response.message)
            }

            GetBobersResponse(
                success = response.success,
                boberDataList = response.boberDataList,
                message = response.message
            )
        } catch (e: Exception) {
            _boberDataList.value = BoberListState(error = e.message ?: "Unknown error")

            GetBobersResponse(success = false, message = e.message ?: "")
        }
    }

    fun updateBoberList(boberList: List<BoberData>) {
        _boberDataList.value = BoberListState(boberList = boberList)
    }

    override suspend fun getMatchedUsers(): MatchedUserResponse {
        return try {
            val token = getToken()

            val response = api.getMatchedUsers("Bearer $token")

            MatchedUserResponse(
                success = response.success,
                matchedUserList = response.matchedUserList,
                message = response.message
            )
        } catch (e: Exception) {
            MatchedUserResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun getLikedUsers(): LikedUsersResponse {
        return try {
            val token = getToken()

            val response = api.getLikedUsers("Bearer $token")
//            if (response.success) {
//                _likedUserList.value = response.likedUserList
//                _userData.value = userData.value?.copy(
//                    likedUsers = response.likedUserList.map { it.id ?: "" }
//                )
//            }

            LikedUsersResponse(
                success = response.success,
                likedUserList = response.likedUserList,
                message = response.message
            )
        } catch (e: Exception) {
            LikedUsersResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun getBlockedUsers(): BlockedUsersResponse {
        return try {
            val token = getToken()

            val response = api.getBlockedUsers("Bearer $token")

//            if (response.success) {
//                _blockedUserList.value = response.blockList
//                _userData.value = userData.value?.copy(
//                    blockList = response.blockList.map { it.id ?: "" }
//                )
//            }

            BlockedUsersResponse(
                success = response.success,
                blockList = response.blockList,
                message = response.message
            )
        } catch (e: Exception) {
            BlockedUsersResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun getWhoLikes(): WhoLikesResponse {
        return try {
            val token = getToken()

            val response = api.getWhoLikes("Bearer $token")
//            if (response.success) {
//                _whoLikesList.value = response.whoLikesList
//                _userData.value = userData.value?.copy(
//                    gotLiked = response.whoLikesList
//                )
//            }

            WhoLikesResponse(
                success = response.success,
                whoLikesList = response.whoLikesList,
                message = response.message
            )
        } catch (e: Exception) {
            WhoLikesResponse(success = false, message = e.message ?: "")
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
        val token = getToken()

        return try {
            val response = api.like(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success) {
                _userData.value = response.user
                val boberListMutable = boberDataList.value.boberList.toMutableList()
                boberListMutable.removeIf { it.userId == recipientId }

                _boberDataList.value = boberDataList.value.copy(
                    boberList = boberListMutable
                )
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
        val token = getToken()

        return try {
            val response = api.pass(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success) {
                _userData.value = response.user

                val boberListMutable = boberDataList.value.boberList.toMutableList()
                boberListMutable.removeIf { it.userId == recipientId }

                _boberDataList.value = boberDataList.value.copy(
                    boberList = boberListMutable
                )

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
        val token = getToken()

        return try {
            val response = api.unlike(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success) {
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
        val token = getToken()

        return try {
            val response = api.unMatch(
                token = "Bearer $token",
                recipientId = recipientId
            )

//            if (response.success) {
//                _userData.value = response.user
//            }
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
        val token = getToken()

        return try {
            val response = api.block(
                token = "Bearer $token",
                recipientId = recipientId
            )
            Log.d("sdajkdla", "$response")
            UpdateUserResponse(
                success = response.success,
                user = response.user,
                message = response.message
            )
        } catch (e: Exception) {
            Log.d("sdajkdla", "${e.message}")

            UpdateUserResponse(success = false, message = e.message ?: "")
        }
    }

    override suspend fun report(
        recipientId: String,
        reason: String,
        optional: String
    ): MessageResponse {
        val token = getToken()

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
        val token = getToken()

        return try {
            val response = api.unblock(
                token = "Bearer $token",
                recipientId = recipientId
            )

            if (response.success) {
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

    override suspend fun takePremiumCard(
        premiumCardRequest: PremiumCardRequest
    ): UpdateUserResponse {
        val token = getToken()

        return try {
            val response = api.takePremiumCard(
                token = "Bearer $token",
                premiumCardRequest = premiumCardRequest
            )

            if (response.success) {
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
            val token = getToken()

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
            val token = getToken()

            val response =
                api.decryptLocation(token = "Bearer $token", recipientLocation = recipientLocation)

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