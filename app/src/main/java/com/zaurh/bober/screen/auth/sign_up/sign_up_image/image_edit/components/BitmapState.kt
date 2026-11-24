package com.zaurh.bober.screen.auth.sign_up.sign_up_image.image_edit.components

import android.graphics.Bitmap

data class BitmapState(
    val initialBitmap: Bitmap? = null,
    val rotatedBitmap: Bitmap? = null,
    val croppedBitmap: Bitmap? = null,
    val finalBitmap: Bitmap? = null,

    val rotated: Boolean = false,
    val rotation: Float = 0f,

)
