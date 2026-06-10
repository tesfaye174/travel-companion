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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.travelcompanion.data.preferences.SettingsDataStore
import com.travelcompanion.databinding.ActivityMainBinding
import com.travelcompanion.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private fun setupNavigation() {
        val navView = binding.bottomNavigation

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        val topLevelDestinations = setOf(
            R.id.navigation_home,
            R.id.navigation_trips,
            R.id.navigation_map,
            R.id.navigation_statistics,
            R.id.navigation_profile
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.isVisible = destination.id in topLevelDestinations
        }

        val vibrator = getVibratorCompat()
        navView.setOnItemSelectedListener { item ->
            // Prova a fare vibrazione tattile senza bloccare se il dispositivo non la supporta
            try {
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } catch (_: Exception) { /* ignore */ }
            androidx.navigation.ui.NavigationUI.onNavDestinationSelected(item, navController)
        }
    }

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
        // Applica il tema prima di super.onCreate() per evitare ricreazione dell'activity
        applyThemeBeforeCreate()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun applyThemeBeforeCreate() {
        // Legge il tema dalle SharedPreferences direttamente (Hilt non è ancora disponibile)
        val prefs = getSharedPreferences(SettingsDataStore.THEME_PREFS, MODE_PRIVATE)
        val themeMode = prefs.getString(
            SettingsDataStore.THEME_PREFS_KEY,
            "system"
        ) ?: "system"

        val targetMode = when (themeMode) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> return
        }
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
        }
    }
}
