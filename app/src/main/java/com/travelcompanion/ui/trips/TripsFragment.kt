package com.travelcompanion.ui.trips

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentTripsBinding
import com.travelcompanion.domain.model.Trip
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
        setupSwipeToDelete()
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

    private fun setupSwipeToDelete() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private val deleteBackground = ColorDrawable(Color.parseColor("#F44336"))
            private val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return
                val trip = tripAdapter.currentList[position]
                showDeleteConfirmation(trip, position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - (deleteIcon?.intrinsicHeight ?: 0)) / 2

                if (dX < 0) {
                    // Swiping left
                    deleteBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteBackground.draw(c)

                    deleteIcon?.let { icon ->
                        icon.setTint(Color.WHITE)
                        val iconTop = itemView.top + iconMargin
                        val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        val iconBottom = iconTop + icon.intrinsicHeight
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        icon.draw(c)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvTrips)
    }

    private fun showDeleteConfirmation(trip: Trip, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_trip_title)
            .setMessage(getString(R.string.delete_trip_message, trip.title))
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteTrip(trip)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // Restore the item in the adapter
                tripAdapter.notifyItemChanged(position)
            }
            .setOnCancelListener {
                // Restore the item if dialog is cancelled
                tripAdapter.notifyItemChanged(position)
            }
            .show()
    }

    private fun deleteTrip(trip: Trip) {
        viewModel.deleteTrip(trip)
        Snackbar.make(binding.root, R.string.trip_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                viewModel.insertTrip(trip)
            }
            .show()
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

