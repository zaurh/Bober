package com.zaurh.bober.screen.sign_up.sign_up_image.components

import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.screen.sign_up.SignUpViewModel

@Composable
fun SUI_PickImage(
    signUpViewModel: SignUpViewModel
) {
    val pickedImage = signUpViewModel.imageState.value.pickedImage
    val loadingImage = signUpViewModel.imageState.value.loading
    val errorMessage = signUpViewModel.imageState.value.errorMessage
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            signUpViewModel.uploadMedia(
                description = "",
                imageUri = it,
                contentResolver = context.contentResolver,
                context = context
            ) {

            }
        }
    }

    Column {
        Text(text = "Pick a profile picture", color = Color.White)
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
                        if (!loadingImage) {
                            it.clickable {
                                launcher.launch("image/*")
                            }
                        } else {
                            it
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (pickedImage.isNotEmpty()) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = pickedImage,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                } else if (loadingImage) {
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
                Image(modifier = Modifier.size(20.dp),painter = painterResource(id = R.drawable.warning_ic), contentDescription = "")
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = errorMessage, color = Color.White)
            }
        }
    }

}