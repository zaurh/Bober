package com.zaurh.bober.screen.edit_profile

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.Languages
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.data.user.UserUpdate
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.screen.edit_profile.states.DeleteImageState
import com.zaurh.bober.screen.edit_profile.states.InterestsState
import com.zaurh.bober.screen.edit_profile.states.LanguageState
import com.zaurh.bober.screen.edit_profile.states.UsernameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** The backend keeps at most this many profile photos. */
const val MAX_PHOTOS = 6

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepo: AuthRepo
) : ViewModel() {

    val userDataState: StateFlow<UserData?> = userRepository.userData

    private val _loadingState = mutableStateOf(false)
    val loadingState: State<Boolean> = _loadingState

    private val _discardChanges = mutableStateOf(false)
    val discardChanges: State<Boolean> = _discardChanges

    private val _discardChangesAlert = mutableStateOf(false)
    val discardChangesAlert: State<Boolean> = _discardChangesAlert

    private val _usernameState =
        mutableStateOf(UsernameState(text = userDataState.value?.username ?: ""))
    val usernameState: State<UsernameState> = _usernameState

    private val _nameState = mutableStateOf(userDataState.value?.name ?: "")
    val nameState: State<String> = _nameState

    private val _images = mutableStateOf(userDataState.value?.imageUrl ?: listOf())
    val images: MutableState<List<String>> = _images

    private val _imageUploadLoading = mutableStateOf(false)
    val imageUploadLoading: State<Boolean> = _imageUploadLoading

    private val _deleteImageState = mutableStateOf(DeleteImageState())
    val deleteImageState: State<DeleteImageState> = _deleteImageState

    private val _aboutMeState = mutableStateOf(userDataState.value?.aboutMe ?: "")
    val aboutMeState: State<String> = _aboutMeState

    private val _heightTextState = mutableStateOf(userDataState.value?.height?.takeIf { it > 0 }?.toString().orEmpty())
    val heightTextState: State<String> = _heightTextState

    private val _jobTitleState = mutableStateOf(userDataState.value?.jobTitle ?: "")
    val jobTitleState: State<String> = _jobTitleState

    private val _gender = mutableStateOf(userDataState.value?.gender)
    val gender: State<Gender?> = _gender

    private val _birthDateState = mutableStateOf(userDataState.value?.birthDate ?: "")
    val birthDateState: State<String> = _birthDateState

    private val _heightState = mutableStateOf(false)
    val heightState: State<Boolean> = _heightState

    private val _datePickerState = mutableStateOf(false)
    val datePickerState: State<Boolean> = _datePickerState

    private val _languageState = mutableStateOf(LanguageState(selectedLanguages = userDataState.value?.languages ?: listOf()))
    val languageState: State<LanguageState> = _languageState

    private val _interestsState = mutableStateOf(InterestsState(selectedInterests = userDataState.value?.interests ?: listOf()))
    val interestsState: State<InterestsState> = _interestsState

    private val _genderState = mutableStateOf(false)
    val genderState: State<Boolean> = _genderState

    init {
        // The screen can open before the profile has loaded; fill the form once it arrives, so saving
        // never overwrites the real profile with empty fields.
        if (userDataState.value == null) {
            viewModelScope.launch {
                userDataState.filterNotNull().first().let { user ->
                    if (!discardChanges.value) fillFrom(user)
                }
            }
        }
    }

    private fun fillFrom(user: UserData) {
        _usernameState.value = usernameState.value.copy(text = user.username.orEmpty())
        _nameState.value = user.name.orEmpty()
        _images.value = user.imageUrl.orEmpty()
        _aboutMeState.value = user.aboutMe.orEmpty()
        _heightTextState.value = user.height?.takeIf { it > 0 }?.toString().orEmpty()
        _jobTitleState.value = user.jobTitle.orEmpty()
        _gender.value = user.gender
        _birthDateState.value = user.birthDate.orEmpty()
        _languageState.value = languageState.value.copy(selectedLanguages = user.languages.orEmpty())
        _interestsState.value = interestsState.value.copy(selectedInterests = user.interests.orEmpty())
    }


    fun onUsernameChange(username: String) {
        if (username.length <= 20){
            _usernameState.value = usernameState.value.copy(
                text = username.lowercase()
            )
        }
    }

    fun onUsernameChangeState() {
        _usernameState.value = usernameState.value.copy(
            changeState = !usernameState.value.changeState,
            error = ""
        )
    }

    fun checkUsername() {
        viewModelScope.launch(Dispatchers.IO) {
            _usernameState.value = usernameState.value.copy(
                loading = true
            )
            val response = authRepo.checkUsername(usernameState.value.text)
            if (response.success) {
                userRepository.updateUserData(
                    userUpdate = UserUpdate(
                        username = usernameState.value.text
                    )
                )
                _usernameState.value = usernameState.value.copy(
                    changeState = false,
                    error = "",
                    loading = false
                )
            } else {
                _usernameState.value = usernameState.value.copy(
                    error = response.message,
                    loading = false
                )
            }
        }
    }

    fun onNameChange(name: String) {
        _discardChanges.value = true
        if (name.length < 30){
            _nameState.value = name
        }
    }

    fun onAboutMeChange(aboutMe: String) {
        _discardChanges.value = true
        if (aboutMe.length <= 300){
            _aboutMeState.value = aboutMe
        }
    }

    fun onHeightTextChange(height: String) {
        _discardChanges.value = true
        if (height.all { it.isDigit() }) {
            if (height.isNotEmpty()) {
                if (height.toInt() < 230) {
                    _heightTextState.value = height
                }
            } else {
                _heightTextState.value = height
            }

        }
    }

    fun onJobTitleChange(jobTitle: String) {
        _discardChanges.value = true
        if (jobTitle.length <= 30){
            _jobTitleState.value = jobTitle
        }
    }

    fun onHeightStateChange(change: Boolean) {
        _heightState.value = change
    }

    fun onDatePickerChange(change: Boolean) {
        _datePickerState.value = change
    }

    fun onLanguagesStateChange() {
        _languageState.value = languageState.value.copy(
            modalState = !languageState.value.modalState
        )
    }

    fun onLanguagesChange(language: Languages) {
        _discardChanges.value = true
        _languageState.value = languageState.value.copy(
            selectedLanguages = if (languageState.value.selectedLanguages.contains(language)) {
                languageState.value.selectedLanguages.minus(language)
            } else {
                if (languageState.value.selectedLanguages.count() < 10){
                    languageState.value.selectedLanguages.plus(language)
                }else{
                    languageState.value.selectedLanguages
                }
            }
        )
    }

    fun onInterestsStateChange() {
        _interestsState.value = interestsState.value.copy(
            modalState = !interestsState.value.modalState
        )
    }

    fun onInterestsChange(interest: Interests) {
        _discardChanges.value = true
        _interestsState.value = interestsState.value.copy(
            selectedInterests = if (interestsState.value.selectedInterests.contains(interest)) {
                interestsState.value.selectedInterests.minus(interest)
            } else {
                if (interestsState.value.selectedInterests.count() < 10){
                    interestsState.value.selectedInterests.plus(interest)
                }else{
                    interestsState.value.selectedInterests
                }
            }
        )
    }

    fun onGenderStateChange(change: Boolean) {
        _genderState.value = change
    }

    fun onGenderSelect(gender: Gender) {
        _discardChanges.value = true
        _gender.value = gender
    }

    fun onBirthDateChange(birthDate: String) {
        _discardChanges.value = true
        _birthDateState.value = birthDate
    }


    fun onImageChange(imageList: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            _images.value = imageList
        }
    }

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    /** Errors (failed save, upload, delete) to show as a toast. */
    val messages: SharedFlow<String> = _messages

    fun updateUserData(onSuccess: () -> Unit) {
        if (loadingState.value) return
        viewModelScope.launch(Dispatchers.IO) {
            _loadingState.value = true
            val response = userRepository.updateUserData(
                userUpdate = UserUpdate(
                    name = nameState.value,
                    aboutMe = aboutMeState.value,
                    languages = languageState.value.selectedLanguages,
                    interests = interestsState.value.selectedInterests,
                    imageUrl = images.value,
                    height = heightTextState.value.toIntOrNull() ?: 0,
                    jobTitle = jobTitleState.value,
                    gender = gender.value,
                    birthDate = birthDateState.value
                ),
            )
            _loadingState.value = false
            if (response.success) {
                _discardChanges.value = false
                withContext(Dispatchers.Main) { onSuccess() }
            } else {
                _messages.emit(response.message)
            }
        }
    }

    fun uploadMedia(
        imageUri: Uri,
        contentResolver: ContentResolver,
        context: Context,
        onSuccess: (String) -> Unit
    ) {
        _imageUploadLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val fileName = imageUri.lastPathSegment ?: "image.jpg"

                inputStream?.use {
                    val response = userRepository.uploadMedia(it, fileName)
                    if (response.success) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Uploaded successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                        onSuccess(response.fileLink)
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                _imageUploadLoading.value = false

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                _imageUploadLoading.value = false

            }
        }
    }


    fun updateImage(fileLink: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if ((userDataState.value?.imageUrl?.size ?: 0) < MAX_PHOTOS) {
                userRepository.updateUserData(
                    userUpdate = UserUpdate(
                        imageUrl = userDataState.value?.imageUrl?.plus(fileLink)
                            ?: listOf(fileLink)
                    )
                )
            }
        }
    }

    fun onDeleteImageState(selectedImage: String) {
        _deleteImageState.value = deleteImageState.value.copy(
            selectedImage = selectedImage,
            confirmation = !deleteImageState.value.confirmation,
            cannotDelete = false
        )
    }

    fun deleteImage(fileLink: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (images.value.size > 1){
                _deleteImageState.value = deleteImageState.value.copy(
                    cannotDelete = false,
                    loading = true
                )
                userRepository.updateUserData(
                    userUpdate = UserUpdate(
                        imageUrl = userDataState.value?.imageUrl?.minus(fileLink) ?: listOf()
                    ),
                    onSuccess = {
                        _deleteImageState.value = deleteImageState.value.copy(
                            loading = false,
                            confirmation = false,
                            cannotDelete = false,
                            selectedImage = ""
                        )
                    },
                    onFailure = {
                        _deleteImageState.value = deleteImageState.value.copy(
                            loading = false,
                            selectedImage = ""
                        )
                        _messages.tryEmit("Couldn't delete the photo. Please try again.")
                    }
                )
            }else{
                _deleteImageState.value = deleteImageState.value.copy(
                    cannotDelete = true,
                    selectedImage = ""
                )
            }

        }
    }

    fun onDiscardChanges(change: Boolean) {
        _discardChanges.value = change
    }

    fun onDiscardChangesAlert() {
        _discardChangesAlert.value = !discardChangesAlert.value
    }


}