package com.zaurh.bober.screen.sign_up

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.ui.theme.BoberPalette

/** A large tappable option on the gradient (gender, "show me"). White when selected. */
@Composable
fun GradientOptionCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Int? = null,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (selected) Color.White else Color.White.copy(alpha = 0.16f),
        contentColor = if (selected) BoberPalette.Navy else Color.White,
        border = if (selected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
    ) {
        Row(Modifier.padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    painterResource(icon), null,
                    tint = if (selected) BoberPalette.PinkStrong else Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.size(16.dp))
            }
            Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            if (selected) Icon(Icons.Default.CheckCircle, null, tint = BoberPalette.PinkStrong)
        }
    }
}

/** A selectable pill on the gradient (interests). */
@Composable
fun GradientChip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = if (selected) Color.White else Color.White.copy(alpha = 0.16f),
        contentColor = if (selected) BoberPalette.Navy else Color.White,
        border = if (selected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp))
    }
}
