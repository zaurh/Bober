package com.zaurh.bober.screen.who_likes.components

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
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.home.components.SearchBar
import com.zaurh.bober.screen.who_likes.WhoLikesUserItem
import com.zaurh.bober.screen.who_likes.WhoLikesViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WL_Content(
    whoLikesViewModel: WhoLikesViewModel,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val userData = whoLikesViewModel.userDataState.collectAsState()
    val userList = whoLikesViewModel.userListDataState.collectAsState()
    val searchQuery = whoLikesViewModel.searchText.value

    BackHandler(
        onBack = {
            if (searchQuery.isNotEmpty()){
                whoLikesViewModel.onSearch("")
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
            val matchedUserIdList = userData.value?.matchList?.map { it.matchUserId } ?: listOf()
            val whoLikesUserId = userData.value?.gotLiked ?: listOf()

            val filteredWhoLikesUserId = whoLikesUserId.filterNot { it in matchedUserIdList }

            val whoLikesUserList = userList.value.filter {
                it?.id in filteredWhoLikesUserId
            }
            val filteredWhoLikes = whoLikesUserList.filter {
                it?.username?.contains(searchQuery) == true
            }

            if (whoLikesUserList.isNotEmpty()){
                SearchBar(
                    value = searchQuery,
                    onValueChange = whoLikesViewModel::onSearch,
                    placeHolder = "Search"
                )
                Spacer(modifier = Modifier.size(16.dp))

                if (filteredWhoLikes.isNotEmpty()){
                    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                        items(filteredWhoLikes) { userData ->
                            WhoLikesUserItem(userData = userData ?: UserData()) {
                                val whoLikedUsername = userData?.username ?: ""
                                navController.navigate(Screen.ProfileScreen.passUsername(whoLikedUsername))
                            }
                        }
                    }
                }else{
                    Text(text = "No one is found.")
                }
                
            }else{
                Text(text = "No one liked you yet :(")
            }
            
        }
    }

}