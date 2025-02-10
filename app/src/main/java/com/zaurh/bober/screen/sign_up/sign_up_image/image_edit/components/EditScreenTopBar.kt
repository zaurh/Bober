package com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreenTopBar(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onArrowBack: () -> Unit,
    onCrop: () -> Unit,
    onDone: () -> Unit
) {
    TopAppBar(
        title = {
            IconButton(onClick = {
                onClose()
            }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "")
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    onCrop()
                }) {
                    Icon(modifier = Modifier.size(20.dp),painter = painterResource(R.drawable.crop_ic), contentDescription = "")
                }
                IconButton(onClick = {
                    onArrowBack()
                }) {
                    Icon(modifier = Modifier.size(20.dp),painter = painterResource(R.drawable.rotate_ic), contentDescription = "")
                }
                IconButton(onClick = {
                    onDone()
                }) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "")
                }
            }

        }
    )
}