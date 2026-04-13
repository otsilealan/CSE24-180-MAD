package com.accommodation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    private const val MAX_DIMENSION = 800
    private const val QUALITY = 80

    fun saveImage(context: Context, uri: Uri): String? = runCatching {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        val original = BitmapFactory.decodeStream(input)
        val scaled = scaleBitmap(original)
        val file = File(context.filesDir, "img_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { scaled.compress(Bitmap.CompressFormat.JPEG, QUALITY, it) }
        file.absolutePath
    }.getOrNull()

    private fun scaleBitmap(bmp: Bitmap): Bitmap {
        val ratio = minOf(MAX_DIMENSION.toFloat() / bmp.width, MAX_DIMENSION.toFloat() / bmp.height, 1f)
        return if (ratio < 1f) Bitmap.createScaledBitmap(bmp, (bmp.width * ratio).toInt(), (bmp.height * ratio).toInt(), true)
        else bmp
    }
}
