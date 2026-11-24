@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.bober.screen.chat.modal_sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.profile.ProfileDialog
import com.zaurh.bober.screen.profile.ProfileViewModel

/** The chat's ⋮ menu: background, profile, and the relationship actions. */
@Composable
fun Chat_ModalSheet(
    chatScreenViewModel: ChatScreenViewModel,
    onViewProfile: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    if (!chatScreenViewModel.modalSheetState.value) return
    fun pick(action: () -> Unit) {
        chatScreenViewModel.closeModalSheet()
        action()
    }
    ModalBottomSheet(
        onDismissRequest = chatScreenViewModel::closeModalSheet,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(Modifier.navigationBarsPadding().padding(bottom = 16.dp)) {
            SheetItem(Icons.Default.Person, "View profile") { pick(onViewProfile) }
            SheetItem(Icons.Default.Edit, "Chat background") { chatScreenViewModel.openBackgroundSheet() }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SheetItem(Icons.Default.Clear, "Unmatch", destructive = true) { pick { profileViewModel.openDialog(ProfileDialog.Unmatch) } }
            SheetItem(Icons.Default.Warning, "Report", destructive = true) { pick { profileViewModel.openDialog(ProfileDialog.Report) } }
            SheetItem(Icons.Default.Lock, "Block", destructive = true) { pick { profileViewModel.openDialog(ProfileDialog.Block) } }
        }
    }
}

@Composable
private fun SheetItem(icon: ImageVector, title: String, destructive: Boolean = false, onClick: () -> Unit) {
    val color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    ListItem(
        headlineContent = { Text(title, color = color) },
        leadingContent = { Icon(icon, contentDescription = null, tint = color) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick)
    )
}
