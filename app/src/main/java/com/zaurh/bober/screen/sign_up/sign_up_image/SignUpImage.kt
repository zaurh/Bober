package com.zaurh.bober.screen.sign_up.sign_up_image

import com.zaurh.bober.screen.image_edit.ImageEditorDialog
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.zaurh.bober.navigation.Screen
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.ui.components.AuthScaffold
import com.zaurh.bober.ui.components.BoberButton
import com.zaurh.bober.ui.components.BoberButtonStyle
import com.zaurh.bober.ui.components.BoberPasswordField
import com.zaurh.bober.ui.components.FieldMessage
import com.zaurh.bober.ui.theme.BoberPalette

@Composable
fun SignUpImage(
    signUpViewModel: SignUpViewModel,
    navController: NavController,
) {
    val focus = LocalFocusManager.current
    val context = LocalContext.current
    val image = signUpViewModel.imageState.value
    val password = signUpViewModel.passwordState.value
    val loading = signUpViewModel.signUpLoading.value

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
                signUpViewModel.uploadMedia(edited, context.contentResolver)
            },
            onCancel = { editing = null }
        )
    }

    val signedIn = signUpViewModel.signedIn.collectAsState()
    LaunchedEffect(signedIn.value) {
        if (signedIn.value) navController.navigate(Screen.PagerScreen.route) { popUpTo(0) }
    }

    fun finish() {
        focus.clearFocus()
        signUpViewModel.signUp()
    }

    AuthScaffold(
        title = "Last step",
        subtitle = "Add your best photo and secure your account.",
        step = 4,
        onBack = { navController.popBackStack() },
        bottom = {
            BoberButton(
                text = "Create account",
                onClick = ::finish,
                style = BoberButtonStyle.OnGradient,
                loading = loading,
                enabled = image.pickedImage.isNotEmpty() && password.password.isNotEmpty() && !image.loading,
            )
        }
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                enabled = !image.loading,
                shape = MaterialTheme.shapes.large,
                color = Color.White.copy(alpha = 0.16f),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)),
                modifier = Modifier
                    .width(180.dp)
                    .height(240.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    when {
                        image.pickedImage.isNotEmpty() -> {
                            AsyncImage(
                                model = image.pickedImage,
                                contentDescription = "Profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .size(36.dp)
                            ) {
                                Icon(Icons.Default.Edit, "Change photo", tint = BoberPalette.PinkStrong, modifier = Modifier.padding(8.dp))
                            }
                        }
                        image.loading -> CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                        else -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(52.dp)) {
                                Icon(Icons.Default.Add, null, tint = BoberPalette.PinkStrong, modifier = Modifier.padding(12.dp))
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("Add photo", color = Color.White, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
            val imageError = image.errorMessage.ifEmpty { null }
            if (imageError != null) FieldMessage(imageError, onGradient = true)
        }

        Spacer(Modifier.height(28.dp))
        BoberPasswordField(
            value = password.password,
            onValueChange = signUpViewModel::onPasswordChange,
            label = "Password",
            placeholder = "At least 8 characters",
            onGradient = true,
            error = password.errorMessage.ifEmpty { null },
            keyboardActions = KeyboardActions(onDone = { finish() }),
        )
    }
}
