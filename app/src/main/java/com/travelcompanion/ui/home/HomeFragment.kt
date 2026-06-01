package com.travelcompanion.ui.home

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.travelcompanion.R
import com.travelcompanion.core.ui.UiState
import com.travelcompanion.core.ui.safeNavigate
import com.travelcompanion.databinding.FragmentHomeBinding
import com.travelcompanion.ui.trips.TripsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var destinationsAdapter: DestinationsAdapter
    private lateinit var tripsAdapter: TripsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWelcomeGreeting()
        setupSwipeRefresh()
        setupDestinationsRecyclerView()
        setupTripsRecyclerView()
        setupClickListeners()
        setupBackPressHandler()
        observeViewModel()
    }

    private var lastBackPressMillis: Long = 0L
    private fun setupBackPressHandler() {
        // Require a confirming second back-press within 2s to exit from Home (start destination).
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val now = SystemClock.elapsedRealtime()
                    if (now - lastBackPressMillis < 2000L) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    } else {
                        lastBackPressMillis = now
                        Snackbar.make(binding.root, R.string.press_back_again_to_exit, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    private fun setupWelcomeGreeting() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userName.collect { name ->
                    if (name.isNotBlank()) {
                        binding.tvHeroTitle.text = getString(R.string.ready_for_adventure_name, name)
                    }
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.primary),
            ContextCompat.getColor(requireContext(), R.color.secondary)
        )
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupDestinationsRecyclerView() {
        destinationsAdapter = DestinationsAdapter { destination ->
            val bundle = Bundle().apply {
                putString("destination", "${destination.city}, ${destination.country}")
            }
            findNavController().safeNavigate(R.id.action_home_to_new_trip, bundle)
        }

        binding.rvDestinations.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = destinationsAdapter
            setHasFixedSize(true)
        }

        destinationsAdapter.submitList(SuggestedDestinations.destinations)
    }

    private fun setupTripsRecyclerView() {
        tripsAdapter = TripsAdapter(
            onTripClick = { trip ->
                val bundle = Bundle().apply {
                    putLong("tripId", trip.id)
                }
                findNavController().safeNavigate(R.id.action_home_to_trip_details, bundle)
            },
            onTripLongClick = { _ -> }
        )

        binding.rvRecentTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripsAdapter
            // Item size is bounded (max 3 items) so fixed-size optimization is valid
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTrip.setOnClickListener {
            findNavController().safeNavigate(R.id.action_home_to_new_trip)
        }

        binding.btnStartTrip.setOnClickListener {
            findNavController().safeNavigate(R.id.action_home_to_new_trip)
        }

        binding.btnMyTrips.setOnClickListener {
            findNavController().safeNavigate(R.id.action_home_to_trips)
        }

        binding.btnExplore.setOnClickListener {
            findNavController().safeNavigate(R.id.action_home_to_map)
        }

        binding.layoutEmptyState.btnEmptyAction.setOnClickListener {
            findNavController().safeNavigate(R.id.action_home_to_new_trip)
        }

        binding.ivSearch.setOnClickListener {
            findNavController().safeNavigate(R.id.action_home_to_trips)
        }

        binding.ivMenu.setOnClickListener {
            findNavController().safeNavigate(R.id.navigation_profile)
        }

        binding.btnSeeAllTrips.setOnClickListener {
            findNavController().safeNavigate(R.id.action_home_to_trips)
        }

        binding.btnSeeAllDestinations.setOnClickListener {
            findNavController().safeNavigate(R.id.action_home_to_map)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.quickStatsState.collect { state ->
                        when (state) {
                            is UiState.Idle -> {
                                binding.tvTotalTrips.text =
                                    resources.getQuantityString(R.plurals.trips_count_plural, 0, 0)
                                binding.tvTotalDistance.text = getString(R.string.total_distance_val, 0f)
                            }
                            is UiState.Success -> {
                                val stats = state.data
                                binding.tvTotalTrips.text =
                                    resources.getQuantityString(R.plurals.trips_count_plural, stats.totalTrips, stats.totalTrips)
                                binding.tvTotalDistance.text = getString(R.string.total_distance_val, stats.totalDistance)
                            }
                            is UiState.Loading -> { /* keep current values visible */ }
                            is UiState.Error -> {
                                binding.tvTotalTrips.text =
                                    resources.getQuantityString(R.plurals.trips_count_plural, 0, 0)
                                binding.tvTotalDistance.text = getString(R.string.total_distance_val, 0f)
                            }
                        }
                    }
                }

                launch {
                    viewModel.recentTripsState.collect { state ->
                        when (state) {
                            is UiState.Idle -> { /* initial state */ }
                            is UiState.Success -> {
                                binding.swipeRefresh.isRefreshing = false
                                binding.shimmerTrips.stopShimmer()
                                binding.shimmerTrips.visibility = View.GONE

                                val trips = state.data
                                if (trips.isEmpty()) {
                                    binding.rvRecentTrips.visibility = View.GONE
                                    binding.layoutEmptyState.root.visibility = View.VISIBLE
                                } else {
                                    binding.rvRecentTrips.visibility = View.VISIBLE
                                    binding.layoutEmptyState.root.visibility = View.GONE
                                    tripsAdapter.submitList(trips)
                                }
                            }
                            is UiState.Loading -> {
                                binding.rvRecentTrips.visibility = View.GONE
                                binding.layoutEmptyState.root.visibility = View.GONE
                                binding.shimmerTrips.visibility = View.VISIBLE
                                binding.shimmerTrips.startShimmer()
                            }
                            is UiState.Error -> {
                                binding.swipeRefresh.isRefreshing = false
                                binding.shimmerTrips.stopShimmer()
                                binding.shimmerTrips.visibility = View.GONE
                                binding.rvRecentTrips.visibility = View.GONE
                                binding.layoutEmptyState.root.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
