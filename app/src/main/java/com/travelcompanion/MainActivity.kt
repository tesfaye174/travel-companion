package com.travelcompanion

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.travelcompanion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Activity principale - è l'unica dell'app, uso il pattern Single Activity
 * con Navigation Component per gestire i vari fragment.
 *
 * Ho messo qui anche la gestione del tema e la vibrazione sui tab
 * perchè mi sembrava il posto più sensato.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // serve per leggere il tema salvato dall'utente
    // In questa variabile viene iniettato il DataStore delle impostazioni tramite Hilt. Permette di accedere alle preferenze utente in modo reattivo.
    @javax.inject.Inject
    lateinit var settingsDataStore: com.travelcompanion.data.preferences.SettingsDataStore

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // le schermate principali dove voglio che si veda la bottom bar
    // Qui si definisce l'elenco delle destinazioni principali dell'app, dove la bottom bar deve essere visibile. Negli altri fragment (es. dettaglio viaggio) la barra viene nascosta per dare più spazio ai contenuti.
    private val mainDestinations = setOf(
        R.id.navigation_home,
        R.id.navigation_map,
        R.id.navigation_tips,
        R.id.navigation_profile
    )

    /**
     * Applica il tema salvato dall'utente (chiaro, scuro o sistema).
     * Se la lettura fallisce, applica il tema di sistema.
     */
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
                // Se qualcosa va storto leggo comunque il tema di sistema.
                // Qui loggo l'errore in modo semplice: non vogliamo crashare l'app per un problema di preferenze.
                Timber.w(e, "applySavedTheme: failed to read settings, using system default")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    /**
     * Configura la navigazione e la bottom bar.
     * Mostra/nasconde la bottom bar in base alla destinazione.
     * Gestisce la vibrazione e la selezione dei tab.
     */
    private fun setupNavigation() {
        val navView = binding.bottomNavigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val shouldShowBottomNav = destination.id in mainDestinations
            binding.bottomNavigation.isVisible = shouldShowBottomNav
            binding.navDivider.isVisible = shouldShowBottomNav
        }

        val vibrator = getVibratorCompat()
        navView.setOnItemSelectedListener { item ->
            // Vibrazione breve per feedback
            // Quando l'utente seleziona una voce della bottom bar, viene attivata una breve vibrazione per migliorare il feedback tattile e l'usabilità.
            vibrator?.let {
                if (it.hasVibrator() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
            val navOptions = androidx.navigation.NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.navigation_home, inclusive = false, saveState = true)
                .setRestoreState(true)
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .build()
            try {
                navController.navigate(item.itemId, null, navOptions)
                true
            } catch (e: Exception) {
                // Se la destinazione non esiste o la navigazione fallisce, logghiamo e ignoriamo il click.
                Timber.w(e, "MainActivity: navigation to %s failed", item.itemId)
                false
            }
        }
        navView.setOnItemReselectedListener { _ -> }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val menu = navView.menu
            menu.forEach { menuItem ->
                menuItem.isChecked = (menuItem.itemId == destination.id)
            }
        }
    }

    /**
     * Restituisce il Vibrator compatibile con la versione Android.
     * Da Android 12 in poi si usa VibratorManager.
     */
    private fun getVibratorCompat(): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as? Vibrator
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
