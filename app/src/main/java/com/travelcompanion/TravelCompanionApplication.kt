package com.travelcompanion

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.travelcompanion.utils.NotificationUtils
import com.travelcompanion.ui.worker.ReminderWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Classe Application principale dell'app Travel Companion.
 *
 * Questa è la classe che viene istanziata per prima all'avvio dell'app.
 * L'annotazione @HiltAndroidApp è fondamentale perché:
 * - Genera il codice necessario per l'iniezione delle dipendenze con Hilt
 * - Crea il "component" di base a livello applicazione
 *
 * Ho deciso di inizializzare qui Timber per il logging e il canale
 * per le notifiche dato che devono essere disponibili fin dall'avvio.
 *
 * @see <a href="https://developer.android.com/training/dependency-injection/hilt-android">Documentazione Hilt</a>
 */
@HiltAndroidApp
class TravelCompanionApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Timber serve per il logging durante lo sviluppo.
        // Lo attivo solo in debug perché in produzione non voglio
        // che i log finiscano nel logcat (questione di sicurezza e performance)
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebuggable) {
            Timber.plant(Timber.DebugTree())
        }

        // Il canale notifiche va creato prima di poter inviare qualsiasi notifica (API 26+)
        NotificationUtils.createNotificationChannel(this)

        // Schedulo il worker che ricorda all'utente di pianificare viaggi
        schedulePeriodicReminder()
    }

    /**
     * Configura un task periodico che invia un promemoria giornaliero all'utente.
     *
     * WorkManager è la soluzione raccomandata da Google per task in background
     * che devono essere eseguiti anche se l'app è chiusa o il dispositivo riavviato.
     */
    private fun schedulePeriodicReminder() {
        // Non metto constraints particolari (WiFi, batteria ecc.)
        // perché è solo un reminder leggero che non consuma risorse
        val constraints = Constraints.Builder().build()

        // PeriodicWorkRequest per un task che si ripete ogni giorno
        val work = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        // enqueueUniquePeriodicWork evita duplicati: se esiste già un worker
        // con lo stesso nome, viene sostituito (POLICY.UPDATE)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "trip_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }
}
