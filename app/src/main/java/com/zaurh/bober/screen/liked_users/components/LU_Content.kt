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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.home.components.SearchBar
import com.zaurh.bober.screen.liked_users.LikedUserItem
import com.zaurh.bober.screen.liked_users.LikedUsersViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LU_Content(
    likedUsersViewModel: LikedUsersViewModel,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val userData = likedUsersViewModel.userDataState.collectAsState()
    val likedUserList = likedUsersViewModel.likedUserList.collectAsState()
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

            if (likedUserList.value.isNotEmpty()){
                val filteredLikedUsers = likedUserList.value.filter {
                    it?.username?.contains(searchQuery) == true
                }
                SearchBar(
                    value = searchQuery,
                    onValueChange = likedUsersViewModel::onSearch,
                    placeHolder = "Search for liked users"
                )
                Spacer(modifier = Modifier.size(16.dp))
                if (filteredLikedUsers.isNotEmpty()){
                    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                        items(filteredLikedUsers) { likedUserData ->
                            likedUserData?.let {
                                LikedUserItem(likedUserData = it) {
                                    navController.navigate(Screen.ProfileScreen.passUsername(it.username))
                                }
                            }

                        }
                    }
                }else{
                    Text(text = "No one is found.")
                }

            }else{
                Text(text = "You've not liked anyone.")
            }

        }
    }

}