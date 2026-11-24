package com.zaurh.bober.screen.auth.sign_up.sign_up_image.image_edit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.BRIGHTNESS
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.CONTRAST
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.COOL
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.FilterType
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.GRAYSCALE
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.INVERT
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.LOMO
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.ORIGINAL
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.SEPIA
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.WARM
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.bitmapToUriString
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.image_edit.components.BitmapState
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.image_edit.components.FilterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class ImageUrlState(
    val imageUrl: String? = null,
    val loading: Boolean = false,
    val error: String = ""
)

@HiltViewModel
class ImageEditViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _bitmapState = MutableStateFlow(BitmapState())
    val bitmapState = _bitmapState.asStateFlow()

    private val _imageUrlState = MutableStateFlow(ImageUrlState())
    val imageUrlState = _imageUrlState.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState = _filterState.asStateFlow()

    private val _matrix = MutableStateFlow(ORIGINAL)
    val matrix = _matrix.asStateFlow()

    var topLeft = MutableStateFlow(Offset(0f, 0f))
    var topRight = MutableStateFlow(Offset(0f, 0f))
    var bottomLeft = MutableStateFlow(Offset(0f, 0f))
    var bottomRight = MutableStateFlow(Offset(0f, 0f))

    var offsetChanged = MutableStateFlow(false)

    var screenWidth = MutableStateFlow(0f)
    var screenHeight = MutableStateFlow(0f)


    init {
        startTemp()
    }

    fun startTemp() {
        Log.d("Viewmodelstates", "ImageEdit Started")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Viewmodelstates", "ImageEdit Ended")

    }


    fun updateScreenSize(width: Float, height: Float) {
        Log.d("sdkjladklsa", "$width, $height")
        screenWidth.value = width
        screenHeight.value = height
    }


    fun updateOffsets(
        newTopLeft: Offset,
        newTopRight: Offset,
        newBottomLeft: Offset,
        newBottomRight: Offset
    ) {
        topLeft.value = newTopLeft
        topRight.value = newTopRight
        bottomLeft.value = newBottomLeft
        bottomRight.value = newBottomRight
        offsetChanged.value = true
    }


    fun updateImageState(bitmap: Bitmap) {
        _bitmapState.value = bitmapState.value.copy(
            finalBitmap = bitmap
        )
    }


    fun rotateBitmap() {
        val matrix = Matrix().apply { postRotate(90f) }
        _bitmapState.value.rotatedBitmap?.let {

            val rotatedBitmap = Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
            _bitmapState.value = bitmapState.value.copy(
//                croppedBitmap = rotatedBitmap,
                rotatedBitmap = rotatedBitmap,
                rotated = true,
                rotation = bitmapState.value.rotation + 90f
            )
        }
    }

    fun cropImage(cropped: Bitmap, onSuccess: () -> Unit) {
        _bitmapState.value = bitmapState.value.copy(
            croppedBitmap = cropped,
//            filteredBitmap = cropped,
//            rotatedBitmap = cropped
        )
        onSuccess()
    }

    fun applyFilter(filterType: FilterType) {
        _bitmapState.value.initialBitmap?.let {
            when (filterType) {
                FilterType.ORIGINAL -> {
                    _filterState.value = FilterState(original = true)
                    _matrix.value = ORIGINAL
                }

                FilterType.BRIGHTNESS -> {
                    _filterState.value = FilterState(brightness = true)
                    _matrix.value = BRIGHTNESS
                }

                FilterType.CONTRAST -> {
                    _filterState.value = FilterState(contrast = true)
                    _matrix.value = CONTRAST
                }

                FilterType.GRAYSCALE -> {
                    _filterState.value = FilterState(grayScale = true)
                    _matrix.value = GRAYSCALE
                }

                FilterType.INVERT -> {
                    _filterState.value = FilterState(invert = true)
                    _matrix.value = INVERT
                }

                FilterType.SEPIA -> {
                    _filterState.value = FilterState(sepia = true)
                    _matrix.value = SEPIA
                }

                FilterType.COOL -> {
                    _filterState.value = FilterState(cool = true)
                    _matrix.value = COOL
                }

                FilterType.LOMO -> {
                    _filterState.value = FilterState(lomo = true)
                    _matrix.value = LOMO
                }

                FilterType.WARM -> {
                    _filterState.value = FilterState(warm = true)
                    _matrix.value = WARM
                }
            }

            Log.d("skdajdslkajdsak", "${filterState.value}")

//            val filteredBitmap = when (filterType) {
//                FilterType.ORIGINAL -> _bitmapState.value.initialBitmap
//                FilterType.GRAYSCALE -> null
//                FilterType.SEPIA -> applySepiaFilter(it)
//                FilterType.INVERT -> applyInvertFilter(it)
//                FilterType.BRIGHTNESS -> applyBrightnessFilter(
//                    1.5f,
//                    it
//                )  // Example brightness value
//                FilterType.CONTRAST -> applyContrastFilter(2f, it)      // Example contrast value
//                FilterType.HUE -> applyHueFilter(180f, it)              // Example hue value
//                FilterType.SATURATION -> applySaturationFilter(1.2f, it) // Example saturation value
//            }

            // Update the bitmap with the filtered result
//            _bitmapState.value = bitmapState.value.copy(
//                filteredBitmap = true
//            )
        }
    }

    fun doneEditing(context: Context) {
        viewModelScope.launch(Dispatchers.Default) { // Runs in background thread
            _imageUrlState.update { it.copy(loading = true) }
            Log.d("Started", "Processing started")

            val croppedBitmap = bitmapState.value.croppedBitmap
            if (croppedBitmap == null) {
                Log.e("ErrorBitmap", "Cropped bitmap is null!")
                return@launch
            }

            // Create Matrix for Rotation
            val matrix = Matrix().apply { postRotate(bitmapState.value.rotation) }

            // Rotate the Bitmap
            val rotatedBitmap = Bitmap.createBitmap(
                croppedBitmap, 0, 0, croppedBitmap.width, croppedBitmap.height, matrix, true
            )

            // Create a new bitmap with the same size as rotatedBitmap
            val filteredBitmap = Bitmap.createBitmap(
                rotatedBitmap.width, rotatedBitmap.height, rotatedBitmap.config
            )

            // Create a Canvas to draw onto the new Bitmap
            val canvas = Canvas(filteredBitmap)

            // Create a Paint object with the ColorMatrix
            val paint = Paint().apply {
                colorFilter = ColorMatrixColorFilter(ColorMatrix(_matrix.value))
            }

            // Draw the rotated Bitmap onto the new one with the filter
            canvas.drawBitmap(rotatedBitmap, 0f, 0f, paint)

            val imageUri = bitmapToUriString(context, filteredBitmap)
            Log.d("ImageUri", imageUri ?: "")


            // Run upload on IO dispatcher
            val imageUrl = withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    uploadMedia(Uri.parse(imageUri), context) { url ->
                        continuation.resume(url)
                    }
                }
            }

            // Update UI state on Main Thread
            withContext(Dispatchers.Main) {
                _bitmapState.update { it.copy(finalBitmap = filteredBitmap) }
                _imageUrlState.update { it.copy(imageUrl = imageUrl) }
            }

        }
    }


    fun uriToBitmap(context: Context, imageUri: String) {
        try {
            val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))
            BitmapFactory.decodeStream(inputStream).also {
                inputStream?.close()
                _bitmapState.value = bitmapState.value.copy(
                    initialBitmap = it,
                    croppedBitmap = it,
                    rotatedBitmap = it
                )
            }
            if (bitmapState.value.croppedBitmap == null){
                Log.d("sdkaljdslakdsa", "bitmap is null")
            }else{
                Log.d("sdkaljdslakdsa", "bitmap is not null")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun resetEditing(removeFinal: Boolean = false) {
        _matrix.value = ORIGINAL
        _bitmapState.value = bitmapState.value.copy(
            rotatedBitmap = null,
            croppedBitmap = null,
            rotation = 0f,
            rotated = false
        )
        _imageUrlState.update {
            it.copy(
                imageUrl = null,
                loading = false,
                error = ""
            )
        }
        if (removeFinal) {
            _bitmapState.value = bitmapState.value.copy(
                finalBitmap = null
            )

        }
    }

    private fun uploadMedia(
        imageUri: Uri,
        context: Context,
        onSuccess: (String) -> Unit
    ) {
        _imageUrlState.update { it.copy(loading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val fileName = imageUri.lastPathSegment ?: "image.jpg"

                inputStream?.use { input ->
                    val response = userRepository.uploadMedia("description", input, fileName)
                    if (response.success) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Uploaded successfully", Toast.LENGTH_SHORT)
                                .show()
                            onSuccess(response.fileLink)
                        }
                        _imageUrlState.update { it.copy(loading = false) }

                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed upload", Toast.LENGTH_SHORT).show()
                        }
                        _imageUrlState.update { it.copy(loading = false) }

                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                _imageUrlState.update { it.copy(loading = false) }
            }
        }
    }

}