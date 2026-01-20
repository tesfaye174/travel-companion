package com.travelcompanion.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentTripsBinding
import com.travelcompanion.domain.model.TripType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TripViewModel by viewModels()
    private lateinit var tripAdapter: TripsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChips()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        tripAdapter = TripsAdapter { trip ->
            val bundle = Bundle().apply {
                putLong("tripId", trip.id)
            }
             findNavController().navigate(R.id.navigation_trip_details, bundle)
        }
        
        binding.rvTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripAdapter
        }
    }

    private fun setupChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, _ ->
            val type = when (group.checkedChipId) {
                R.id.chip_local -> TripType.LOCAL
                R.id.chip_day -> TripType.DAY_TRIP
                R.id.chip_multi_day -> TripType.MULTI_DAY
                else -> null
            }
            viewModel.setFilterType(type)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.setSearchQuery(text?.toString().orEmpty())
        }
    }

    private fun observeViewModel() {
        viewModel.allTrips.observe(viewLifecycleOwner) { trips ->
            tripAdapter.submitList(trips)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
