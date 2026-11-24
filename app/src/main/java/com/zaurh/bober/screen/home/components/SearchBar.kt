package com.zaurh.bober.screen.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.zaurh.bober.ui.components.BoberTextField

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeHolder: String,
    modifier: Modifier = Modifier,
) {
    BoberTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeHolder,
        modifier = modifier,
        imeAction = ImeAction.Search,
        leading = { Icon(Icons.Default.Search, contentDescription = null) },
        trailing = if (value.isNotEmpty()) {
            { IconButton(onClick = { onValueChange("") }) { Icon(Icons.Default.Close, contentDescription = "Clear") } }
        } else null,
    )
}
