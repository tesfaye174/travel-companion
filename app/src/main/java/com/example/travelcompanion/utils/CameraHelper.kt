package com.example.travelcompanion.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraHelper(private val context: Context) {

    private var imageCapture: ImageCapture? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun startCamera(lifecycleOwner: LifecycleOwner, previewView: androidx.camera.view.PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraHelper", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun takePhoto(onPhotoSaved: (Uri) -> Unit) {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraHelper", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onPhotoSaved(savedUri)
                }
            }
        )
    }

    private fun getOutputDirectory(): File {
        val mediaDir = context.getExternalFilesDir(null)?.let {
            File(it, "TravelCompanion").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}
