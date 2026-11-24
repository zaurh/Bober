package com.zaurh.bober.data.user.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.responses.MatchedUserData
import com.zaurh.bober.data.responses.WhoLikesData
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.Languages
import com.zaurh.bober.data.user.PassedUserData
import com.zaurh.bober.data.user.PremiumCardData
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.data.user.UserData

@Entity(tableName = "userEntity")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String?,
    val googleId: String?,
    val premiumCards: List<PremiumCardData>?,
    val name: String?,
    val gender: Gender?,
    val username: String?,
    val email: String?,
    val emailIsVerified: Boolean?,
    val password: String?,
    val encryptedLocation: String?,
    val aboutMe: String?,
    val interests: List<Interests>?,
    val languages: List<Languages>?,
    val jobTitle: String?,
    val height: Int?,
    val birthDate: String?,
    val imageUrl: List<String>?,
    val likedUsers: List<String>?,
    val passedUsers: List<PassedUserData>?,
    val gotLiked: List<WhoLikesData>?,
    val blockList: List<String>?,
    val chatList: List<ChatData>?,
    val matchList: List<MatchedUserData>?,
    val ageRangeStart: Float?,
    val ageRangeEnd: Float?,
    val maximumDistance: Float?,
    val showFullDistance: Boolean?,
    val showMe: ShowMe?,
    val online: Boolean?,
    val likeToken: Int?
)

fun UserEntity.toUserData() = UserData(
    id = userId,
    premiumCards = premiumCards,
    name = name,
    gender = gender,
    username = username,
    email = email,
    emailIsVerified = emailIsVerified,
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


class UserDataConverters {
    private val gson = Gson()

    // Gender converter
    @TypeConverter
    fun fromGender(gender: Gender?): String? {
        return gender?.name
    }

    @TypeConverter
    fun toGender(gender: String?): Gender? {
        return gender?.let { Gender.valueOf(it) }
    }

    // List of LastMessageData converter
    @TypeConverter
    fun fromImageUrl(data: List<String>?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toImageUrl(data: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

    // Interests converter
    @TypeConverter
    fun fromInterests(interests: Interests?): String? {
        return gson.toJson(interests)
    }

    @TypeConverter
    fun toInterests(interests: String?): Interests? {
        return gson.fromJson(interests, Interests::class.java)
    }

    // List of Interests converter
    @TypeConverter
    fun fromInterestsList(interests: List<Interests>?): String? {
        return gson.toJson(interests)
    }

    @TypeConverter
    fun toInterestsList(interests: String?): List<Interests>? {
        val listType = object : TypeToken<List<Interests>>() {}.type
        return gson.fromJson(interests, listType)
    }

    @TypeConverter
    fun fromCardDataList(cardData: List<PremiumCardData>): String? {
        return gson.toJson(cardData)
    }

    @TypeConverter
    fun toCardDataList(cardData: String?): List<PremiumCardData>? {
        val listType = object : TypeToken<List<PremiumCardData>>() {}.type
        return gson.fromJson(cardData, listType)
    }

    // Languages converter
    @TypeConverter
    fun fromLanguages(languages: Languages?): String? {
        return gson.toJson(languages)
    }

    @TypeConverter
    fun toLanguages(languages: String?): Languages? {
        return gson.fromJson(languages, Languages::class.java)
    }

    // List of Languages converter
    @TypeConverter
    fun fromLanguagesList(languages: List<Languages>?): String? {
        return gson.toJson(languages)
    }

    @TypeConverter
    fun toLanguagesList(languages: String?): List<Languages>? {
        val listType = object : TypeToken<List<Languages>>() {}.type
        return gson.fromJson(languages, listType)
    }

    // PassedUserData converter
    @TypeConverter
    fun fromPassedUserData(data: PassedUserData?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toPassedUserData(data: String?): PassedUserData? {
        return gson.fromJson(data, PassedUserData::class.java)
    }

    // List of PassedUserData converter
    @TypeConverter
    fun fromPassedUserDataList(data: List<PassedUserData>?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toPassedUserDataList(data: String?): List<PassedUserData>? {
        val listType = object : TypeToken<List<PassedUserData>>() {}.type
        return gson.fromJson(data, listType)
    }

    // WhoLikesData converter
    @TypeConverter
    fun fromWhoLikesData(data: WhoLikesData?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toWhoLikesData(data: String?): WhoLikesData? {
        return gson.fromJson(data, WhoLikesData::class.java)
    }

    // List of WhoLikesData converter
    @TypeConverter
    fun fromWhoLikesDataList(data: List<WhoLikesData>?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toWhoLikesDataList(data: String?): List<WhoLikesData>? {
        val listType = object : TypeToken<List<WhoLikesData>>() {}.type
        return gson.fromJson(data, listType)
    }

    // ChatData converter
    @TypeConverter
    fun fromChatData(data: ChatData?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toChatData(data: String?): ChatData? {
        return gson.fromJson(data, ChatData::class.java)
    }

    // List of ChatData converter
    @TypeConverter
    fun fromChatDataList(data: List<ChatData>?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toChatDataList(data: String?): List<ChatData>? {
        val listType = object : TypeToken<List<ChatData>>() {}.type
        return gson.fromJson(data, listType)
    }

    // MatchedUserData converter
    @TypeConverter
    fun fromMatchedUserData(data: MatchedUserData?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toMatchedUserData(data: String?): MatchedUserData? {
        return gson.fromJson(data, MatchedUserData::class.java)
    }

    // List of MatchedUserData converter
    @TypeConverter
    fun fromMatchedUserDataList(data: List<MatchedUserData>?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toMatchedUserDataList(data: String?): List<MatchedUserData>? {
        val listType = object : TypeToken<List<MatchedUserData>>() {}.type
        return gson.fromJson(data, listType)
    }

    // ShowMe converter
    @TypeConverter
    fun fromShowMe(showMe: ShowMe?): String? {
        return showMe?.name
    }

    @TypeConverter
    fun toShowMe(showMe: String?): ShowMe? {
        return showMe?.let { ShowMe.valueOf(it) }
    }

    @TypeConverter
    fun fromMessageStatus(status: MessageStatus): String {
        return status.name
    }

    @TypeConverter
    fun toMessageStatus(status: String): MessageStatus {
        return MessageStatus.valueOf(status)
    }
}
