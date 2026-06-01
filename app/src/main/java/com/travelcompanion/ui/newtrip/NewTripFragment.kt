package com.travelcompanion.ui.newtrip

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentNewTripBinding
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.service.TrackingService
import com.travelcompanion.ui.tracking.TrackingActivity
import com.travelcompanion.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

// Fragment for creating new trips
@AndroidEntryPoint
class NewTripFragment : Fragment() {

    private var _binding: FragmentNewTripBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()
    private var datePickerDialog: DatePickerDialog? = null

    private val viewModel: NewTripViewModel by viewModels()

    // Uses DateUtils for date formatting for consistency

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
        prefillFromArgs()
        observeViewModel()
    }

    private fun prefillFromArgs() {
        val preset = arguments?.getString("destination").orEmpty()
        if (preset.isNotBlank()) {
            binding.etDestination.setText(preset)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDatePickers() {
        // One listener per field (end-icon on the TextInputLayout). Multiple listeners
        // could trigger two dialogs on a fast double-tap.
        binding.etStartDate.setOnClickListener { showDatePicker(true) }
        binding.tilStartDate.setEndIconOnClickListener { showDatePicker(true) }

        binding.etEndDate.setOnClickListener { showDatePicker(false) }
        binding.tilEndDate.setEndIconOnClickListener { showDatePicker(false) }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        // Avoid stacking dialogs if the user taps twice
        if (datePickerDialog?.isShowing == true) return

        datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                val formatted = DateUtils.formatDate(calendar.time)
                if (isStartDate) binding.etStartDate.setText(formatted)
                else binding.etEndDate.setText(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).also { it.show() }
    }

    private fun setupListeners() {
        binding.btnStartTrip.setOnClickListener {
            val destination = binding.etDestination.text?.toString()?.trim().orEmpty()
            if (destination.isEmpty()) {
                binding.etDestination.error = getString(R.string.enter_destination)
                return@setOnClickListener
            }

            val startDateText = binding.etStartDate.text?.toString()?.trim().orEmpty()
            if (startDateText.isEmpty()) {
                Snackbar.make(binding.root, getString(R.string.select_start_date), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startDate = runCatching { DateUtils.dateFormat.parse(startDateText) }.getOrNull()
            if (startDate == null) {
                Snackbar.make(binding.root, getString(R.string.invalid_start_date), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val endDateText = binding.etEndDate.text?.toString()?.trim().orEmpty()
            val endDate = runCatching { DateUtils.dateFormat.parse(endDateText) }.getOrNull() ?: startDate

            val tripType = when (binding.chipGroupType.checkedChipId) {
                R.id.chip_local -> TripType.LOCAL
                R.id.chip_day -> TripType.DAY_TRIP
                R.id.chip_multi_day -> TripType.MULTI_DAY
                else -> TripType.LOCAL
            }

            // Multi-day trips require an explicit end date that's after the start
            if (tripType == TripType.MULTI_DAY) {
                if (endDateText.isEmpty()) {
                    Snackbar.make(binding.root, getString(R.string.select_end_date), Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (endDate.before(startDate)) {
                    Snackbar.make(binding.root, getString(R.string.end_date_before_start), Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val title = getString(R.string.trip_to_destination, destination)
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.createdTripId.collect { id ->
                        if (id <= 0) return@collect
                        val intent = Intent(requireContext(), TrackingActivity::class.java).apply {
                            putExtra(TrackingService.EXTRA_TRIP_ID, id)
                        }
                        startActivity(intent)
                        viewModel.resetSaveState()
                        // Pop NewTripFragment so backing out of TrackingActivity returns to Home, not to this filled-in form
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        datePickerDialog?.dismiss()
        datePickerDialog = null
        _binding = null
    }
}
