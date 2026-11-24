package com.zaurh.bober.screen.settings.components

import com.zaurh.bober.ui.components.SettingsSwitchRow
import com.zaurh.bober.ui.components.SettingsRow
import com.zaurh.bober.ui.components.SettingsGroup
import com.zaurh.bober.ui.components.SettingsDivider
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.bober.BuildConfig
import com.zaurh.bober.R
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.password.ChangePasswordDialog
import com.zaurh.bober.screen.password.PasswordViewModel
import com.zaurh.bober.screen.settings.SettingsViewModel
import com.zaurh.bober.screen.settings.email.EmailVerificationDialog
import com.zaurh.bober.screen.settings.email.EmailVerificationViewModel
import com.zaurh.bober.util.sendMail

@Composable
fun SettingsContent(
    paddingValues: PaddingValues,
    navController: NavController,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit,
    settingsViewModel: SettingsViewModel,
    emailVerificationViewModel: EmailVerificationViewModel = hiltViewModel(),
    passwordViewModel: PasswordViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val user = settingsViewModel.userDataState.collectAsState().value
    val deleteAccountState = settingsViewModel.deleteAccountState.value
    val error = settingsViewModel.error.value
    LaunchedEffect(error) {
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            settingsViewModel.errorShown()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(bottom = 24.dp)
    ) {
        SettingsGroup("Discovery") {
            AgeRangeFilter(
                start = user?.ageRangeStart ?: MIN_AGE,
                end = user?.ageRangeEnd ?: MAX_AGE,
                onChange = settingsViewModel::updateAgeRange
            )
            DistanceFilter(
                distance = user?.maximumDistance ?: MAX_DISTANCE_KM,
                onChange = settingsViewModel::updateMaximumDistance
            )
            SettingsSwitchRow(
                icon = R.drawable.location_ic,
                title = "Expand distance",
                subtitle = "Show people further away when no one is nearby",
                checked = user?.showFullDistance ?: true,
                enabled = user != null,
                onCheckedChange = settingsViewModel::showFullDistance
            )
            SettingsDivider()
            SettingsRow(
                icon = R.drawable.bober_ic,
                title = "Show me",
                subtitle = (user?.showMe ?: ShowMe.EVERYONE).displayName,
                onClick = { settingsViewModel.onShowMeStateChange(true) }
            )
        }

        SettingsGroup("App") {
            SettingsRow(
                icon = R.drawable.notification_ic,
                title = "Notifications",
                subtitle = "Messages, likes and matches",
                onClick = { navController.navigate(Screen.NotificationScreen.route) }
            )
            SettingsDivider()
            SettingsSwitchRow(
                icon = R.drawable.moon_ic,
                title = "Dark theme",
                checked = darkTheme,
                onCheckedChange = { if (it != darkTheme) onThemeUpdated() }
            )
        }

        SettingsGroup("People") {
            SettingsRow(
                icon = R.drawable.liked_users_ic,
                title = "Liked users",
                subtitle = "People you liked",
                onClick = { navController.navigate(Screen.LikedUsersScreen.route) }
            )
            SettingsDivider()
            SettingsRow(
                icon = R.drawable.block_ic,
                title = "Blocked users",
                subtitle = "People you blocked",
                onClick = { navController.navigate(Screen.BlockedUsersScreen.route) }
            )
        }

        SettingsGroup("Account") {
            val verifiedEmail = user?.email?.takeIf { it.isNotEmpty() && user.emailIsVerified == true }
            SettingsRow(
                icon = R.drawable.contact_ic,
                title = "Email",
                subtitle = verifiedEmail?.let { "$it · Verified" } ?: "Add and verify your email",
                onClick = emailVerificationViewModel::openDialog
            )
            // Google accounts have no password of their own.
            if (user != null && user.googleId.isNullOrEmpty()) {
                SettingsDivider()
                SettingsRow(
                    icon = R.drawable.password_ic,
                    title = "Change password",
                    onClick = passwordViewModel::openChange
                )
            }
        }

        SettingsGroup("Support") {
            SettingsRow(
                icon = R.drawable.warning_ic,
                title = "Contact us",
                subtitle = "Questions, feedback or a problem",
                onClick = { context.sendMail() }
            )
        }

        SettingsGroup(null, Modifier.padding(top = 20.dp)) {
            SettingsRow(
                icon = R.drawable.logout_ic,
                title = "Log out",
                onClick = {
                    settingsViewModel.logout {
                        navController.navigate(Screen.SignInScreen.route) { popUpTo(0) }
                    }
                }
            )
            SettingsDivider()
            SettingsRow(
                icon = R.drawable.delete_ic,
                title = "Delete account",
                destructive = true,
                onClick = settingsViewModel::onDeleteAccountAlertChange
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Bober ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

    ChangePasswordDialog(viewModel = passwordViewModel)
    EmailVerificationDialog(
        state = emailVerificationViewModel.dialog.value,
        verifiedEmail = user?.email?.takeIf { it.isNotEmpty() && user.emailIsVerified == true },
        onEmailChange = emailVerificationViewModel::onEmailChange,
        onSend = emailVerificationViewModel::sendVerification,
        onDismiss = emailVerificationViewModel::closeDialog
    )
    DeleteAccountAlert(
        alertState = deleteAccountState.alert,
        countdown = deleteAccountState.countdown,
        enabled = deleteAccountState.enabled,
        loading = deleteAccountState.loading,
        onDelete = {
            settingsViewModel.deleteAccount {
                navController.navigate(Screen.SignInScreen.route) { popUpTo(0) }
            }
        },
        onDismiss = settingsViewModel::onDeleteAccountAlertChange
    )
}
