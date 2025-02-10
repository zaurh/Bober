package com.zaurh.bober.screen.sign_up.sign_up_image.image_edit

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.components.EditScreenContent
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.components.EditScreenTopBar

@Composable
fun ImageEditScreen(
    modifier: Modifier = Modifier,
    imageEditViewModel: ImageEditViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val bitmap = imageEditViewModel.bitmapState.collectAsState()

    LaunchedEffect(true) {
        val cropped = bitmap.value.croppedBitmap
        if (cropped == null){
            Log.d("sdakljlaskdsad", "bitmap is null")
        }else{
            Log.d("sdakljlaskdsad", "bitmap is not null")
        }

    }

    BackHandler(
        onBack = {
            imageEditViewModel.resetEditing()
            navController.navigateUp()
        }
    )

    Scaffold(
        topBar = {
            EditScreenTopBar(
                onClose = {
                    imageEditViewModel.resetEditing()
                    navController.navigateUp()
                },
                onArrowBack = {
                    imageEditViewModel.rotateBitmap()
                },
                onDone = {
                    navController.navigateUp()
                    imageEditViewModel.doneEditing(context = context)
//                    imageEditViewModel.resetEditing()
                },
                onCrop = {
                    val bitmap = imageEditViewModel.bitmapState.value
                    bitmap.initialBitmap?.let {
                        imageEditViewModel.updateImageState(it)
                        navController.navigate(Screen.CropImage.route)
                    }
                }
            )
        },
        content = { padding ->
            EditScreenContent(
                modifier = modifier.padding(padding),
                imageEditViewModel = imageEditViewModel
            )
        }
    )
}