package com.travelcompanion.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object PermissionUtils {

    // Permission request codes
    private const val REQUEST_LOCATION_PERMISSION = 100
    private const val REQUEST_CAMERA_PERMISSION = 101
    private const val REQUEST_STORAGE_PERMISSION = 102
    private const val REQUEST_NOTIFICATION_PERMISSION = 103
    private const val REQUEST_BACKGROUND_LOCATION = 104

    // Permission groups
    val LOCATION_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    val CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    val NOTIFICATION_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray()
    }

    // Check permissions
    fun hasLocationPermissions(context: Context): Boolean {
        return hasPermissions(context, *LOCATION_PERMISSIONS)
    }

    fun hasCameraPermission(context: Context): Boolean {
        return hasPermissions(context, *CAMERA_PERMISSIONS)
    }

    fun hasStoragePermissions(context: Context): Boolean {
        return hasPermissions(context, *STORAGE_PERMISSIONS)
    }

    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return hasPermissions(context, *NOTIFICATION_PERMISSIONS)
        }
        return true
    }

    fun hasBackgroundLocationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    // Request permissions with modern Activity Result API
    fun registerPermissionLauncher(
        fragment: Fragment,
        onGranted: () -> Unit,
        onDenied: () -> Unit,
        onRationale: (permissions: List<String>) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.entries.all { it.value }

            if (granted) {
                onGranted()
            } else {
                val deniedPermissions = permissions
                    .filter { !it.value }
                    .map { it.key }

                val shouldShowRationale = deniedPermissions.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        fragment.requireActivity(),
                        permission
                    )
                }

                if (shouldShowRationale) {
                    onRationale(deniedPermissions)
                } else {
                    onDenied()
                }
            }
        }
    }

    // Show rationale dialog
    fun showPermissionRationaleDialog(
        context: Context,
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onCancel: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> onConfirm() }
            .setNegativeButton("Annulla") { _, _ -> onCancel() }
            .setCancelable(false)
            .show()
    }

    // Show settings dialog
    fun showSettingsDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Permessi Necessari")
            .setMessage("Per utilizzare tutte le funzionalità dell'app, " +
                    "è necessario concedere i permessi nelle impostazioni.")
            .setPositiveButton("Impostazioni") { _, _ ->
                openAppSettings(context)
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    // Check if permission rationale should be shown
    fun shouldShowPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
}

