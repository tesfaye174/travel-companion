package com.travelcompanion

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.travelcompanion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

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
    @javax.inject.Inject
    lateinit var settingsDataStore: com.travelcompanion.data.preferences.SettingsDataStore

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // le schermate principali dove voglio che si veda la bottom bar
    // nelle altre (tipo dettaglio viaggio) la nascondo
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
                // se qualcosa va storto uso il tema di sistema
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    // setup della navigazione con bottom bar e navcontroller
    private fun setupNavigation() {
        val navView = binding.bottomNavigation

        // prendo il navcontroller dal fragment container
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // nascondo la bottom bar quando sono in una schermata di dettaglio
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val shouldShowBottomNav = destination.id in mainDestinations
            binding.bottomNavigation.isVisible = shouldShowBottomNav
            binding.navDivider.isVisible = shouldShowBottomNav
        }

        // vibrazione quando cambio tab, è un tocco carino
        val vibrator = getVibratorCompat()

        // listener per gestire i click sui tab
        navView.setOnItemSelectedListener { item ->
            // vibrazione corta di 30ms
            vibrator?.let {
                if (it.hasVibrator() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }

            // navigo alla destinazione con delle animazioni fade
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
                // a volte capita che la destinazione non esiste, meglio non crashare
                false
            }
        }

        // per ora non faccio niente se l'utente clicca di nuovo sulla tab selezionata
        // magari in futuro aggiungo lo scroll to top
        navView.setOnItemReselectedListener { _ ->
        }

        // sincronizzo la selezione della bottom bar quando navigo col back button
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // aggiorno quale tab è selezionato
            val menu = navView.menu
            for (i in 0 until menu.size()) {
                val menuItem = menu.getItem(i)
                if (menuItem.itemId == destination.id) {
                    menuItem.isChecked = true
                    break
                }
            }
        }
    }

    // restituisce il vibrator compatibile con le varie versioni android
    // da android 12 cambia come si ottiene
    private fun getVibratorCompat(): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as? VibratorManager
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
