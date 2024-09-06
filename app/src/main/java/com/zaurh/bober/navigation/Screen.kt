package com.zaurh.bober.navigation

sealed class Screen(val route: String){

    data object PagerScreen: Screen(route = "pager_screen")
    data object SignUpUsername: Screen(route = "sign_up_username_screen")
    data object SignUpGender: Screen(route = "sign_up_gender_screen")
    data object SignUpAbout: Screen(route = "sign_up_about")
    data object SignUpImage: Screen(route = "sign_up_image")
    data object SignInScreen: Screen(route = "sign_in_screen")
    data object MatchScreen: Screen(route = "match_screen")
    data object HomeScreen: Screen(route = "home_screen")
    data object ChatScreen: Screen(route = "chat_screen/{username}")
    data object AccountScreen: Screen(route = "account_screen")
    data object ProfileScreen : Screen(route = "profile_screen/{username}/{imageIndex}")
    data object EditProfileScreen: Screen(route = "edit_profile_screen")
    data object SettingsScreen: Screen(route = "settings_screen")
    data object NotificationScreen: Screen(route = "notification_screen")
    data object BlockedUsersScreen: Screen(route = "blocked_users_screen")
    data object LikedUsersScreen: Screen(route = "liked_users_screen")
    data object WhoLikesScreen: Screen(route = "who_likes_screen")


    fun createRoute(username: String? = null): String {
        return when (this) {
            is ChatScreen -> route.replace("{username}", username.toString())
            else -> route
        }
    }

    fun passUsername(username: String? = null): String {
        return when (this) {
            is ProfileScreen -> route
                .replace("{username}", username.toString())
            else -> route
        }
    }

}