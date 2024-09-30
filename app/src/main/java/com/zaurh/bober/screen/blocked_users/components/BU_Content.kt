package com.zaurh.bober.screen.blocked_users.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.blocked_users.BlockedUserItem
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.home.components.SearchBar

@Composable
fun BU_Content(
    blockedUsersViewModel: BlockedUsersViewModel,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val userDataState = blockedUsersViewModel.userDataState.collectAsState()
    val blockedUserList = blockedUsersViewModel.blockedUserList.collectAsState()
    val searchQuery = blockedUsersViewModel.searchText.value



    BackHandler(
        onBack = {
            if (searchQuery.isNotEmpty()) {
                blockedUsersViewModel.onSearch("")
            } else {
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
            val blockList = blockedUserList.value


            if (blockList.isNotEmpty()) {
                val searchedBlockedUsers = blockList.filter {
                    it?.username?.contains(searchQuery) == true
                }
                SearchBar(
                    value = searchQuery,
                    onValueChange = blockedUsersViewModel::onSearch,
                    placeHolder = "Search for blocked users"
                )
                Spacer(modifier = Modifier.size(16.dp))
                if (searchedBlockedUsers.isNotEmpty()) {
                    LazyColumn {
                        items(searchedBlockedUsers) { profileData ->
                            profileData?.let {
                                val username = profileData.username
                                val profileId = profileData.id ?: ""

                                BlockedUserItem(
                                    blockedUserData = profileData,
                                    onItemClick = {
                                        navController.navigate(
                                            Screen.ProfileScreen.passUsername(
                                                username
                                            )
                                        )
                                    }) {
                                    blockedUsersViewModel.unBlockUser(recipientId = profileId)
                                }
                            }

                        }
                    }
                } else {
                    Text(text = "No one is found.")
                }

            } else {
                Text(text = "Your block list is empty.")
            }

        }
    }

}