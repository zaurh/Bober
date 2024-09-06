package com.zaurh.bober.screen.edit_profile.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EP_TopBar(
    navController: NavController,
    editProfileViewModel: EditProfileViewModel
) {
    val focus = LocalFocusManager.current
    val isLoading = editProfileViewModel.loadingState.value

    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Edit profile")
            Spacer(modifier = Modifier.size(8.dp))
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

    }, navigationIcon = {
        IconButton(onClick = {
            navController.popBackStack()
        }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }
    }, actions = {
        IconButton(onClick = {
            editProfileViewModel.updateUserData(onSuccess = {
                focus.clearFocus()
                navController.popBackStack()
            })
        }) {
            Icon(imageVector = Icons.Default.Done, contentDescription = "")
        }
    })
}