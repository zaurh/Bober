package com.zaurh.bober.screen.sign_up.sign_up_about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.zaurh.bober.R
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.sign_up.GradientChip
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.ui.components.AuthScaffold
import com.zaurh.bober.ui.components.BoberButton
import com.zaurh.bober.ui.components.BoberButtonStyle
import com.zaurh.bober.ui.theme.BoberPalette
import com.zaurh.bober.util.localDateToString
import com.zaurh.bober.util.stringToLocalDate
import java.time.LocalDate

private const val MAX_INTERESTS = 10

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignUpAbout(
    signUpViewModel: SignUpViewModel,
    navController: NavController
) {
    val birthDate = signUpViewModel.birthDateState.value
    val showMe = signUpViewModel.showMeState.value.showMe
    val interests = signUpViewModel.interestsState.value.interests

    AuthScaffold(
        title = "About you",
        subtitle = "Helps us show you the right people.",
        step = 3,
        onBack = { navController.popBackStack() },
        bottom = {
            BoberButton(
                text = "Continue",
                onClick = { navController.navigate(Screen.SignUpImage.route) },
                style = BoberButtonStyle.OnGradient,
            )
        }
    ) {
        Label("Birthday")
        Surface(
            onClick = { signUpViewModel.onDateOfBirthChange(true) },
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            contentColor = BoberPalette.Navy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.date_ic), null, Modifier.size(22.dp), tint = BoberPalette.Navy.copy(alpha = 0.6f))
                Spacer(Modifier.size(12.dp))
                Text(birthDate.birthDate, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(24.dp))
        Label("Show me")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(ShowMe.WOMAN, ShowMe.MAN, ShowMe.EVERYONE).forEach { option ->
                GradientChip(
                    text = option.displayName,
                    selected = showMe == option,
                    onClick = { signUpViewModel.selectShowMe(option) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Label("Interests", Modifier.weight(1f))
            Text("${interests.size}/$MAX_INTERESTS", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Interests.entries.forEach { interest ->
                GradientChip(
                    text = interest.displayName,
                    selected = interest in interests,
                    onClick = { signUpViewModel.onInterestChange(interest) }
                )
            }
        }
    }

    if (birthDate.datePicker) {
        val latest = LocalDate.now().minusYears(18)
        AlertDialog(
            onDismissRequest = { signUpViewModel.onDateOfBirthChange(false) },
            title = { Text("Your birthday") },
            text = {
                WheelDatePicker(
                    startDate = runCatching { stringToLocalDate(birthDate.birthDate) }.getOrDefault(LocalDate.of(2000, 1, 1)),
                    yearsRange = (latest.year - 80)..latest.year,
                    textColor = MaterialTheme.colorScheme.onSurface,
                ) { signUpViewModel.onBirthDateChange(localDateToString(it)) }
            },
            confirmButton = {
                TextButton(onClick = { signUpViewModel.onDateOfBirthChange(false) }) { Text("Done") }
            }
        )
    }
}

@Composable
private fun Label(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = Color.White,
        modifier = modifier.padding(start = 4.dp, bottom = 10.dp)
    )
}
