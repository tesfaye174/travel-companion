package com.travelcompanion.ui.newtrip

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.travelcompanion.databinding.FragmentNewTripBinding
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.ui.tracking.TrackingActivity
import com.travelcompanion.ui.tracking.TrackingService
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewTripFragment : Fragment() {

    private var _binding: FragmentNewTripBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    private val viewModel: NewTripViewModel by viewModels()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupDatePickers()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDatePickers() {
        // Set click listeners on both EditText and TextInputLayout for better touch handling
        binding.etStartDate.setOnClickListener {
            showDatePicker(true)
        }
        binding.tilStartDate.setOnClickListener {
            showDatePicker(true)
        }
        binding.tilStartDate.setEndIconOnClickListener {
            showDatePicker(true)
        }

        binding.etEndDate.setOnClickListener {
            showDatePicker(false)
        }
        binding.tilEndDate.setOnClickListener {
            showDatePicker(false)
        }
        binding.tilEndDate.setEndIconOnClickListener {
            showDatePicker(false)
        }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                if (isStartDate) {
                    binding.etStartDate.setText(dateFormat.format(calendar.time))
                } else {
                    binding.etEndDate.setText(dateFormat.format(calendar.time))
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun setupListeners() {
        binding.btnStartTrip.setOnClickListener {
            val destination = binding.etDestination.text?.toString()?.trim().orEmpty()
            if (destination.isEmpty()) {
                binding.etDestination.error = getString(com.travelcompanion.R.string.destination)
                Snackbar.make(binding.root, "Inserisci una destinazione", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startDateText = binding.etStartDate.text?.toString()?.trim().orEmpty()
            if (startDateText.isEmpty()) {
                Snackbar.make(binding.root, "Seleziona una data di inizio", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startDate = runCatching { dateFormat.parse(startDateText) }.getOrNull()
            if (startDate == null) {
                Snackbar.make(binding.root, "Data di inizio non valida", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val endDateText = binding.etEndDate.text?.toString()?.trim().orEmpty()
            val endDate = runCatching { dateFormat.parse(endDateText) }.getOrNull() ?: startDate

            val tripType = when (binding.chipGroupType.checkedChipId) {
                com.travelcompanion.R.id.chip_local -> TripType.LOCAL
                com.travelcompanion.R.id.chip_day -> TripType.DAY_TRIP
                com.travelcompanion.R.id.chip_multi_day -> TripType.MULTI_DAY
                else -> TripType.LOCAL
            }

            val title = "Trip to $destination"
            viewModel.createTrip(
                title = title,
                destination = destination,
                tripType = tripType,
                startDate = startDate,
                endDate = endDate,
                notes = ""
            )
        }
    }

    private fun observeViewModel() {
        viewModel.createdTripId.observe(viewLifecycleOwner) { id ->
            if (id == null || id <= 0) return@observe

            val intent = Intent(requireContext(), TrackingActivity::class.java).apply {
                putExtra(TrackingService.EXTRA_TRIP_ID, id)
            }
            startActivity(intent)
            viewModel.resetSaveState()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

