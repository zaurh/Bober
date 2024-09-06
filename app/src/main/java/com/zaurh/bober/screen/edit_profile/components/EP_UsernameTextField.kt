package com.zaurh.bober.screen.edit_profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel

@Composable
fun EP_UsernameTextField(
    editProfileViewModel: EditProfileViewModel
) {
    Column {
        Text(text = "Username", color = MaterialTheme.colorScheme.primary)
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20))
                .clickable {
                    editProfileViewModel.onUsernameChangeState()
                },
            colors = TextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                disabledTextColor = MaterialTheme.colorScheme.primary,
                disabledIndicatorColor = Color.Transparent
            ),
            enabled = false,
            value = editProfileViewModel.usernameState.value.text,
            onValueChange = {}
        )
    }
}