package com.example.travelcompanion

import android.app.Application
import com.example.travelcompanion.utils.NotificationUtils

class TravelCompanionApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Create notification channel
        NotificationUtils.createNotificationChannel(this)
    }
}