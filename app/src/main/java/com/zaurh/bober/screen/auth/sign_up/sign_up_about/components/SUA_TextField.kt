package com.zaurh.bober.screen.auth.sign_up.sign_up_about.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun SUA_TextField(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    title: String,
    icon: Int,
    trailingIcon: @Composable () -> Unit = {},
    placeholder: String,
    visualTransformation: VisualTransformation,
    keyboardType: KeyboardType,
    value: String,
    onValueChange: (String) -> Unit = {},
    onClick: () -> Unit = {}
) {
    val focus = LocalFocusManager.current
    Column() {
        Text(text = title, color = Color.White, fontFamily = outfitFamily)
        Spacer(modifier = Modifier.size(8.dp))
        TextField(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30))
                .clickable {
                    focus.clearFocus()
                    onClick()
                },
            colors = TextFieldDefaults.colors(
                focusedTextColor = colorResource(id = R.color.darkBlue),
                unfocusedTextColor = colorResource(id = R.color.darkBlue),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                disabledTextColor = colorResource(id = R.color.darkBlue),
                disabledPlaceholderColor = colorResource(id = R.color.darkBlue),
                cursorColor = colorResource(id = R.color.darkBlue),
                selectionColors = TextSelectionColors(
                    handleColor = colorResource(id = R.color.backgroundTop),
                    backgroundColor = colorResource(id = R.color.backgroundTop)
                ),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            enabled = enabled,
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = icon),
                    contentDescription = "",
                    tint = colorResource(id = R.color.darkBlue)
                )
            },
            trailingIcon = {
                trailingIcon()
            },
            singleLine = true,
            placeholder = { Text(text = placeholder, fontFamily = outfitFamily) },
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            textStyle = TextStyle.Default.copy(
                fontFamily = outfitFamily,
                fontSize = 16.sp
            )
        )
    }
}

