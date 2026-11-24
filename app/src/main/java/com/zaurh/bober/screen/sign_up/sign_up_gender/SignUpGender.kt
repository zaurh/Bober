package com.zaurh.bober.screen.sign_up.sign_up_gender

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.sign_up.GradientOptionCard
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.ui.components.AuthScaffold
import com.zaurh.bober.ui.components.BoberButton
import com.zaurh.bober.ui.components.BoberButtonStyle

@Composable
fun SignUpGender(
    signUpViewModel: SignUpViewModel,
    navController: NavController
) {
    val selected = signUpViewModel.genderState.value.selectedGender
    val options = listOf(
        Triple(Gender.MAN, "Man", R.drawable.gender_man),
        Triple(Gender.WOMAN, "Woman", R.drawable.gender_woman),
        Triple(Gender.OTHER, "Other", null),
    )

    AuthScaffold(
        title = "I am a…",
        subtitle = "Pick what describes you best.",
        step = 2,
        onBack = { navController.popBackStack() },
        bottom = {
            BoberButton(
                text = "Continue",
                onClick = { navController.navigate(Screen.SignUpAbout.route) },
                style = BoberButtonStyle.OnGradient,
                enabled = selected != Gender.PREFER_NOT_TO_SAY
            )
        }
    ) {
        options.forEach { (gender, label, icon) ->
            GradientOptionCard(
                text = label,
                icon = icon,
                selected = selected == gender,
                onClick = { signUpViewModel.onGenderChange(gender) }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
