package com.zaurh.bober.navigation

import SignUpGender
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.zaurh.bober.screen.account.AccountViewModel
import com.zaurh.bober.screen.blocked_users.BlockedUsersScreen
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.chat.ChatScreen
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.edit_profile.EditProfileScreen
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel
import com.zaurh.bober.screen.forgot_password.ForgotPasswordScreen
import com.zaurh.bober.screen.home.HomeViewModel
import com.zaurh.bober.screen.liked_users.LikedUsersScreen
import com.zaurh.bober.screen.match.MatchViewModel
import com.zaurh.bober.screen.pager.PagerScreen
import com.zaurh.bober.screen.profile.ProfileScreen
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.screen.reset_password.ResetPasswordScreen
import com.zaurh.bober.screen.reset_password.ResetPasswordViewModel
import com.zaurh.bober.screen.settings.SettingsScreen
import com.zaurh.bober.screen.settings.SettingsViewModel
import com.zaurh.bober.screen.settings.account_center.AccountCenterScreen
import com.zaurh.bober.screen.settings.account_center.email.EmailScreen
import com.zaurh.bober.screen.settings.account_center.password.PasswordScreen
import com.zaurh.bober.screen.settings.notifications.NotificationScreen
import com.zaurh.bober.screen.sign_in.SignInScreen
import com.zaurh.bober.screen.sign_in.SignInViewModel
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.screen.sign_up.sign_up_about.SignUpAbout
import com.zaurh.bober.screen.sign_up.sign_up_image.SignUpImage
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.ImageEditScreen
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.ImageEditViewModel
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.crop_image.CropImageScreen
import com.zaurh.bober.screen.sign_up.sign_up_username.SignUpUsername
import com.zaurh.bober.screen.who_likes.WhoLikesScreen
import com.zaurh.bober.screen.who_likes.WhoLikesViewModel
import com.zaurh.bober.util.Constants.BASE_URL

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavGraph(
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit,
    deepLink: String,
    navController: NavHostController,
    startDestination: String = Screen.SignInScreen.route
) {


    NavHost(navController = navController, startDestination = startDestination) {

        navigation(startDestination = "sign_up_graph", route = "main_graph") {

            // Sign Up Graph
            navigation(startDestination = Screen.SignUpUsername.route, route = "sign_up_graph") {
                composable(route = Screen.SignUpImage.route) { backStackEntry ->
                    val signUpViewModel = backStackEntry.sharedViewModel<SignUpViewModel>(navController, "sign_up_graph")
                    val imageEditViewModel = backStackEntry.sharedViewModel<ImageEditViewModel>(navController, "main_graph")

                    SignUpImage(
                        signUpViewModel = signUpViewModel,
                        imageEditViewModel = imageEditViewModel,
                        navController = navController
                    )
                }
                composable(route = Screen.SignUpUsername.route) { backStackEntry ->
                    val signUpViewModel = backStackEntry.sharedViewModel<SignUpViewModel>(navController, "sign_up_graph")
                    SignUpUsername(navController = navController, signUpViewModel = signUpViewModel)
                }

                composable(route = Screen.SignUpGender.route) { backStackEntry ->
                    val signUpViewModel = backStackEntry.sharedViewModel<SignUpViewModel>(navController, "sign_up_graph")
                    SignUpGender(navController = navController, signUpViewModel = signUpViewModel)
                }

                composable(route = Screen.SignUpAbout.route) { backStackEntry ->
                    val signUpViewModel = backStackEntry.sharedViewModel<SignUpViewModel>(navController, "sign_up_graph")
                    SignUpAbout(navController = navController, signUpViewModel = signUpViewModel)
                }
            }

            // Image Edit Graph
            navigation(startDestination = Screen.EditImage.route, route = "image_edit_graph") {
                composable(route = Screen.EditProfileScreen.route) { backStackEntry ->
                    val editProfileViewModel = hiltViewModel<EditProfileViewModel>()
                    val imageEditViewModel = backStackEntry.sharedViewModel<ImageEditViewModel>(navController, "main_graph")


                    EditProfileScreen(
                        navController = navController,
                        editProfileViewModel = editProfileViewModel,
                        imageEditViewModel = imageEditViewModel
                    )
                }

                composable(Screen.EditImage.route) { backStackEntry ->
                    val imageEditViewModel = backStackEntry.sharedViewModel<ImageEditViewModel>(navController, "main_graph")
                    ImageEditScreen(navController = navController, imageEditViewModel = imageEditViewModel)
                }

                composable(route = Screen.CropImage.route) { backStackEntry ->
                    val imageEditViewModel = backStackEntry.sharedViewModel<ImageEditViewModel>(navController, "main_graph")
                    CropImageScreen(imageEditViewModel = imageEditViewModel, navController = navController)
                }
            }

        }






        composable(route = Screen.SignInScreen.route) {
            val signInViewModel = hiltViewModel<SignInViewModel>()

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
            val resetPasswordViewModel = hiltViewModel<ResetPasswordViewModel>()

            ResetPasswordScreen(
                id = userId ?: "",
                token = tokenAndUserIdAndTime ?: "bos geldi",
                navController = navController,
                resetPasswordViewModel = resetPasswordViewModel
            )
        }


        navigation(startDestination = Screen.PagerScreen.route, route = "chat_graph") {
            composable(route = Screen.PagerScreen.route) { backStackEntry ->
                val accountViewModel =
                    backStackEntry.sharedViewModel<AccountViewModel>(navController, "chat_graph")
                val homeViewModel = backStackEntry.sharedViewModel<HomeViewModel>(navController, "chat_graph")
                val matchViewModel = backStackEntry.sharedViewModel<MatchViewModel>(navController, "chat_graph")
                val chatScreenViewModel =
                    backStackEntry.sharedViewModel<ChatScreenViewModel>(navController, "chat_graph")
                val profileViewModel =
                    backStackEntry.sharedViewModel<ProfileViewModel>(navController, "chat_graph")

                PagerScreen(
                    navController = navController,
                    chatScreenViewModel = chatScreenViewModel,
                    matchViewModel = matchViewModel,
                    homeViewModel = homeViewModel,
                    accountViewModel = accountViewModel,
                    profileViewModel = profileViewModel,
                    deepLink = deepLink
                )
            }
            composable(
                route = Screen.ProfileScreen.route,
                arguments = listOf(navArgument("recipientId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val arguments = requireNotNull(backStackEntry.arguments)
                val recipientId = arguments.getString("recipientId")
                val profileViewModel =
                    backStackEntry.sharedViewModel<ProfileViewModel>(navController, "chat_graph")
                val blockedUsersViewModel =
                    backStackEntry.sharedViewModel<BlockedUsersViewModel>(navController, "chat_graph")
                val chatScreenViewModel =
                    backStackEntry.sharedViewModel<ChatScreenViewModel>(navController, "chat_graph")

                ProfileScreen(
                    recipientId = recipientId ?: "",
                    navController = navController,
                    profileViewModel = profileViewModel,
                    chatScreenViewModel = chatScreenViewModel,
                    blockedUsersViewModel = blockedUsersViewModel
                )
            }
            composable(
                route = "chat_screen/{recipientId}",
                arguments = listOf(navArgument("recipientId") { type = NavType.StringType }),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "https://bober-edbb31a834f6.herokuapp.com/open_chat?userId={recipientId}"
                        action = Intent.ACTION_VIEW
                    }
                )
            ) { backStackEntry ->
                val arguments = requireNotNull(backStackEntry.arguments)
                val userId = arguments.getString("recipientId") ?: ""
                val profileViewModel =
                    backStackEntry.sharedViewModel<ProfileViewModel>(navController, "chat_graph")
                val blockedUsersViewModel =
                    backStackEntry.sharedViewModel<BlockedUsersViewModel>(navController, "chat_graph")
                val chatScreenViewModel =
                    backStackEntry.sharedViewModel<ChatScreenViewModel>(navController, "chat_graph")


                ChatScreen(
                    recipientId = userId,
                    navController = navController,
                    chatScreenViewModel = chatScreenViewModel,
                    profileViewModel = profileViewModel,
                    blockedUsersViewModel = blockedUsersViewModel
                )
            }

            composable(route = Screen.BlockedUsersScreen.route) { backStackEntry ->
                val blockedUsersViewModel =
                    backStackEntry.sharedViewModel<BlockedUsersViewModel>(navController, "chat_graph")
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
            composable(route = Screen.WhoLikesScreen.route) { backStackEntry ->
                val whoLikesViewModel = hiltViewModel<WhoLikesViewModel>()
                val chatScreenViewModel =
                    backStackEntry.sharedViewModel<ChatScreenViewModel>(navController, "chat_graph")

                WhoLikesScreen(
                    navController = navController,
                    whoLikesViewModel = whoLikesViewModel,
                    chatScreenViewModel = chatScreenViewModel
                )
            }
        }







        composable(route = Screen.SettingsScreen.route) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()

            SettingsScreen(
                navController = navController,
                darkTheme = darkTheme,
                onThemeUpdated = onThemeUpdated,
                settingsViewModel = settingsViewModel
            )
        }
        composable(route = Screen.NotificationScreen.route) {

            NotificationScreen(
                navController = navController
            )
        }


    }


}


@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
    parentRoute: String
): T {
    val parentEntry = remember(navController) {
        navController.getBackStackEntry(parentRoute) // Dynamically scope to parent
    }
    return hiltViewModel(parentEntry)
}
