package com.zaurh.bober.screen.blocked_users.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.data.responses.BlockedUserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.home.components.SearchBar
import com.zaurh.bober.ui.components.Avatar
import com.zaurh.bober.ui.components.EmptyState

@Composable
fun BU_Content(
    blockedUsersViewModel: BlockedUsersViewModel,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val blocked = blockedUsersViewModel.blockedUserList.collectAsState().value.filterNotNull()
    val query = blockedUsersViewModel.searchText.value
    BackHandler {
        if (query.isNotEmpty()) blockedUsersViewModel.onSearch("") else navController.popBackStack()
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (blocked.isEmpty()) {
            EmptyState(
                title = "No one blocked",
                message = "People you block can't see you or message you. They'll be listed here.",
                animation = R.raw.empty_animation
            )
        } else {
            val filtered = blocked.filter {
                it.username.orEmpty().contains(query, ignoreCase = true) || it.name.orEmpty().contains(query, ignoreCase = true)
            }
            SearchBar(
                value = query,
                onValueChange = blockedUsersViewModel::onSearch,
                placeHolder = "Search",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (filtered.isEmpty()) {
                EmptyState(title = "No one found", message = "Try a different name.", animation = R.raw.empty_animation)
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(filtered, key = { it.id.orEmpty() }) { person ->
                        BlockedRow(
                            person = person,
                            onOpen = { navController.navigate(Screen.ProfileScreen.passUsername(person.username)) },
                            onUnblock = { blockedUsersViewModel.unBlockUser(recipientId = person.id.orEmpty()) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockedRow(person: BlockedUserData, onOpen: () -> Unit, onUnblock: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(url = person.image, size = 52.dp)
        Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                person.name?.ifBlank { null } ?: person.username.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("@${person.username.orEmpty()}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        OutlinedButton(onClick = onUnblock) { Text("Unblock") }
    }
}
