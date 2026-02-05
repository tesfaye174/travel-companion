package com.travelcompanion.utils

import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import timber.log.Timber

class CameraHelper(private val context: Context) {

    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null

    fun startCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: IllegalStateException) {
                // CameraX binding error, likely already bound
                Timber.w(e, "CameraHelper: bindToLifecycle failed (IllegalState)")
            } catch (e: SecurityException) {
                // Camera permission denied
                Timber.w(e, "CameraHelper: permission denied for camera")
            } catch (e: Exception) {
                // Catch generico come fallback, da monitorare
                Timber.e(e, "CameraHelper: unexpected error starting camera")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePhoto(onPhotoTaken: (Uri) -> Unit) {
        val imageCapture = imageCapture ?: return

        val photoFile = createImageFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onPhotoTaken(savedUri)
                }

                override fun onError(exc: ImageCaptureException) {
                    Timber.e(exc, "CameraHelper: error saving image")
                }
            }
        )
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}

