package com.zaurh.bober.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaurh.bober.ui.theme.BoberPalette

enum class BoberButtonStyle {
    /** Pink filled button on normal surfaces. */
    Primary,

    /** White button for use on the brand gradient. */
    OnGradient,
}

/** The app's main call-to-action button: full width, pill-shaped, with a built-in loading state. */
@Composable
fun BoberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: BoberButtonStyle = BoberButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingText: String? = null,
) {
    val (container, content) = when (style) {
        BoberButtonStyle.Primary -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        BoberButtonStyle.OnGradient -> Color.White to BoberPalette.Navy
    }
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = container.copy(alpha = if (loading) 1f else 0.5f),
            disabledContentColor = content.copy(alpha = if (loading) 1f else 0.7f),
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = content, strokeWidth = 2.dp)
            if (loadingText != null) {
                Spacer(Modifier.size(10.dp))
                Text(loadingText, style = MaterialTheme.typography.labelLarge)
            }
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Round action button used for like / pass on the discover deck. */
@Composable
fun CircleActionButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    enabled: Boolean = true,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(size),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 6.dp,
        tonalElevation = 0.dp,
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(size * 0.45f))
        }
    }
}
