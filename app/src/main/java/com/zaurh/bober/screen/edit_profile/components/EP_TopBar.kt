package com.zaurh.bober.screen.edit_profile.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel
import com.zaurh.bober.ui.components.BackTopBar

/** Back asks before dropping unsaved changes ([onBack] decides); Save stores everything and closes. */
@Composable
fun EP_TopBar(editProfileViewModel: EditProfileViewModel, onBack: () -> Unit, onSaved: () -> Unit) {
    val focus = LocalFocusManager.current
    val saving = editProfileViewModel.loadingState.value
    BackTopBar(title = "Edit profile", onBack = onBack) {
        TextButton(
            onClick = {
                focus.clearFocus()
                editProfileViewModel.updateUserData(onSuccess = onSaved)
            },
            enabled = !saving,
            modifier = Modifier.padding(end = 4.dp)
        ) {
            if (saving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
            else Text("Save", style = MaterialTheme.typography.labelLarge)
        }
    }
}
