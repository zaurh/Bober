package com.zaurh.bober.screen.sign_up

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.Interests
import com.zaurh.bober.data.user.ShowMe
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.screen.sign_up.states.BirthDateState
import com.zaurh.bober.screen.sign_up.states.GenderState
import com.zaurh.bober.screen.sign_up.states.ImageState
import com.zaurh.bober.screen.sign_up.states.InterestsState
import com.zaurh.bober.screen.sign_up.states.PasswordState
import com.zaurh.bober.screen.sign_up.states.ShowMeState
import com.zaurh.bober.screen.sign_up.states.UsernameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _signUpLoading = mutableStateOf(false)
    val signUpLoading: State<Boolean> = _signUpLoading

    private val _usernameState = mutableStateOf(UsernameState())
    val usernameState: State<UsernameState> = _usernameState

    private val _genderState = mutableStateOf(GenderState())
    val genderState: State<GenderState> = _genderState

    private val _birthDateState = mutableStateOf(BirthDateState())
    val birthDateState: State<BirthDateState> = _birthDateState

    private val _showMeState = mutableStateOf(ShowMeState())
    val showMeState: State<ShowMeState> = _showMeState

    private val _interestsState = mutableStateOf(InterestsState())
    val interestsState: State<InterestsState> = _interestsState

    private val _imageState = mutableStateOf(ImageState())
    val imageState: State<ImageState> = _imageState

    private val _passwordState = mutableStateOf(PasswordState())
    val passwordState: State<PasswordState> = _passwordState


    fun onUsernameChange(value: String) {
        _usernameState.value = usernameState.value.copy(
            username = value.lowercase()
        )
    }

    fun checkUsername(onSuccess: () -> Unit) {
        _usernameState.value = usernameState.value.copy(
            loading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepo.checkUsername(usernameState.value.username)
            if (response.success) {
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
                _usernameState.value = usernameState.value.copy(
                    errorMessage = "",
                    loading = false
                )
            } else {
                _usernameState.value = usernameState.value.copy(
                    errorMessage = response.message,
                    loading = false
                )
            }
        }
    }


    fun onDateOfBirthChange(change: Boolean) {
        _birthDateState.value = birthDateState.value.copy(
            datePicker = change
        )
    }

    fun onBirthDateChange(birthDate: String) {
        _birthDateState.value = birthDateState.value.copy(
            birthDate = birthDate
        )
    }

    fun selectShowMe(showMe: ShowMe) {
        _showMeState.value = showMeState.value.copy(
            showMe = showMe
        )
    }

    fun onShowMeChange() {
        _showMeState.value = showMeState.value.copy(
            showMeDropdown = !showMeState.value.showMeDropdown
        )
    }

    fun onInterestChange(interests: Interests) {
        if (_interestsState.value.interests.contains(interests)) {
            _interestsState.value = interestsState.value.copy(
                interests = interestsState.value.interests.minus(interests)
            )
        } else {
            if (interestsState.value.interests.count() < 10){
                _interestsState.value = interestsState.value.copy(
                    interests = interestsState.value.interests.plus(interests)
                )
            }
        }
    }

    fun onPasswordChange(password: String) {
        _passwordState.value = passwordState.value.copy(
            password = password
        )
    }

    fun onPasswordVisibilityChange() {
        _passwordState.value = passwordState.value.copy(
            passwordVisibility = !passwordState.value.passwordVisibility
        )
    }


    fun onGenderChange(gender: Gender) {
        _genderState.value = genderState.value.copy(
            selectedGender = gender
        )
    }


    fun uploadMedia(
        description: String,
        imageUri: Uri,
        contentResolver: ContentResolver,
        context: Context,
        onSuccess: (String) -> Unit
    ) {
        _imageState.value = imageState.value.copy(
            loading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _imageState.value = imageState.value.copy(
                    pickedImage = ""
                )
                val inputStream = contentResolver.openInputStream(imageUri)
                val fileName = imageUri.lastPathSegment ?: "image.jpg"

                inputStream?.use {
                    val response = userRepository.uploadMedia(description, it, fileName)
                    if (response.success) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Uploaded successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                        onSuccess(response.fileLink)
                        _imageState.value = imageState.value.copy(
                            pickedImage = response.fileLink
                        )
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed upload", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                _imageState.value = imageState.value.copy(
                    loading = false
                )

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                _imageState.value = imageState.value.copy(
                    loading = false
                )
            }
        }
    }


    fun signUp() {
        viewModelScope.launch(Dispatchers.IO) {
            _signUpLoading.value = true
            val response = authRepo.signUp(
                username = usernameState.value.username,
                password = passwordState.value.password,
                gender = genderState.value.selectedGender,
                image = imageState.value.pickedImage,
                dateOfBirth = birthDateState.value.birthDate,
                showMe = showMeState.value.showMe,
                interests = interestsState.value.interests
            )
            if (response.success) {
                _signUpLoading.value = false
            } else {
                withContext(Dispatchers.Main) {
                    _signUpLoading.value = false
                    _passwordState.value = passwordState.value.copy(
                        errorMessage = response.passwordError ?: ""
                    )
                    _imageState.value = imageState.value.copy(
                        errorMessage = response.imageError ?: ""
                    )
                }

            }
        }
    }

}