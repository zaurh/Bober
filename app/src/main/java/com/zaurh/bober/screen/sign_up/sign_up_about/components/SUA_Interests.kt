package com.zaurh.bober.screen.sign_up.sign_up_about.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.screen.sign_up.SignUpViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SUA_Interests(
    signUpViewModel: SignUpViewModel
) {
    val interestsState = signUpViewModel.interestsState.value

    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "What are your interests", color = Color.White)
            Text(text = "(${interestsState.interests.size}/10)", color = Color.White)
        }
        Spacer(modifier = Modifier.size(16.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            Interests.entries.forEachIndexed { index, interest ->
                Button(modifier = Modifier.padding(2.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = if (interestsState.interests.contains(interest)) Color.DarkGray else Color.White
                ), onClick = {
                    signUpViewModel.onInterestChange(interest)
                }) {
                    Text(
                        text = interest.displayName,
                        color = if (interestsState.interests.contains(interest)) Color.White else colorResource(
                            id = R.color.darkBlue
                        )
                    )
                }
            }
        }
    }
}