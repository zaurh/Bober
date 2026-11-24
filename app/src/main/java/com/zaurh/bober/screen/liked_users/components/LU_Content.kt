package com.zaurh.bober.screen.liked_users.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.home.components.SearchBar
import com.zaurh.bober.screen.liked_users.LikedUsersViewModel
import com.zaurh.bober.ui.components.EmptyState
import com.zaurh.bober.ui.components.PersonCard

@Composable
fun LU_Content(
    likedUsersViewModel: LikedUsersViewModel,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val liked = likedUsersViewModel.likedUserList.collectAsState().value.filterNotNull()
    val query = likedUsersViewModel.searchText.value
    BackHandler {
        if (query.isNotEmpty()) likedUsersViewModel.onSearch("") else navController.popBackStack()
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (liked.isEmpty()) {
            EmptyState(
                title = "No likes sent yet",
                message = "People you like on Discover show up here until they like you back.",
                animation = R.raw.empty_animation
            )
        } else {
            val filtered = liked.filter {
                it.username.orEmpty().contains(query, ignoreCase = true)
            }
            SearchBar(
                value = query,
                onValueChange = likedUsersViewModel::onSearch,
                placeHolder = "Search",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (filtered.isEmpty()) {
                EmptyState(title = "No one found", message = "Try a different name.", animation = R.raw.empty_animation)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(filtered, key = { it.id.orEmpty() }) { person ->
                        PersonCard(
                            image = person.image,
                            title = listOfNotNull(person.username, person.age?.ifBlank { null }).joinToString(", "),
                            onClick = { navController.navigate(Screen.ProfileScreen.passUsername(person.username)) },
                        )
                    }
                }
            }
        }
    }
}
