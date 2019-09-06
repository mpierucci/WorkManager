package com.example.background.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import timber.log.Timber
import java.lang.IllegalArgumentException

class BlurWorker(context: Context, params: WorkerParameters) : Worker(context, params) {


    override fun doWork(): Result {
        return try {
            val context = applicationContext

            val resourceUri = inputData.getString(KEY_IMAGE_URI)

            val picture = decodeImage(resourceUri, context)

            val blurPicture = blurBitmap(picture, context)

            val blurPictureUri = writeBitmapToFile(context, blurPicture)

            makeStatusNotification("Blurred is $blurPictureUri", context)

            val outputData = workDataOf(KEY_IMAGE_URI to blurPictureUri.toString())

            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            Result.failure()
        }
    }

    private fun decodeImage(resourceUri: String?, context: Context): Bitmap {
        if (resourceUri.isNullOrEmpty()) {
            Timber.e("Invalid Uri")
            throw IllegalArgumentException("Invalid Uri")
        }

        val resolver = context.contentResolver

        val picture = BitmapFactory.decodeStream(
            resolver.openInputStream(Uri.parse(resourceUri)))
        return picture
    }
}