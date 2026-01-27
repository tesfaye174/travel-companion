package com.travelcompanion

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.travelcompanion.databinding.ActivityMainBinding
import com.travelcompanion.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
        @javax.inject.Inject
        lateinit var settingsDataStore: com.travelcompanion.data.preferences.SettingsDataStore

    private lateinit var binding: ActivityMainBinding

    private fun applySavedTheme() {
        // Applica il tema salvato in DataStore
        lifecycleScope.launch {
            val mode = settingsDataStore.settingsFlow.first()
            when (mode.themeMode) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun setupNavigation() {
        val navView = binding.bottomNavigation

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        val vibrator = getVibratorCompat()
        navView.setOnItemSelectedListener { item ->
            vibrator?.let {
                if (it.hasVibrator()) {
                    it.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
            androidx.navigation.ui.NavigationUI.onNavDestinationSelected(item, navController)
            true
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
        super.onCreate(savedInstanceState)
        // Applica il tema scelto dall'utente prima di setContentView
        // Per ora usa il tema di sistema come default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()

        // Applica il tema salvato in background
        applySavedTheme()
    }
}
