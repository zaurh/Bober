package com.zaurh.bober.screen.auth.sign_up.sign_up_image

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.auth.sign_up.SignUpViewModel
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.SUI_Password
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.SUI_PickImage
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.image_edit.ImageEditViewModel
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun SignUpImage(
    signUpViewModel: SignUpViewModel,
    imageEditViewModel: ImageEditViewModel,
    navController: NavController
) {
    val focus = LocalFocusManager.current
    val passwordState = signUpViewModel.passwordState.value
    val context = LocalContext.current
    val bitmapState = imageEditViewModel.bitmapState.collectAsState()
    val imageUrlState = imageEditViewModel.imageUrlState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        colorResource(id = R.color.backgroundTop),
                        colorResource(id = R.color.backgroundBottom)
                    )
                )
            )
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = "Ready to go", color = Color.White, fontSize = 32.sp, fontFamily = outfitFamily)

        Column {
            SUI_PickImage(
                signUpViewModel = signUpViewModel,
                onImageSelected = { uri ->
                    imageEditViewModel.uriToBitmap(context, uri)
                    navController.navigate(Screen.EditImage.route)
                    signUpViewModel.bottomSheetTrigger()
                },
                imageEditViewModel = imageEditViewModel
            )
            Spacer(modifier = Modifier.size(16.dp))
            SUI_Password(signUpViewModel = signUpViewModel)
            Spacer(modifier = Modifier.size(4.dp))
            AnimatedVisibility(visible = passwordState.errorMessage.isNotEmpty()) {
                Row {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.warning_ic),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = passwordState.errorMessage,
                        color = Color.White,
                        fontFamily = outfitFamily
                    )
                }
            }
        }


        Column {
            Button(enabled = !signUpViewModel.signUpLoading.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                onClick = {
                    Log.d("dsaljsdadas", "${imageUrlState.value.imageUrl}")
                    focus.clearFocus()
                    imageUrlState.value.imageUrl?.let {
                        signUpViewModel.signUp(imageUrl = it){
                            imageEditViewModel.resetEditing(true)
                        }
                    }


                }) {
                if (signUpViewModel.signUpLoading.value) {
                    Text(
                        text = "Loading...",
                        color = colorResource(id = R.color.darkBlue),
                        fontFamily = outfitFamily,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = colorResource(id = R.color.backgroundTop),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Finish",
                        color = colorResource(id = R.color.darkBlue),
                        fontFamily = outfitFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

}