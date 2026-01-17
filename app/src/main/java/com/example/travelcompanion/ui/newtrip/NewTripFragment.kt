package com.example.travelcompanion.ui.newtrip

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.travelcompanion.R
import com.example.travelcompanion.TravelCompanionApplication
import com.example.travelcompanion.databinding.FragmentNewTripBinding
import com.example.travelcompanion.domain.model.TripType
import com.example.travelcompanion.utils.DateUtils
import java.util.*

class NewTripFragment : Fragment() {

    private var _binding: FragmentNewTripBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewTripViewModel by viewModels {
        NewTripViewModelFactory((requireActivity().application as TravelCompanionApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTripTypeSpinner()
        setupDatePickers()
        setupObservers()

        binding.buttonCreateTrip.setOnClickListener {
            createTrip()
        }
    }

    private fun setupTripTypeSpinner() {
        val tripTypes = TripType.values().map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tripTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTripType.adapter = adapter
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        binding.textStartDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    viewModel.setStartDate(calendar.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.textEndDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    viewModel.setEndDate(calendar.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupObservers() {
        viewModel.startDate.observe(viewLifecycleOwner) { date ->
            binding.textStartDate.text = DateUtils.formatDate(date)
        }

        viewModel.endDate.observe(viewLifecycleOwner) { date ->
            binding.textEndDate.text = DateUtils.formatDate(date)
        }

        viewModel.tripCreated.observe(viewLifecycleOwner) { tripId ->
            tripId?.let {
                findNavController().navigateUp()
            }
        }
    }

    private fun createTrip() {
        val title = binding.editTripTitle.text.toString()
        val destination = binding.editDestination.text.toString()

        if (title.isBlank() || destination.isBlank()) {
            return
        }

        viewModel.setDestination(destination)
        viewModel.setTripType(TripType.values()[binding.spinnerTripType.selectedItemPosition])
        viewModel.setNotes(binding.editNotes.text.toString())
        viewModel.createTrip(title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
