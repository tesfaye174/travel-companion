package com.example.travelcompanion.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelcompanion.R
import com.example.travelcompanion.TravelCompanionApplication
import com.example.travelcompanion.databinding.FragmentTripsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripsViewModel by viewModels {
        TripsViewModelFactory((requireActivity().application as TravelCompanionApplication).repository)
    }

    private lateinit var adapter: TripsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilterButtons()
        observeTrips()

        binding.fabNewTrip.setOnClickListener {
            findNavController().navigate(R.id.action_trips_to_newTrip)
        }
    }

    private fun setupRecyclerView() {
        adapter = TripsAdapter(
            onTripClick = { trip ->
                val action = TripsFragmentDirections.actionTripsToTripDetails(trip.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { trip ->
                viewModel.deleteTrip(trip)
            }
        )

        binding.recyclerTrips.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TripsFragment.adapter
        }
    }

    private fun setupFilterButtons() {
        binding.chipAll.setOnClickListener {
            viewModel.setFilter(TripFilter.ALL)
        }

        binding.chipUpcoming.setOnClickListener {
            viewModel.setFilter(TripFilter.UPCOMING)
        }

        binding.chipPast.setOnClickListener {
            viewModel.setFilter(TripFilter.PAST)
        }
    }

    private fun observeTrips() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedFilter.observe(viewLifecycleOwner) { filter ->
                when (filter) {
                    TripFilter.ALL -> collectTrips(viewModel.trips)
                    TripFilter.UPCOMING -> collectTrips(viewModel.upcomingTrips)
                    TripFilter.PAST -> collectTrips(viewModel.pastTrips)
                    else -> collectTrips(viewModel.trips)
                }
            }
        }
    }

    private fun collectTrips(flow: kotlinx.coroutines.flow.StateFlow<List<com.example.travelcompanion.domain.model.Trip>>) {
        viewLifecycleOwner.lifecycleScope.launch {
            flow.collectLatest { trips ->
                adapter.submitList(trips)

                if (trips.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerTrips.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerTrips.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}