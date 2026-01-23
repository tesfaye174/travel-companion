package com.travelcompanion.ui.settings

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

    private val gson: Gson = GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()

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

        observeSettings()
        setupListeners()
    }

    private fun observeSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe POI notifications setting
                launch {
                    settingsDataStore.notifyPoiFlow.collect { enabled ->
                        binding.switchPoi.isChecked = enabled
                    }
                }

                // Observe reminders setting
                launch {
                    settingsDataStore.notifyRemindersFlow.collect { enabled ->
                        binding.switchReminders.isChecked = enabled
                    }
                }

                // Observe auto tracking setting
                launch {
                    settingsDataStore.autoTrackingFlow.collect { enabled ->
                        binding.switchAutoTracking.isChecked = enabled
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        // Switch listeners
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

        // Export data button
        binding.btnExportData.setOnClickListener {
            exportData()
        }

        // Delete all data button
        binding.btnDeleteData.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun exportData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val exportData = withContext(Dispatchers.IO) {
                    collectExportData()
                }

                val jsonContent = gson.toJson(exportData)
                saveToDownloads(jsonContent)

                Toast.makeText(requireContext(), R.string.export_data_success, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Timber.e(e, "Failed to export data")
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
                // Use MediaStore for Android 10+
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
                // Legacy approach for older Android versions
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
                    // Delete all trips (cascades to related data due to Room foreign keys)
                    val trips = repository.getAllTrips().first()
                    trips.forEach { trip ->
                        repository.deleteTrip(trip)
                    }

                    // Clear settings
                    settingsDataStore.clearAll()
                }

                Toast.makeText(requireContext(), R.string.all_data_deleted, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete data")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Data class for export
    data class ExportData(
        val exportDate: String,
        val appVersion: String,
        val trips: List<com.travelcompanion.domain.model.Trip>,
        val journeys: List<com.travelcompanion.domain.model.Journey>,
        val settings: Map<String, Any>
    )
}


