package com.zaurh.bober.data.user

import com.zaurh.bober.data.message.LastMessageData
import com.zaurh.bober.data.responses.MatchedUserData
import com.zaurh.bober.data.responses.WhoLikesData
import com.zaurh.bober.data.user.room.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: String? = "",
    val fcmToken: String? = "",
    val isPremium: Boolean? = false,
    val name: String? = "",
    val gender: Gender? = Gender.PREFER_NOT_TO_SAY,
    val username: String? = "",
    val email: String? = "",
    val emailIsVerified: Boolean? = false,
    val lastMessages: List<LastMessageData>? = listOf(),
    val password: String? = "",
    val encryptedLocation: String? = "",
    val aboutMe: String? = "",
    val interests: List<Interests>? = listOf(),
    val languages: List<Languages>? = listOf(),
    val jobTitle: String? = "",
    val height: Int? = 0,
    val birthDate: String? = "",
    val imageUrl: List<String>? = listOf(),
    val likedUsers: List<String>? = listOf(),
    val passedUsers: List<PassedUserData>? = listOf(),
    val gotLiked: List<WhoLikesData>? = listOf(),
    val blockList: List<String>? = listOf(),
    val chatList: List<ChatData>? = listOf(),
    val matchList: List<MatchedUserData>? = listOf(),
    val ageRangeStart: Float? = 18f,
    val ageRangeEnd: Float? = 100f,
    val maximumDistance: Float? = 200f,
    val showFullDistance: Boolean? = true,
    val showMe: ShowMe? = ShowMe.EVERYONE,
    val salt: String? = "",
    val online: Boolean? = false,
    val lastSeen: Long? = 0,
    val likeToken: Int? = 0
)

fun UserData.toUserEntity() = UserEntity(
    userId = id,
    isPremium = isPremium,
    name = name,
    gender = gender,
    username = username,
    email = email,
    emailIsVerified = emailIsVerified,
    lastMessages = lastMessages,
    password = password,
    encryptedLocation = encryptedLocation,
    aboutMe = aboutMe,
    interests = interests,
    languages = languages,
    jobTitle = jobTitle,
    height = height,
    birthDate = birthDate,
    imageUrl = imageUrl,
    likedUsers = likedUsers,
    passedUsers = passedUsers,
    gotLiked = gotLiked,
    blockList = blockList,
    chatList = chatList,
    matchList = matchList,
    ageRangeStart = ageRangeStart,
    ageRangeEnd = ageRangeEnd,
    maximumDistance = maximumDistance,
    showFullDistance = showFullDistance,
    showMe = showMe,
    online = online,
    likeToken = likeToken
)
