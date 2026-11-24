package com.zaurh.bober.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.ui.theme.BoberPalette

/**
 * Filled, rounded text field. [onGradient] switches to white fields with navy text for the auth screens,
 * where the background is the brand gradient in both light and dark mode.
 */
@Composable
fun BoberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    error: String? = null,
    onGradient: Boolean = false,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val container = if (onGradient) Color.White else MaterialTheme.colorScheme.surfaceContainerHigh
    val text = if (onGradient) BoberPalette.Navy else MaterialTheme.colorScheme.onSurface
    val hint = if (onGradient) BoberPalette.Navy.copy(alpha = 0.45f) else MaterialTheme.colorScheme.onSurfaceVariant
    val accent = if (onGradient) BoberPalette.PinkStrong else MaterialTheme.colorScheme.primary

    Column(modifier) {
        if (label != null) {
            Text(
                label,
                style = MaterialTheme.typography.titleSmall,
                color = if (onGradient) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            shape = MaterialTheme.shapes.medium,
            placeholder = { Text(placeholder) },
            leadingIcon = leading,
            trailingIcon = trailing,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = container,
                unfocusedContainerColor = container,
                disabledContainerColor = container,
                errorContainerColor = container,
                focusedTextColor = text,
                unfocusedTextColor = text,
                disabledTextColor = text,
                errorTextColor = text,
                focusedPlaceholderColor = hint,
                unfocusedPlaceholderColor = hint,
                disabledPlaceholderColor = hint,
                focusedLeadingIconColor = hint,
                unfocusedLeadingIconColor = hint,
                disabledLeadingIconColor = hint,
                focusedTrailingIconColor = hint,
                unfocusedTrailingIconColor = hint,
                disabledTrailingIconColor = hint,
                cursorColor = accent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                selectionColors = TextSelectionColors(handleColor = accent, backgroundColor = accent.copy(alpha = 0.3f)),
            ),
        )
        AnimatedVisibility(visible = error != null) {
            FieldMessage(error.orEmpty(), onGradient = onGradient)
        }
    }
}

/** Error line under a field. On the gradient it's white on a translucent pill, so it stays readable. */
@Composable
fun FieldMessage(message: String, onGradient: Boolean = false, modifier: Modifier = Modifier) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = if (onGradient) Color.White else MaterialTheme.colorScheme.error,
        modifier = modifier.padding(start = 6.dp, top = 6.dp)
    )
}

/** Password field with a show/hide toggle. */
@Composable
fun BoberPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Password",
    label: String? = null,
    error: String? = null,
    onGradient: Boolean = false,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var visible by remember { mutableStateOf(false) }
    BoberTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        label = label,
        error = error,
        onGradient = onGradient,
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        leading = { Icon(painterResource(R.drawable.password_ic), null, Modifier.size(22.dp)) },
        trailing = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    painterResource(if (visible) R.drawable.visibilityon_ic else R.drawable.visibilityoff_ic),
                    contentDescription = if (visible) "Hide password" else "Show password",
                    modifier = Modifier.size(22.dp)
                )
            }
        },
    )
}
