package com.zaurh.bober.screen.blocked_users

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.responses.BlockedUserData
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.screen.blocked_users.state.BlockedUsersState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockedUsersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData

    private val _blockAlertState = mutableStateOf(false)
    val blockAlertState: State<Boolean> = _blockAlertState

    private val _blockedUsersState = mutableStateOf(BlockedUsersState())
    val blockedUsersState: State<BlockedUsersState> = _blockedUsersState

    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    fun getBlockList(){
        viewModelScope.launch(Dispatchers.IO) {
            _blockedUsersState.value = blockedUsersState.value.copy(
                loading = true,
                error = "",
                empty = false
            )
            val response = userRepository.getBlockedUsers()
            if (response.success){
                if (response.blockList.isNotEmpty()){
                    _blockedUsersState.value = blockedUsersState.value.copy(
                        loading = false,
                        blockList = response.blockList,
                        error = "",
                        empty = false
                    )
                }else{
                    _blockedUsersState.value = blockedUsersState.value.copy(
                        loading = false,
                        blockList = listOf(),
                        error = "",
                        empty = true
                    )
                }
            }else{
                _blockedUsersState.value = blockedUsersState.value.copy(
                    loading = false,
                    blockList = listOf(),
                    error = response.message,
                    empty = false
                )
            }
        }
    }

    fun onSearch(value: String) {
        _searchText.value = value
    }

    fun unBlockUser(recipientId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.unblock(recipientId)
            if(response.success){
                val mutableBlockList = blockedUsersState.value.blockList.toMutableList()
                mutableBlockList.removeIf { it.id == recipientId }
                _blockedUsersState.value = blockedUsersState.value.copy(
                    blockList = mutableBlockList
                )

//                getBlockList()
            }

        }
    }

    fun onBlockStateChange() {
        _blockAlertState.value = !blockAlertState.value
    }

    fun blockUser(recipientId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.block(recipientId)

            if (response.success) {
                val mutableBlockList = blockedUsersState.value.blockList.toMutableList()
                mutableBlockList.add(
                    BlockedUserData(
                        id = recipientId
                    )
                )
                _blockedUsersState.value = blockedUsersState.value.copy(
                    blockList = mutableBlockList
                )
                _blockAlertState.value = false
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            }

        }
    }

}