package com.zaurh.bober.screen.edit_profile.components.modal_sheets

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.zaurh.bober.R
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel
import com.zaurh.bober.util.localDateToString
import com.zaurh.bober.util.stringToLocalDate
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EP_DatePickerMS(
    editProfileViewModel: EditProfileViewModel
) {
    if (editProfileViewModel.datePickerState.value) {
        ModalBottomSheet(content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Birth date",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = {
                        editProfileViewModel.onDatePickerChange(false)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = "Select your date of birth", color = Color.Gray)
                Spacer(modifier = Modifier.size(16.dp))
                val birthDate = editProfileViewModel.birthDateState.value
                Box(modifier = Modifier.fillMaxWidth()){
                    WheelDatePicker(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20))
                            .background(colorResource(id = R.color.gray))
                            .align(Alignment.Center),
                        startDate = if (birthDate.isNotEmpty()) stringToLocalDate(birthDate) else LocalDate.of(2000,1,1),
                        yearsRange = 1924..2006,
                        textColor = Color.White
                    ) {
                        editProfileViewModel.onBirthDateChange(localDateToString(it))
                    }
                }

                Spacer(modifier = Modifier.size(48.dp))
            }
        }, onDismissRequest = {
            editProfileViewModel.onDatePickerChange(false)
        })
    }

}
