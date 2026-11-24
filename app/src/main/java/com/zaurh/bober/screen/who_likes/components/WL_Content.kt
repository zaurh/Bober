package com.zaurh.bober.screen.who_likes.components

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
import com.zaurh.bober.screen.who_likes.WhoLikesUserItem
import com.zaurh.bober.screen.who_likes.WhoLikesViewModel
import com.zaurh.bober.ui.components.EmptyState

@Composable
fun WL_Content(
    whoLikesViewModel: WhoLikesViewModel,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val likes = whoLikesViewModel.whoLikesState.collectAsState().value.filterNotNull()
    val query = whoLikesViewModel.searchText.value

    BackHandler {
        if (query.isNotEmpty()) whoLikesViewModel.onSearch("") else navController.popBackStack()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (likes.isEmpty()) {
            EmptyState(
                title = "No likes yet",
                message = "When someone likes you, they'll appear here. Like them back to match!",
                animation = R.raw.empty_animation
            )
            return@Column
        }

        val filtered = likes.filter { it.username.contains(query, ignoreCase = true) }
        SearchBar(
            value = query,
            onValueChange = whoLikesViewModel::onSearch,
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
                items(filtered, key = { it.id }) { like ->
                    WhoLikesUserItem(whoLikesData = like) {
                        navController.navigate(Screen.ProfileScreen.passUsername(like.username))
                    }
                }
            }
        }
    }
}
