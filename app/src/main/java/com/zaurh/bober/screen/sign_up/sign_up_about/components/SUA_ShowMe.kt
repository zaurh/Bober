package com.zaurh.bober.screen.sign_up.sign_up_about.components

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.zaurh.bober.R
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.screen.sign_up.SignUpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SUA_ShowMe(
    signUpViewModel: SignUpViewModel
) {
    val options = listOf(ShowMe.MAN, ShowMe.WOMAN, ShowMe.EVERYONE)
    val interestedInState = signUpViewModel.showMeState.value
    val interestedIn = signUpViewModel.showMeState.value

    ExposedDropdownMenuBox(
        expanded = interestedInState.showMeDropdown,
        onExpandedChange = { signUpViewModel.onShowMeChange() },
    ) {
        val selectedInterested = interestedIn.showMe.displayName
        SUA_TextField(modifier = Modifier.menuAnchor(),
            title = "Who are you interested in?",
            icon = R.drawable.profile_ic,
            trailingIcon = {
                Icon(
                    modifier = Modifier.rotate(if (interestedInState.showMeDropdown) 180f else 0f),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "",
                    tint = colorResource(id = R.color.darkBlue)
                )
            },
            placeholder = "",
            value = selectedInterested,
            enabled = false,
            keyboardType = KeyboardType.Text,
            visualTransformation = VisualTransformation.None
        )
        ExposedDropdownMenu(
            modifier = Modifier.background(colorResource(id = R.color.backgroundTop)),
            expanded = signUpViewModel.showMeState.value.showMeDropdown,
            onDismissRequest = {
                signUpViewModel.onShowMeChange()
            },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption.displayName, color = Color.White) },
                    onClick = {
                        signUpViewModel.selectShowMe(selectionOption)
                        signUpViewModel.onShowMeChange()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}