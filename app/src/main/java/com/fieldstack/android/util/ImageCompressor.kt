package com.fieldstack.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCompressor @Inject constructor() {

    /**
     * Decodes [uri], scales it so neither dimension exceeds [maxDimension],
     * and re-encodes as JPEG at [quality] into the app's cache directory.
     * Returns the URI of the compressed file.
     */
    suspend fun compress(
        context: Context,
        uri: Uri,
        maxDimension: Int = 1280,
        quality: Int = 80,
    ): Uri = withContext(Dispatchers.IO) {
        val input = context.contentResolver.openInputStream(uri)
            ?: return@withContext uri  // fallback: return original if unreadable

        val original = BitmapFactory.decodeStream(input)
        input.close()

        if (original == null) return@withContext uri

        val scaled = scaleBitmap(original, maxDimension)
        val outFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outFile).use { out ->
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        if (scaled !== original) scaled.recycle()
        original.recycle()
        // Delete the source file if it lives in cacheDir (e.g. raw camera capture)
        val sourceFile = runCatching { File(uri.path!!) }.getOrNull()
        if (sourceFile != null && sourceFile != outFile &&
            sourceFile.canonicalPath.startsWith(context.cacheDir.canonicalPath)) {
            sourceFile.delete()
        }
        outFile.toUri()
    }

    private fun scaleBitmap(src: Bitmap, maxDimension: Int): Bitmap {
        val w = src.width
        val h = src.height
        if (w <= maxDimension && h <= maxDimension) return src
        val ratio = maxDimension.toFloat() / maxOf(w, h)
        return Bitmap.createScaledBitmap(src, (w * ratio).toInt(), (h * ratio).toInt(), true)
    }
}
