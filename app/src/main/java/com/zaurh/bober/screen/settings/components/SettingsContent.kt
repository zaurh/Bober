package com.zaurh.bober.screen.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.settings.SettingsViewModel
import com.zaurh.bober.screen.settings.theme.CustomSwitcher
import com.zaurh.bober.util.sendMail

@Composable
fun SettingsContent(
    paddingValues: PaddingValues,
    navController: NavController,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val currentUserData = settingsViewModel.userDataState.collectAsState()
    val deleteAccountState = settingsViewModel.deleteAccountState.value
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val currentUser = currentUserData.value ?: UserData()

            TitleItem(title = "Filter")
            SettingsAgeRange(
                userData = currentUser,
                onValueChange = { start, end ->
                    settingsViewModel.updateAgeRange(start, end)
                }
            )
            Spacer(modifier = Modifier.size(12.dp))
            SettingsDistance(
                userData = currentUser,
                onValueChange = { distance ->
                    settingsViewModel.updateMaximumDistance(distance)
                },
                onSelectCheckBox = {
                    settingsViewModel.showFullDistance(it)
                }
            )
            Spacer(modifier = Modifier.size(12.dp))
            val showMeDesc = currentUser.showMe?.displayName ?: ""
            SettingsItem(title = "Show me", description = showMeDesc, onClick = {
                settingsViewModel.onShowMeStateChange(true)
            }, icon = R.drawable.bober_ic)
            SettingsDivider()

            TitleItem(title = "Activity")
            SettingsItem(
                title = "Blocked users",
                description = "See users who you blocked",
                onClick = {
                    navController.navigate(Screen.BlockedUsersScreen.route)
                },
                icon = R.drawable.block_ic
            )
            Spacer(modifier = Modifier.size(12.dp))

            SettingsItem(
                title = "Liked users",
                description = "See users who you liked",
                onClick = {
                    navController.navigate(Screen.LikedUsersScreen.route)
                },
                icon = R.drawable.heart_ic
            )
            SettingsDivider()
            TitleItem(title = "Application")
            SettingsItem(
                title = "Notifications",
                description = "Enable/disable notifications",
                onClick = {
                    navController.navigate(Screen.NotificationScreen.route)
                },
                icon = R.drawable.notification_ic
            )

            Spacer(modifier = Modifier.size(12.dp))

            SettingsItem(
                title = "Theme",
                description = "Enable light/dark mode",
                onClick = onThemeUpdated,
                icon = R.drawable.moon_ic,
                rightArrowEnabled = false
            ) {
                CustomSwitcher(
                    size = 40.dp,
                    padding = 5.dp,
                    switch = darkTheme,
                    firstIcon = "☀\uFE0F",
                    secondIcon = "\uD83C\uDF19"
                )
            }
            Spacer(modifier = Modifier.size(12.dp))

            SettingsItem(
                title = "Contact",
                description = "Contact with us",
                onClick = {
                    context.sendMail()
                },
                icon = R.drawable.contact_ic
            )
            SettingsDivider()

            TitleItem(title = "Account")
            SettingsItem(
                title = "Log out",
                description = "Log out of the application",
                onClick = {
                    settingsViewModel.logout {
                        navController.navigate(Screen.SignInScreen.route) {
                            popUpTo(0)
                        }
//                        settingsViewModel.updateOnlineStatus(online = false)
                    }
                },
                icon = R.drawable.logout_ic
            )
            Spacer(modifier = Modifier.size(12.dp))
            SettingsItem(
                title = "Delete account",
                description = "Delete your account permanently",
                onClick = {
                    settingsViewModel.onDeleteAccountAlertChange()
                },
                icon = R.drawable.delete_ic
            )
        }
    }
    DeleteAccountAlert(
        alertState = deleteAccountState.alert,
        countdown = deleteAccountState.countdown,
        enabled = deleteAccountState.enabled,
        loading = deleteAccountState.loading,
        onDelete = {
            settingsViewModel.deleteAccount {
                navController.navigate(Screen.SignInScreen.route) {
                    popUpTo(0)
                }
            }
        },
        onDismiss = {
            settingsViewModel.onDeleteAccountAlertChange()
        }
    )

}




