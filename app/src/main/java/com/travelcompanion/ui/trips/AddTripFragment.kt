package com.travelcompanion.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.travelcompanion.TravelCompanionApplication
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentAddTripBinding
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType

class AddTripFragment : Fragment() {
    private var _binding: FragmentAddTripBinding? = null
    private val binding get() = _binding!!

    // Placeholder: remove ViewModel dependency until repository wiring is complete

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveTrip.setOnClickListener {
            val destination = binding.etDestination.text.toString()
            // Simplified: ignore radio selection mapping for now
            if (destination.isNotBlank()) {
                com.google.android.material.snackbar.Snackbar.make(view, "Trip saved (placeholder)", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

