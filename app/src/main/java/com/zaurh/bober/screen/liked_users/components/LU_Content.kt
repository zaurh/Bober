package com.zaurh.bober.screen.liked_users.components

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.home.components.SearchBar
import com.zaurh.bober.screen.liked_users.LikedUserItem
import com.zaurh.bober.screen.liked_users.LikedUsersViewModel
import com.zaurh.bober.ui.theme.outfitFamily

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LU_Content(
    likedUsersViewModel: LikedUsersViewModel,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val likedUserState = likedUsersViewModel.likedUsersState.value
    val searchQuery = likedUsersViewModel.searchText.value

    BackHandler(
        onBack = {
            if (searchQuery.isNotEmpty()){
                likedUsersViewModel.onSearch("")
            }else{
                navController.popBackStack()
            }
        }
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (likedUserState.likedUsers.isNotEmpty()){
                SearchBar(
                    value = searchQuery,
                    onValueChange = likedUsersViewModel::onSearch,
                    placeHolder = "Search for liked users"
                )
                Spacer(Modifier.size(16.dp))

                val filteredLikedUsers = likedUserState.likedUsers.filter {
                    it.username?.contains(searchQuery) == true
                }

                if (filteredLikedUsers.isNotEmpty()){
                    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                        items(filteredLikedUsers) { likedUserData ->
                            likedUserData.let {
                                LikedUserItem(likedUserData = it) {
                                    navController.navigate(Screen.ProfileScreen.createRoute(recipientId = it.id ?: ""))
                                }
                            }

                        }
                    }
                }else{
                    if (searchQuery.isNotEmpty()){
                        Text(text = "No one is found.", fontFamily = outfitFamily)
                    }
                }
            }
            else{
                Text(text = "You've not liked anyone.", fontFamily = outfitFamily)
            }

        }
    }

}