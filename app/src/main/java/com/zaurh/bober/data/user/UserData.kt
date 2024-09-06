package com.zaurh.bober.data.user

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: String? = "",
    val name: String? = "",
    val gender: Gender? = Gender.PREFER_NOT_TO_SAY,
    val username: String? = "",
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
    val gotLiked: List<String>? = listOf(),
    val blockList: List<String>? = listOf(),
    val chatList: List<ChatData>? = listOf(),
    val matchList: List<MatchData>? = listOf(),
    val ageRangeStart: Float? = 18f,
    val ageRangeEnd: Float? = 100f,
    val maximumDistance: Float? = 200f,
    val showFullDistance: Boolean? = true,
    val showMe: ShowMe? = ShowMe.EVERYONE,
    val salt: String? = "",
    val online: Boolean? = false,
    val lastSeen: Long? = 0
)

