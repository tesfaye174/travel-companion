package com.example.travelcompanion.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.travelcompanion.TravelCompanionApplication
import com.example.travelcompanion.R
import com.example.travelcompanion.databinding.FragmentAddTripBinding
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.model.TripType

class AddTripFragment : Fragment() {
    private var _binding: FragmentAddTripBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripViewModel by viewModels {
        TripViewModelFactory((requireActivity().application as TravelCompanionApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
            val destination = binding.editDestination.text.toString()
            val type = when (binding.radioGroupType.checkedRadioButtonId) {
                R.id.radio_local -> TripType.LOCAL
                R.id.radio_day -> TripType.DAY
                else -> TripType.MULTI_DAY
            }

            if (destination.isNotBlank()) {
                val trip = Trip(
                    destination = destination,
                    startDate = System.currentTimeMillis(),
                    endDate = null,
                    type = type
                )
                viewModel.insertTrip(trip)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
