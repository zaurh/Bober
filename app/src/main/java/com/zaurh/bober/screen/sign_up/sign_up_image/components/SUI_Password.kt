package com.zaurh.bober.screen.sign_up.sign_up_image.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.screen.sign_up.sign_up_about.components.SUA_TextField

@Composable
fun SUI_Password(
    signUpViewModel: SignUpViewModel
) {
    val passwordState = signUpViewModel.passwordState.value

    SUA_TextField(
        title = "Create a password",
        enabled = true,
        icon = R.drawable.password_ic,
        placeholder = "Enter a new password",
        value = passwordState.password,
        onValueChange = signUpViewModel::onPasswordChange,
        trailingIcon = {
            val image = if (passwordState.passwordVisibility)
                painterResource(id = R.drawable.visibilityon_ic)
            else painterResource(id = R.drawable.visibilityoff_ic)

            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = signUpViewModel::onPasswordVisibilityChange
            ) {
                Icon(painter = image, contentDescription = "")
            }
        },
        keyboardType = KeyboardType.Password,
        visualTransformation = if (passwordState.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
    )
}