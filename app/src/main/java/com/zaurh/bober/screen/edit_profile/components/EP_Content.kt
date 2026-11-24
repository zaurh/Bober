package com.zaurh.bober.screen.edit_profile.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.ImageThumbnail
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.getMediaFromGallery
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.image_edit.ImageEditViewModel
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel
import com.zaurh.bober.ui.theme.outfitFamily
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EP_Content(
    navController: NavController,
    padding: PaddingValues,
    editProfileViewModel: EditProfileViewModel,
    imageEditViewModel: ImageEditViewModel
) {
    val userDataState = editProfileViewModel.userDataState.collectAsState()
    val context = LocalContext.current
    val dataImage = editProfileViewModel.images
    val images = dataImage.value
    val focus = LocalFocusManager.current
    val bitmap = imageEditViewModel.bitmapState.collectAsState()
    val imageUrlState = imageEditViewModel.imageUrlState.collectAsState()

    val bottomSheetEnabled = editProfileViewModel.bottomSheetEnabled.value

    BackHandler(
        onBack = {
            if (editProfileViewModel.discardChanges.value) {
                editProfileViewModel.onDiscardChangesAlert()
            } else {
                navController.navigateUp()
                editProfileViewModel.onDiscardChanges(false)
            }
        }
    )
    DiscardChangesAlert(
        alertState = editProfileViewModel.discardChangesAlert.value,
        onDismiss = editProfileViewModel::onDiscardChangesAlert
    ) {
        focus.clearFocus()
        editProfileViewModel.onDiscardChangesAlert()
        navController.popBackStack()
    }

    LaunchedEffect(key1 = userDataState.value?.imageUrl) {
        editProfileViewModel.onImageChange(imageList = userDataState.value?.imageUrl ?: listOf())
    }


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
            editProfileViewModel.bottomSheetTrigger()
        }
    }

    LaunchedEffect(bitmap.value.finalBitmap) {
        bitmap.value.finalBitmap?.let {
            imageUrlState.value.imageUrl?.let {
                editProfileViewModel.updateImage(
                    fileLink = it
                ) {
                    imageEditViewModel.resetEditing(true)
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            editProfileViewModel.selectImage(it.toString()) {
                imageEditViewModel.uriToBitmap(context, it.toString())
                navController.navigate(Screen.EditImage.route)
            }
        }
    }

    if (bottomSheetEnabled) {
        ModalBottomSheet(
            onDismissRequest = editProfileViewModel::bottomSheetTrigger
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
                                    editProfileViewModel.selectImage(uri) {
                                        imageEditViewModel.uriToBitmap(context, uri)
                                        navController.navigate(Screen.EditImage.route)
                                    }
                                }
                            }
                        }
                    }
                }

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 48.dp, end = 36.dp),
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




    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp)
        ) {
            userDataState.value?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Images (${images.size}/6)",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = outfitFamily
                    )
                    if (images.size < 6) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (imageUrlState.value.loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Spacer(modifier = Modifier.size(4.dp))
                            Button(
                                enabled = !imageUrlState.value.loading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                onClick = {
                                    focus.clearFocus()
                                    if (isPermissionGranted) {
                                        editProfileViewModel.bottomSheetTrigger()
                                    } else {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                        } else {
                                            launcher.launch("image/*")
                                        }
                                    }

                                }) {
                                Text(
                                    text = if (imageUrlState.value.loading) "Uploading..." else "+ Add new image",
                                    color = Color.White,
                                    fontFamily = outfitFamily,
                                    fontSize = 16.sp
                                )
                            }
                        }

                    }

                }

                val state =
                    rememberReorderableLazyGridState(
                        onMove = { from, to ->
                            dataImage.value = dataImage.value.toMutableList().apply {
                                add(to.index, removeAt(from.index))
                            }
                            editProfileViewModel.onDiscardChanges(true)
                        })
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    state = state.gridState,
                    modifier = Modifier
                        .height(if (images.size > 3) 400.dp else 200.dp)
                        .reorderable(state)
                ) {
                    items(images, { it }) { item ->
                        ReorderableItem(
                            state = state,
                            key = item,
                            defaultDraggingModifier = Modifier
                        ) { isDragging ->
                            EP_MediaItem(
                                modifier = Modifier.detectReorderAfterLongPress(state),
                                image = item,
                                isDragging = isDragging,
                                onClick = {
                                    editProfileViewModel.onDeleteImageState(selectedImage = it)
                                }
                            )
                        }

                    }

                }
                EP_DeleteImageAlert(editProfileViewModel = editProfileViewModel)

                EP_UsernameTextField(editProfileViewModel = editProfileViewModel)
                Spacer(modifier = Modifier.size(16.dp))
                EP_TextField(
                    title = "Name",
                    placeholder = "Add your name",
                    value = editProfileViewModel.nameState.value,
                    onValueChange = editProfileViewModel::onNameChange,
                    singleLine = true
                )
                Spacer(modifier = Modifier.size(16.dp))

                EP_TextField(
                    title = "About me (${editProfileViewModel.aboutMeState.value.count()}/300)",
                    placeholder = "Tell something about yourself",
                    value = editProfileViewModel.aboutMeState.value,
                    onValueChange = editProfileViewModel::onAboutMeChange
                )
                Spacer(modifier = Modifier.size(16.dp))

                val height = editProfileViewModel.heightTextState.value
                EP_MultipleChoiceItem(
                    title = "Height",
                    icon = R.drawable.height,
                    text = if (height.isEmpty() || height.toInt() == 0) "Add height" else height
                ) {
                    editProfileViewModel.onHeightStateChange(true)
                }
                Spacer(modifier = Modifier.size(16.dp))

                EP_MultipleChoiceItem(
                    title = "Birth date",
                    icon = R.drawable.date_ic,
                    text = editProfileViewModel.birthDateState.value.ifEmpty {
                        "Add birth date"
                    }
                ) {
                    editProfileViewModel.onDatePickerChange(true)
                }

                Spacer(modifier = Modifier.size(16.dp))


                val languages = editProfileViewModel.languageState.value.selectedLanguages

                EP_MultipleChoiceItem(
                    title = "Languages",
                    icon = R.drawable.language_ic,
                    text = languages.joinToString(", ") { it.displayName }
                        .ifEmpty { "Add languages you speak" },
                    onClick = editProfileViewModel::onLanguagesStateChange
                )

                Spacer(modifier = Modifier.size(16.dp))

                val gender = editProfileViewModel.gender.value
                EP_MultipleChoiceItem(
                    title = "Gender",
                    icon = R.drawable.gender,
                    text = gender?.displayName ?: "",
                    onClick = {
                        editProfileViewModel.onGenderStateChange(true)
                    })

                Spacer(modifier = Modifier.size(16.dp))

                val interests = editProfileViewModel.interestsState.value.selectedInterests

                EP_MultipleChoiceItem(
                    title = "Interests",
                    icon = R.drawable.language_ic,
                    text = interests.joinToString(", ") { it.displayName }
                        .ifEmpty { "Add your interests" },
                    onClick = editProfileViewModel::onInterestsStateChange
                )

                Spacer(modifier = Modifier.size(16.dp))

                EP_TextField(
                    title = "Job title",
                    placeholder = "What do you do?",
                    value = editProfileViewModel.jobTitleState.value,
                    onValueChange = editProfileViewModel::onJobTitleChange,
                    singleLine = true
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}