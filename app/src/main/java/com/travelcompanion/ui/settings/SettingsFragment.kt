
package com.travelcompanion.ui.settings
import java.io.IOException
import org.json.JSONObject

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.travelcompanion.R
import com.travelcompanion.data.preferences.SettingsDataStore
import com.travelcompanion.databinding.FragmentSettingsBinding
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @Inject
    lateinit var repository: ITripRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        observeSettings()
        setupListeners()
    }

    private fun observeSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingsDataStore.notifyPoiFlow.collect { enabled ->
                        binding.switchPoi.isChecked = enabled
                    }
                }

                launch {
                    settingsDataStore.notifyRemindersFlow.collect { enabled ->
                        binding.switchReminders.isChecked = enabled
                    }
                }

                launch {
                    settingsDataStore.autoTrackingFlow.collect { enabled ->
                        binding.switchAutoTracking.isChecked = enabled
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.switchPoi.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                settingsDataStore.setNotifyPoi(isChecked)
            }
        }

        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                settingsDataStore.setNotifyReminders(isChecked)
            }
        }

        binding.switchAutoTracking.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                settingsDataStore.setAutoTracking(isChecked)
            }
        }

        binding.btnThemeMode.setOnClickListener {
            showThemeDialog()
        }

        binding.btnExportData.setOnClickListener {
            exportData()
        }

        binding.btnDeleteData.setOnClickListener {
            showDeleteConfirmation()
        }

        // Contatta il supporto: apre il client di posta
        binding.btnContactSupport.setOnClickListener {
            val emailIntent = android.content.Intent(
                android.content.Intent.ACTION_SENDTO,
                android.net.Uri.parse("mailto:" + getString(R.string.support_email))
            ).apply {
                putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            }
            val chooser = android.content.Intent.createChooser(emailIntent, getString(R.string.contact_support))
            if (emailIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(requireContext(), R.string.no_email_app, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showThemeDialog() {
        val themeOptions = arrayOf(
            getString(R.string.theme_light),
            getString(R.string.theme_dark),
            getString(R.string.theme_system)
        )
        val themeValues = arrayOf("light", "dark", "system")

        viewLifecycleOwner.lifecycleScope.launch {
            val currentTheme = settingsDataStore.settingsFlow.first().themeMode
            val checkedItem = themeValues.indexOf(currentTheme)

            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.choose_theme)
                .setSingleChoiceItems(themeOptions, checkedItem) { dialog, which ->
                    val selected = themeValues[which]
                    viewLifecycleOwner.lifecycleScope.launch {
                        settingsDataStore.setThemeMode(selected)
                        applyTheme(selected)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    private fun applyTheme(mode: String) {
        when (mode) {
            "light" -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
            else -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun exportData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Costruisce il JSON e scrive su disco in background
                withContext(Dispatchers.IO) {
                    val exportData = collectExportData()
                    val jsonContent = buildExportJson(exportData)
                    saveToDownloads(jsonContent)
                }
                Toast.makeText(requireContext(), R.string.export_data_success, Toast.LENGTH_LONG).show()
            } catch (e: kotlinx.coroutines.CancellationException) {
                // l'utente ha lasciato la schermata durante l'export: non è un errore, niente toast
                throw e
            } catch (e: IOException) {
                Timber.e(e)
                Toast.makeText(requireContext(), R.string.export_data_error, Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                Timber.e(e)
                Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(requireContext(), R.string.export_data_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun collectExportData(): ExportData {
        val trips = repository.getAllTrips().first()
        val journeys = repository.getAllJourneys().first()
        val settings = settingsDataStore.settingsFlow.first()

        return ExportData(
            exportDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()),
            appVersion = "1.0",
            trips = trips,
            journeys = journeys,
            settings = mapOf(
                "notifyPoi" to settings.notifyPoi,
                "notifyReminders" to settings.notifyReminders,
                "autoTracking" to settings.autoTracking,
                "distanceUnit" to settings.distanceUnit,
                "themeMode" to settings.themeMode
            )
        )
    }

    private suspend fun saveToDownloads(content: String) {
        withContext(Dispatchers.IO) {
            val fileName = "travel_companion_export_${System.currentTimeMillis()}.json"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+: usa MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = requireContext().contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("Failed to create file")

                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                }
            } else {
                // Android pre-10: accesso diretto alla cartella Download
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(content.toByteArray())
                }
            }
        }
    }

    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_all_title)
            .setMessage(R.string.delete_all_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteAllData()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteAllData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Un'unica eliminazione in cascata grazie alle Foreign Key di Room
                    repository.deleteAllTrips()
                    settingsDataStore.clearAll()
                }
                Toast.makeText(requireContext(), R.string.all_data_deleted, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Timber.e(e, "deleteAllData failed")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buildExportJson(data: ExportData): String {
        val sb = StringBuilder()
        sb.appendLine("{")
        sb.appendLine("  \"exportDate\": ${JSONObject.quote(data.exportDate)},")
        sb.appendLine("  \"appVersion\": ${JSONObject.quote(data.appVersion)},")
        sb.appendLine("  \"tripCount\": ${data.trips.size},")
        sb.appendLine("  \"journeyCount\": ${data.journeys.size},")
        sb.appendLine("  \"trips\": [")
        data.trips.forEachIndexed { i, trip ->
            val comma = if (i < data.trips.size - 1) "," else ""
            sb.appendLine("    {\"id\": ${trip.id}, \"title\": ${JSONObject.quote(trip.title)}, \"destination\": ${JSONObject.quote(trip.destination)}, \"type\": ${JSONObject.quote(trip.tripType.name)}, \"distance\": ${trip.totalDistance}, \"duration\": ${trip.totalDuration}}$comma")
        }
        sb.appendLine("  ],")
        sb.appendLine("  \"settings\": {")
        data.settings.entries.forEachIndexed { i, (key, value) ->
            val comma = if (i < data.settings.size - 1) "," else ""
            val jsonValue = if (value is String) JSONObject.quote(value) else "$value"
            sb.appendLine("    ${JSONObject.quote(key)}: $jsonValue$comma")
        }
        sb.appendLine("  }")
        sb.appendLine("}")
        return sb.toString()
    }

    data class ExportData(
        val exportDate: String,
        val appVersion: String,
        val trips: List<com.travelcompanion.domain.model.Trip>,
        val journeys: List<com.travelcompanion.domain.model.Journey>,
        val settings: Map<String, Any>
    )
}

