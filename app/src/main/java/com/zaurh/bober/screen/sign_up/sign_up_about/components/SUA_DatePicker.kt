package com.zaurh.bober.screen.sign_up.sign_up_about.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.zaurh.bober.R
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.util.localDateToString
import com.zaurh.bober.util.stringToLocalDate
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SUA_DatePicker(
    signUpViewModel: SignUpViewModel
) {
    val birthDateState = signUpViewModel.birthDateState.value

    if (signUpViewModel.birthDateState.value.datePicker) {
        AlertDialog(containerColor = colorResource(id = R.color.backgroundTop),onDismissRequest = {
            signUpViewModel.onDateOfBirthChange(false)
        }, text = {
            WheelDatePicker(
                startDate = if (birthDateState.birthDate.isNotEmpty()) stringToLocalDate(birthDateState.birthDate) else LocalDate.of(2000,1,1),
                yearsRange = 1924..2005,
                textColor = Color.White
            ) {
                signUpViewModel.onBirthDateChange(localDateToString(it))
            }
        }, confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),onClick = {
                signUpViewModel.onDateOfBirthChange(false)
            }) {
                Text(text = "Done", color = colorResource(id = R.color.darkBlue))
            }
        })
    }

}