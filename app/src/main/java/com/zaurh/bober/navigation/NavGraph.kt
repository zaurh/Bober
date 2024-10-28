package com.zaurh.bober.navigation

import SignUpGender
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.zaurh.bober.screen.account.AccountScreen
import com.zaurh.bober.screen.blocked_users.BlockedUsersScreen
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.chat.ChatScreen
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.edit_profile.EditProfileScreen
import com.zaurh.bober.screen.forgot_password.ForgotPasswordScreen
import com.zaurh.bober.screen.home.HomeScreen
import com.zaurh.bober.screen.home.HomeViewModel
import com.zaurh.bober.screen.liked_users.LikedUsersScreen
import com.zaurh.bober.screen.match.MatchScreen
import com.zaurh.bober.screen.match.MatchViewModel
import com.zaurh.bober.screen.pager.PagerScreen
import com.zaurh.bober.screen.profile.ProfileScreen
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.screen.reset_password.ResetPasswordScreen
import com.zaurh.bober.screen.reset_password.ResetPasswordViewModel
import com.zaurh.bober.screen.settings.SettingsScreen
import com.zaurh.bober.screen.settings.account_center.AccountCenterScreen
import com.zaurh.bober.screen.settings.account_center.email.EmailScreen
import com.zaurh.bober.screen.settings.account_center.password.PasswordScreen
import com.zaurh.bober.screen.settings.notifications.NotificationScreen
import com.zaurh.bober.screen.sign_in.SignInScreen
import com.zaurh.bober.screen.sign_in.SignInViewModel
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.screen.sign_up.sign_up_about.SignUpAbout
import com.zaurh.bober.screen.sign_up.sign_up_image.SignUpImage
import com.zaurh.bober.screen.sign_up.sign_up_username.SignUpUsername
import com.zaurh.bober.screen.who_likes.WhoLikesScreen
import com.zaurh.bober.screen.who_likes.WhoLikesViewModel
import com.zaurh.bober.util.Constants.BASE_URL

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavGraph(
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit,
    navController: NavHostController,
    startDestination: String = Screen.SignInScreen.route
) {
    val chatScreenViewModel = hiltViewModel<ChatScreenViewModel>()
    val matchViewModel = hiltViewModel<MatchViewModel>()
    val signUpViewModel = hiltViewModel<SignUpViewModel>()
    val signInViewModel = hiltViewModel<SignInViewModel>()
    val blockedUsersViewModel = hiltViewModel<BlockedUsersViewModel>()
    val whoLikesViewModel = hiltViewModel<WhoLikesViewModel>()
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val resetPasswordViewModel = hiltViewModel<ResetPasswordViewModel>()



    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screen.PagerScreen.route) {
            PagerScreen(
                navController = navController,
                chatScreenViewModel = chatScreenViewModel,
                matchViewModel = matchViewModel,
                homeViewModel = homeViewModel
            )
        }

        composable(route = Screen.SignUpUsername.route) {
            SignUpUsername(navController = navController, signUpViewModel = signUpViewModel)
        }
        composable(route = Screen.SignUpGender.route) {
            SignUpGender(navController = navController, signUpViewModel = signUpViewModel)
        }
        composable(route = Screen.SignUpAbout.route) {
            SignUpAbout(navController = navController, signUpViewModel = signUpViewModel)
        }
        composable(route = Screen.SignUpImage.route) {
            SignUpImage(signUpViewModel = signUpViewModel)
        }
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(
                navController = navController,
                signInViewModel = signInViewModel
            )
        }
        composable(route = Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(
                navController = navController
            )
        }
        composable(route = Screen.AccountCenterScreen.route) {
            AccountCenterScreen(
                navController = navController
            )
        }
        composable(
            route = Screen.EmailScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "$BASE_URL/verify_email?token={token}"
                    action = Intent.ACTION_VIEW
                }
            )
        ) { entry ->
            val tokenAndTime = entry.arguments?.getString("token")
            EmailScreen(
                token = tokenAndTime ?: "",
                navController = navController
            )
        }
        composable(route = Screen.PasswordScreen.route) {
            PasswordScreen(
                navController = navController
            )
        }

        composable(
            route = Screen.ResetPasswordScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "$BASE_URL/reset-password?token={token}"
                    action = Intent.ACTION_VIEW
                }
            )
        ) { entry ->
            val tokenAndUserIdAndTime = entry.arguments?.getString("token")
            val userId = tokenAndUserIdAndTime?.dropLast(13)?.drop(36)

            ResetPasswordScreen(
                id = userId ?: "",
                token = tokenAndUserIdAndTime ?: "bos geldi",
                navController = navController,
                resetPasswordViewModel = resetPasswordViewModel
            )
        }
        composable(route = Screen.MatchScreen.route) {
            MatchScreen(
                matchViewModel = matchViewModel,
                navController = navController
            )
        }
        composable(route = Screen.AccountScreen.route) {
            AccountScreen(
                navController = navController
            )
        }
        composable(
            route = Screen.ProfileScreen.route, arguments = listOf(
                navArgument("username") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val username = arguments.getString("username")
            ProfileScreen(
                username = username ?: "",
                navController = navController,
                profileViewModel = profileViewModel
            )
        }
        composable(
            route = Screen.ChatScreen.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        )
        { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val username = arguments.getString("username")
            ChatScreen(
                username = username ?: "",
                navController = navController,
                chatScreenViewModel = chatScreenViewModel
            )
        }
        composable(route = Screen.BlockedUsersScreen.route) {
            BlockedUsersScreen(
                navController = navController,
                blockedUsersViewModel = blockedUsersViewModel
            )
        }
        composable(route = Screen.LikedUsersScreen.route) {
            LikedUsersScreen(
                navController = navController
            )
        }
        composable(route = Screen.WhoLikesScreen.route) {
            WhoLikesScreen(
                navController = navController,
                whoLikesViewModel = whoLikesViewModel
            )
        }
        composable(route = Screen.SettingsScreen.route) {
            SettingsScreen(
                navController = navController,
                darkTheme = darkTheme,
                onThemeUpdated = onThemeUpdated
            )
        }
        composable(route = Screen.NotificationScreen.route) {
            NotificationScreen(
                navController = navController
            )
        }


        composable(route = Screen.EditProfileScreen.route) {
            EditProfileScreen(
                navController = navController
            )
        }
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                chatScreenViewModel = chatScreenViewModel,
                homeViewModel = homeViewModel
            )
        }
    }



}