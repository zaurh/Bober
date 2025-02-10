package com.zaurh.bober.screen.settings.account_center.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.zaurh.bober.ui.theme.outfitFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AC_TopBar(
    navController: NavController
) {
    TopAppBar(title = {
        Text(text = "Account center", fontFamily = outfitFamily)
    }, navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }
    })
}