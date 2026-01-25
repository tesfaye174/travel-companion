package com.travelcompanion

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.travelcompanion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
        @javax.inject.Inject
        lateinit var settingsDataStore: com.travelcompanion.data.preferences.SettingsDataStore
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Applica il tema scelto dall'utente prima di setContentView
        applySavedTheme()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        private fun applySavedTheme() {
            // Applica il tema salvato in DataStore
            val job = kotlinx.coroutines.GlobalScope.launch {
                val mode = settingsDataStore.settingsFlow.first().themeMode
                when (mode) {
                    "light" -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
                    "dark" -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
                    else -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
            runBlocking { job.join() }
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
}

