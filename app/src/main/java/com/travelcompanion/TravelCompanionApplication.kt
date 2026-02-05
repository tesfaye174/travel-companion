package com.travelcompanion

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Configuration
import com.travelcompanion.utils.NotificationUtils
import com.travelcompanion.ui.worker.ReminderWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.hilt.work.HiltWorkerFactory

/**
 * Classe Application dell'app.
 * Qui inizializzo tutto quello che deve partire all'avvio:
 * Timber per i log, il canale notifiche e il worker per i reminder.
 */
@HiltAndroidApp
class TravelCompanionApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Attivo Timber solo quando l'app è in modalità debug.
        // Questo evita di inviare log dettagliati in release e somiglia al comportamento usato nei progetti universitari.
        if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            Timber.plant(Timber.DebugTree())
        }

        // Creo il canale per le notifiche (obbligatorio da Android 8/Oreo) per garantire la corretta visualizzazione delle notifiche.
        NotificationUtils.createNotificationChannel(this)

        // Non inizializzo manualmente WorkManager qui: se è abilitata l'inizializzazione automatica tramite InitializationProvider,
        // WorkManager sarà già pronto prima di onCreate e userà la configurazione di default.

        // Schedulo un reminder periodico (giornaliero). Lo faccio sempre, WorkManager gestisce l'esecuzione.
        schedulePeriodicReminder()
    }

    // Fornisco la Configuration a WorkManager in modo che utilizzi il
    // HiltWorkerFactory per l'injection dei worker. Non chiamare
    // WorkManager.initialize manualmente per evitare doppia inizializzazione.
    // WorkManager versions differ: some expect a method getWorkManagerConfiguration(),
    // others expect a property `workManagerConfiguration`. Implement the property
    // to be compatible with the project's WorkManager API.
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    // configuro workmanager per mandare un reminder ogni giorno
    private fun schedulePeriodicReminder() {
        val constraints = Constraints.Builder().build()

        val work = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        // uso enqueueUniquePeriodicWork per evitare duplicati
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "trip_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }
}
