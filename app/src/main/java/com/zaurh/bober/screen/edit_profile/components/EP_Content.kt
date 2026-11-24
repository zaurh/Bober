package com.zaurh.bober.screen.edit_profile.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel
import com.zaurh.bober.screen.edit_profile.MAX_PHOTOS
import com.zaurh.bober.screen.image_edit.ImageEditorDialog
import com.zaurh.bober.ui.components.SettingsDivider
import com.zaurh.bober.ui.components.SettingsGroup
import com.zaurh.bober.ui.components.SettingsRow
import com.zaurh.bober.ui.theme.PhotoScrim
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable

private const val ADD_TILE_KEY = "add-photo"
private val Gap = 10.dp

@Composable
fun EP_Content(padding: PaddingValues, editProfileViewModel: EditProfileViewModel) {
    val context = LocalContext.current
    val focus = LocalFocusManager.current
    val user = editProfileViewModel.userDataState.collectAsState().value

    LaunchedEffect(Unit) {
        editProfileViewModel.messages.collect { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    // Photos are saved immediately on upload / delete, so follow the server's list.
    LaunchedEffect(user?.imageUrl) {
        user?.imageUrl?.let { editProfileViewModel.onImageChange(it) }
    }

    // Picked photos go through the editor (crop / rotate / filter) before uploading.
    var editing by rememberSaveable { mutableStateOf<Uri?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) editing = uri
    }
    editing?.let { source ->
        ImageEditorDialog(
            source = source,
            onDone = { edited ->
                editing = null
                editProfileViewModel.uploadMedia(imageUri = edited, contentResolver = context.contentResolver, context = context) {
                    editProfileViewModel.updateImage(it)
                }
            },
            onCancel = { editing = null }
        )
    }
    EP_DeleteImageAlert(editProfileViewModel = editProfileViewModel)

    if (user == null) {
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        PhotoGrid(
            editProfileViewModel = editProfileViewModel,
            onAdd = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        )

        SettingsGroup("About you") {
            SettingsRow(
                icon = R.drawable.profile_ic,
                title = "Username",
                subtitle = "@${editProfileViewModel.usernameState.value.text}",
                onClick = { focus.clearFocus(); editProfileViewModel.onUsernameChangeState() }
            )
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = editProfileViewModel.nameState.value,
                    onValueChange = editProfileViewModel::onNameChange,
                    label = { Text("Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                val bio = editProfileViewModel.aboutMeState.value
                OutlinedTextField(
                    value = bio,
                    onValueChange = editProfileViewModel::onAboutMeChange,
                    label = { Text("Bio") },
                    placeholder = { Text("A few words about you") },
                    minLines = 3,
                    maxLines = 6,
                    supportingText = { Text("${bio.length}/300") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = editProfileViewModel.jobTitleState.value,
                    onValueChange = editProfileViewModel::onJobTitleChange,
                    label = { Text("Job title") },
                    placeholder = { Text("What do you do?") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        SettingsGroup("Basics") {
            SettingsRow(
                icon = R.drawable.date_ic,
                title = "Birthday",
                subtitle = editProfileViewModel.birthDateState.value.ifEmpty { "Add your birthday" },
                onClick = { focus.clearFocus(); editProfileViewModel.onDatePickerChange(true) }
            )
            SettingsDivider()
            SettingsRow(
                icon = R.drawable.gender,
                title = "Gender",
                subtitle = editProfileViewModel.gender.value?.displayName ?: "Add your gender",
                onClick = { focus.clearFocus(); editProfileViewModel.onGenderStateChange(true) }
            )
            SettingsDivider()
            val height = editProfileViewModel.heightTextState.value.toIntOrNull()?.takeIf { it > 0 }
            SettingsRow(
                icon = R.drawable.height,
                title = "Height",
                subtitle = height?.let { "$it cm" } ?: "Add your height",
                onClick = { focus.clearFocus(); editProfileViewModel.onHeightStateChange(true) }
            )
        }

        SettingsGroup("More about you") {
            val interests = editProfileViewModel.interestsState.value.selectedInterests
            SettingsRow(
                icon = R.drawable.heart_ic,
                title = "Interests",
                subtitle = interests.joinToString(", ") { it.displayName }.ifEmpty { "Add things you're into" },
                onClick = { focus.clearFocus(); editProfileViewModel.onInterestsStateChange() }
            )
            SettingsDivider()
            val languages = editProfileViewModel.languageState.value.selectedLanguages
            SettingsRow(
                icon = R.drawable.language_ic,
                title = "Languages",
                subtitle = languages.joinToString(", ") { it.displayName }.ifEmpty { "Add languages you speak" },
                onClick = { focus.clearFocus(); editProfileViewModel.onLanguagesStateChange() }
            )
        }
    }
}

/** 3-column grid of portrait tiles. Long-press and drag to reorder; the first photo is the main one. */
@Composable
private fun PhotoGrid(editProfileViewModel: EditProfileViewModel, onAdd: () -> Unit) {
    val images = editProfileViewModel.images.value
    val uploading = editProfileViewModel.imageUploadLoading.value
    val showAdd = images.size < MAX_PHOTOS
    val state = rememberReorderableLazyGridState(
        onMove = { from, to ->
            editProfileViewModel.images.value = editProfileViewModel.images.value.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            editProfileViewModel.onDiscardChanges(true)
        },
        canDragOver = { over, _ -> over.key != ADD_TILE_KEY }
    )

    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Photos · ${images.size}/$MAX_PHOTOS",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 4.dp)
        )
        Text(
            "Hold and drag to reorder. Your first photo is the one people see first.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        // A lazy grid can't size itself inside a scrolling column, so give it its exact height.
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val tileWidth = (maxWidth - Gap * 2) / 3
            val tiles = images.size + if (showAdd) 1 else 0
            val rows = (tiles + 2) / 3
            val gridHeight = tileWidth * 4 / 3 * rows + Gap * (rows - 1).coerceAtLeast(0)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = state.gridState,
                horizontalArrangement = Arrangement.spacedBy(Gap),
                verticalArrangement = Arrangement.spacedBy(Gap),
                userScrollEnabled = false,
                modifier = Modifier
                    .height(gridHeight)
                    .reorderable(state)
            ) {
                items(images, key = { it }) { image ->
                    ReorderableItem(state, key = image) { dragging ->
                        PhotoTile(
                            image = image,
                            main = image == images.firstOrNull(),
                            dragging = dragging,
                            canRemove = images.size > 1,
                            onRemove = { editProfileViewModel.onDeleteImageState(selectedImage = image) },
                            modifier = Modifier.detectReorderAfterLongPress(state)
                        )
                    }
                }
                if (showAdd) {
                    item(key = ADD_TILE_KEY) { AddTile(uploading = uploading, onClick = onAdd) }
                }
            }
        }
    }
}

@Composable
private fun PhotoTile(
    image: String,
    main: Boolean,
    dragging: Boolean,
    canRemove: Boolean,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier
            .aspectRatio(0.75f)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(if (dragging) 3.dp else 0.dp, if (dragging) MaterialTheme.colorScheme.primary else Color.Transparent, shape)
    ) {
        AsyncImage(
            model = image,
            contentDescription = if (main) "Main photo" else "Photo",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_image),
            modifier = Modifier.fillMaxSize()
        )
        if (main) {
            Text(
                "Main",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(PhotoScrim)
                    .padding(start = 8.dp, top = 16.dp, bottom = 6.dp)
            )
        }
        if (canRemove && !dragging) {
            IconButton(
                onClick = onRemove,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.45f), contentColor = Color.White),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(28.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove photo", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun AddTile(uploading: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = !uploading,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        modifier = Modifier.aspectRatio(0.75f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (uploading) {
                CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 3.dp, color = MaterialTheme.colorScheme.primary)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Add photo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
