package com.travelcompanion.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.travelcompanion.location.GeofenceProvider
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("BootReceiver received BOOT_COMPLETED, scheduling geofence re-registration")
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val work = OneTimeWorkRequestBuilder<com.travelcompanion.workers.GeofenceRegistrationWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "geofence_re_register",
                ExistingWorkPolicy.REPLACE,
                work
            )
        }
    }
}
