package com.zaurh.bober.screen.sign_up.sign_up_about

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.screen.sign_up.sign_up_about.components.SUA_DatePicker
import com.zaurh.bober.screen.sign_up.sign_up_about.components.SUA_Interests
import com.zaurh.bober.screen.sign_up.sign_up_about.components.SUA_ShowMe
import com.zaurh.bober.screen.sign_up.sign_up_about.components.SUA_TextField

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignUpAbout(
    signUpViewModel: SignUpViewModel,
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        colorResource(id = R.color.backgroundTop),
                        colorResource(id = R.color.backgroundBottom)
                    )
                )
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Tell us more", color = Color.White, fontSize = 32.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState())
                .weight(8f),
            verticalArrangement = Arrangement.Center
        ) {
            SUA_TextField(
                title = "When is your date of birth?",
                enabled = false,
                icon = R.drawable.date_ic,
                placeholder = "2000-01-01",
                value = signUpViewModel.birthDateState.value.birthDate,
                visualTransformation = VisualTransformation.None,
                keyboardType = KeyboardType.Text,
                onClick = {
                    signUpViewModel.onDateOfBirthChange(true)
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            SUA_ShowMe(signUpViewModel = signUpViewModel)
            Spacer(modifier = Modifier.size(16.dp))
            SUA_Interests(signUpViewModel = signUpViewModel)
            Spacer(modifier = Modifier.size(16.dp))
        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                disabledContainerColor = Color.White
            ), onClick = {
                navController.navigate(Screen.SignUpImage.route)
            }) {
                Text(text = "Next", color = colorResource(id = R.color.darkBlue))
            }
        }
    }

    SUA_DatePicker(signUpViewModel = signUpViewModel)
}


