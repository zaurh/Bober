package com.zaurh.bober.navigation

sealed class Screen(val route: String){

    data object WelcomeScreen: Screen(route = "welcome_screen")
    data object PagerScreen: Screen(route = "pager_screen")
    data object SignUpUsername: Screen(route = "sign_up_username_screen")
    data object SignUpGender: Screen(route = "sign_up_gender_screen")
    data object SignUpAbout: Screen(route = "sign_up_about")
    data object SignUpImage: Screen(route = "sign_up_image")
    data object EditImage: Screen(route = "edit_image")
    data object CropImage: Screen(route = "crop_image")
    data object SignInScreen: Screen(route = "sign_in_screen")
    data object ForgotPasswordScreen: Screen(route = "forgot_password_screen")
    data object ResetPasswordScreen: Screen(route = "reset_password_screen")
    data object MatchScreen: Screen(route = "match_screen")
    data object HomeScreen: Screen(route = "home_screen")
    data object ChatScreen: Screen(route = "chat_screen/{recipientId}")
    data object AccountScreen: Screen(route = "account_screen")
    data object ProfileScreen : Screen(route = "profile_screen/{recipientId}")
    data object EditProfileScreen: Screen(route = "edit_profile_screen")
    data object SettingsScreen: Screen(route = "settings_screen")
    data object NotificationScreen: Screen(route = "notification_screen")
    data object AccountCenterScreen: Screen(route = "account_center_screen")
    data object EmailScreen: Screen(route = "email_screen")
    data object PasswordScreen: Screen(route = "password_screen")
    data object BlockedUsersScreen: Screen(route = "blocked_users_screen")
    data object LikedUsersScreen: Screen(route = "liked_users_screen")
    data object WhoLikesScreen: Screen(route = "who_likes_screen")


    fun createRoute(recipientId: String): String {
        return route.replace("{recipientId}", recipientId)
    }

}

