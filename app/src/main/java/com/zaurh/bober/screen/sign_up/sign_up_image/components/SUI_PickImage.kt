@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.bober.screen.sign_up.sign_up_image.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.ImageEditViewModel
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun SUI_PickImage(
    imageEditViewModel: ImageEditViewModel,
    signUpViewModel: SignUpViewModel,
    onImageSelected: (String) -> Unit
) {
    val bitmapState = imageEditViewModel.bitmapState.collectAsState()
    val loadingImage = imageEditViewModel.imageUrlState.collectAsState()
    val errorMessage = signUpViewModel.imageState.value.errorMessage
    val bottomSheetEnabled = signUpViewModel.imageState.value.bottomSheetEnabled
    val context = LocalContext.current
    val focus = LocalFocusManager.current



    val externalStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
    val isPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        externalStoragePermission
    ) == PackageManager.PERMISSION_GRANTED


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Log.e("Permission", "Permission denied for reading videos")
        } else {
            signUpViewModel.bottomSheetTrigger()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            signUpViewModel.selectImage(it.toString()) {
                onImageSelected(it.toString())
            }
        }
    }

    Column {
        Text(text = "Pick a profile picture", color = Color.White, fontFamily = outfitFamily)
        Spacer(modifier = Modifier.size(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .height(200.dp)
                    .width(120.dp)
                    .clip(
                        RoundedCornerShape(10)
                    )
                    .background(Color.White)
                    .let {
                        if (!loadingImage.value.loading) {
                            it.clickable {
                                focus.clearFocus()
                                if (isPermissionGranted) {
                                    signUpViewModel.bottomSheetTrigger()
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                    }else{
                                        launcher.launch("image/*")
                                    }
                                }
                            }
                        } else {
                            it
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                if (bitmapState.value.finalBitmap != null) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = bitmapState.value.finalBitmap,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                } else if (loadingImage.value.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = colorResource(id = R.color.backgroundBottom)
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = "",
                        tint = colorResource(id = R.color.darkBlue)
                    )
                }


            }


        }

        AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
            Row {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.warning_ic),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = errorMessage, color = Color.White)
            }
        }
    }

    if (bottomSheetEnabled) {
        ModalBottomSheet(
            onDismissRequest = signUpViewModel::bottomSheetTrigger
        ) {
            Box(Modifier.fillMaxSize()) {
                Column {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxWidth(),
                        columns = GridCells.Fixed(3)
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val mediaList = getMediaFromGallery(context)
                            items(mediaList) { uri ->
                                ImageThumbnail(uri) {
                                    onImageSelected(it)

//                                    signUpViewModel.uploadMedia(
//                                        description = "",
//                                        imageUri = Uri.parse(uri),
//                                        contentResolver = context.contentResolver,
//                                        context = context,
//                                        onSuccess = {
//                                            signUpViewModel.selectImage(it) {
//                                                onImageSelected(it)
//                                            }
//                                        }
//                                    )
                                }
                            }
                        }

                    }
                }

                FloatingActionButton(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 48.dp, end = 36.dp),
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    onClick = {
                        launcher.launch("image/*")
                    }) {
                    Icon(
                        painter = painterResource(R.drawable.folder_ic),
                        contentDescription = "folder",
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }


}