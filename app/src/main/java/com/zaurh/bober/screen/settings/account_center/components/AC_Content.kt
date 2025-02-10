package com.zaurh.bober.screen.settings.account_center.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.settings.components.SettingsItem
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun AC_Content(
    paddingValues: PaddingValues,
    navController: NavController
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsItem(
                title = "Email",
                description = "Verify email, change email",
                onClick = {
                    navController.navigate(Screen.EmailScreen.route)
                },
                icon = R.drawable.contact_ic
            )
            Spacer(modifier = Modifier.size(12.dp))

            SettingsItem(
                title = "Password",
                description = "Change password, reset password.",
                onClick = {
                    navController.navigate(Screen.PasswordScreen.route)
                },
                icon = R.drawable.lock_ic
            )
        }
    }


}


@Composable
fun AC_TextField(
    title: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = false,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation,
    enabled: Boolean = true,
    trailingIcon: @Composable () -> Unit = {}
) {
    Column {
        Text(text = title, color = MaterialTheme.colorScheme.primary, fontFamily = outfitFamily)
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20)),
            placeholder = { Text(text = placeholder, fontFamily = outfitFamily) },
            enabled = enabled,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSecondary,
                selectionColors = TextSelectionColors(
                    handleColor = MaterialTheme.colorScheme.onSecondary,
                    backgroundColor = MaterialTheme.colorScheme.onSecondary
                ),
            ),
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            textStyle = TextStyle(
                fontFamily = outfitFamily,
                fontSize = 16.sp
            )
        )
    }

}