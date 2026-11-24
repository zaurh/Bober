package com.zaurh.bober.screen.edit_profile.components.modal_sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.Languages
import com.zaurh.bober.screen.edit_profile.EditProfileViewModel
import com.zaurh.bober.ui.components.BoberTextField
import com.zaurh.bober.util.localDateToString
import com.zaurh.bober.util.stringToLocalDate
import java.time.LocalDate

private const val MAX_PICKS = 10

/** Shared bottom-sheet layout for the edit profile pickers: title, hint, Done, content. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSheet(
    title: String,
    subtitle: String,
    onDone: () -> Unit,
    scrollable: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDone,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                TextButton(onClick = onDone) { Text("Done", style = MaterialTheme.typography.labelLarge) }
            }
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun SelectChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (selected) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(6.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun EP_DatePickerMS(editProfileViewModel: EditProfileViewModel) {
    if (!editProfileViewModel.datePickerState.value) return
    val birthDate = editProfileViewModel.birthDateState.value
    val latest = LocalDate.now().minusYears(18)
    EditSheet("Birthday", "Your age is shown on your profile.", onDone = { editProfileViewModel.onDatePickerChange(false) }) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            WheelDatePicker(
                startDate = runCatching { stringToLocalDate(birthDate) }.getOrDefault(LocalDate.of(2000, 1, 1)),
                yearsRange = (latest.year - 80)..latest.year,
                textColor = MaterialTheme.colorScheme.onSurface,
            ) { editProfileViewModel.onBirthDateChange(localDateToString(it)) }
        }
    }
}

@Composable
fun EP_GenderMS(editProfileViewModel: EditProfileViewModel) {
    if (!editProfileViewModel.genderState.value) return
    val selected = editProfileViewModel.gender.value
    EditSheet("Gender", "Pick what describes you best.", onDone = { editProfileViewModel.onGenderStateChange(false) }) {
        Gender.entries.forEach { gender ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .selectable(selected = selected == gender, role = Role.RadioButton, onClick = { editProfileViewModel.onGenderSelect(gender) }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selected == gender, onClick = null)
                Spacer(Modifier.size(14.dp))
                Text(gender.displayName, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun EP_HeightMS(editProfileViewModel: EditProfileViewModel) {
    if (!editProfileViewModel.heightState.value) return
    val height = editProfileViewModel.heightTextState.value.takeIf { it != "0" }.orEmpty()
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    EditSheet("Height", "In centimeters.", onDone = { editProfileViewModel.onHeightStateChange(false) }) {
        BoberTextField(
            value = height,
            onValueChange = editProfileViewModel::onHeightTextChange,
            placeholder = "e.g. 175",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            trailing = { Text("cm", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 12.dp)) },
            modifier = Modifier.focusRequester(focus)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EP_InterestsMS(editProfileViewModel: EditProfileViewModel) {
    val state = editProfileViewModel.interestsState.value
    if (!state.modalState) return
    EditSheet(
        "Interests  ·  ${state.selectedInterests.size}/$MAX_PICKS",
        "Pick up to $MAX_PICKS things you're into.",
        onDone = editProfileViewModel::onInterestsStateChange,
        scrollable = true
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Interests.entries.forEach { interest ->
                SelectChip(interest.displayName, interest in state.selectedInterests) { editProfileViewModel.onInterestsChange(interest) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EP_LanguageMS(editProfileViewModel: EditProfileViewModel) {
    val state = editProfileViewModel.languageState.value
    if (!state.modalState) return
    EditSheet(
        "Languages  ·  ${state.selectedLanguages.size}/$MAX_PICKS",
        "Which languages do you speak?",
        onDone = editProfileViewModel::onLanguagesStateChange,
        scrollable = true
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Languages.entries.forEach { language ->
                SelectChip(language.displayName, language in state.selectedLanguages) { editProfileViewModel.onLanguagesChange(language) }
            }
        }
    }
}
