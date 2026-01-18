package com.travelcompanion.ui.newtrip

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.travelcompanion.databinding.FragmentNewTripBinding
import java.text.SimpleDateFormat
import java.util.*

class NewTripFragment : Fragment() {

    private var _binding: FragmentNewTripBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

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

        setupDatePickers()
        setupListeners()
    }

    private fun setupDatePickers() {
        binding.etStartDate.setOnClickListener {
            showDatePicker(true)
        }

        binding.etEndDate.setOnClickListener {
            showDatePicker(false)
        }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
            // TODO: Save trip and start tracking
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}