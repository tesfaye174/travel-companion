package com.travelcompanion

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.travelcompanion.databinding.ActivityMainBinding
import com.travelcompanion.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/**
 * Activity principale dell'applicazione Travel Companion.
 *
 * Questa è l'unica Activity dell'app (architettura Single Activity).
 * La navigazione tra le varie schermate avviene tramite il Navigation Component
 * che gestisce i Fragment all'interno del NavHostFragment.
 *
 * Funzionalità principali:
 * - Gestione della BottomNavigationView per la navigazione principale
 * - Applicazione del tema (chiaro/scuro) salvato nelle preferenze
 * - Feedback aptico (vibrazione) quando si cambia tab
 *
 * L'annotazione @AndroidEntryPoint permette a Hilt di iniettare le dipendenze.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // Inietto il DataStore per leggere le preferenze del tema
    // Uso field injection perché le Activity non supportano constructor injection
    @javax.inject.Inject
    lateinit var settingsDataStore: com.travelcompanion.data.preferences.SettingsDataStore

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Set delle destinazioni "top-level" dove la bottom navigation deve essere visibile.
    // Per le schermate di dettaglio (es. TripDetails) la bottom nav viene nascosta
    // per dare più spazio al contenuto e migliorare l'esperienza utente.
    private val mainDestinations = setOf(
        R.id.navigation_home,
        R.id.navigation_map,
        R.id.navigation_tips,
        R.id.navigation_profile
    )

    private fun applySavedTheme() {
        lifecycleScope.launch {
            try {
                val mode = settingsDataStore.settingsFlow.first()
                when (mode.themeMode) {
                    "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            } catch (e: Exception) {
                // Fallback al tema di sistema
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    /**
     * Configura la navigazione dell'app usando il Navigation Component di Jetpack.
     *
     * Il setup include:
     * 1. Collegamento tra BottomNavigationView e NavController
     * 2. Listener per mostrare/nascondere la bottom nav in base alla destinazione
     * 3. Feedback aptico al cambio di tab
     * 4. Animazioni fluide durante la navigazione
     */
    private fun setupNavigation() {
        val navView = binding.bottomNavigation

        // Recupero il NavController dal NavHostFragment definito nel layout XML
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Questo metodo collega automaticamente i menu items della bottom nav
        // alle destinazioni nel navigation graph (basandosi sugli ID)
        navView.setupWithNavController(navController)

        // Nascondo la bottom nav quando l'utente è in una schermata di dettaglio.
        // Uso isVisible invece di visibility per semplicità (estensione di ktx)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val shouldShowBottomNav = destination.id in mainDestinations
            binding.bottomNavigation.isVisible = shouldShowBottomNav
            binding.navDivider.isVisible = shouldShowBottomNav
        }

        // Aggiungo una leggera vibrazione quando si preme un tab.
        // È un piccolo tocco di UX che rende l'app più piacevole da usare.
        val vibrator = getVibratorCompat()
        navView.setOnItemSelectedListener { item ->
            // Evito di ricaricare il fragment se l'utente clicca sulla tab già selezionata
            if (navController.currentDestination?.id == item.itemId) {
                return@setOnItemSelectedListener false
            }

            // Vibrazione di 30ms - breve ma percepibile
            vibrator?.let {
                if (it.hasVibrator()) {
                    it.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }

            // Uso NavOptions per avere animazioni di fade in/out durante la navigazione.
            // setPopUpTo evita che si accumulino troppe destinazioni nel back stack.
            val navOptions = androidx.navigation.NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.navigation_home, false)
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .build()

            try {
                navController.navigate(item.itemId, null, navOptions)
                true
            } catch (e: Exception) {
                // In caso di errore di navigazione (es. destinazione non trovata)
                // non crasha l'app ma semplicemente non naviga
                false
            }
        }

        // Quando l'utente clicca di nuovo sulla tab già selezionata,
        // potremmo implementare lo scroll verso l'alto della lista.
        // Per ora è lasciato vuoto ma la struttura c'è per future implementazioni.
        navView.setOnItemReselectedListener { _ ->
            // TODO: implementare scroll to top della RecyclerView del fragment corrente
        }
    }

    /**
     * Restituisce il Vibrator in modo compatibile con tutte le versioni Android.
     *
     * Da Android 12 (API 31) il Vibrator si ottiene dal VibratorManager,
     * mentre nelle versioni precedenti si usava direttamente getSystemService.
     * Ho usato @Suppress per silenziare il warning sulla API deprecata
     * che devo comunque usare per il supporto delle versioni vecchie.
     */
    private fun getVibratorCompat(): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        applySavedTheme()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
