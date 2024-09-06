
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.screen.sign_up.sign_up_gender.components.SUG_GenderButtonItem

@Composable
fun SignUpGender(
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
            Text(text = "Select your gender", color = Color.White, fontSize = 32.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            SUG_GenderButtonItem(
                onClick = {
                    signUpViewModel.onGenderChange(Gender.MAN)
                },
                selected = signUpViewModel.genderState.value.selectedGender == Gender.MAN,
                icon = R.drawable.gender_man,
                text = "Man"
            )

            Spacer(modifier = Modifier.size(24.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(80.dp)
                        .alpha(0.5f), color = Color.White
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text = "OR",
                    color = Color.White
                )
                HorizontalDivider(
                    modifier = Modifier
                        .width(80.dp)
                        .alpha(0.5f), color = Color.White
                )
            }
            Spacer(modifier = Modifier.size(24.dp))
            SUG_GenderButtonItem(
                onClick = {
                    signUpViewModel.onGenderChange(Gender.WOMAN)
                },
                selected = signUpViewModel.genderState.value.selectedGender == Gender.WOMAN,
                icon = R.drawable.gender_woman,
                text = "Woman"
            )
        }
        Row(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.clickable {
                    signUpViewModel.onGenderChange(Gender.OTHER)
                    navController.navigate(Screen.SignUpAbout.route)
                },
                text = "Other",
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ), onClick = {
                navController.navigate(Screen.SignUpAbout.route)
            }) {
                Text(text = "Next", color = colorResource(id = R.color.darkBlue))
            }
        }
    }
}

