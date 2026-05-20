package com.travelcompanion.ui.tripdetails


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.travelcompanion.R
import com.travelcompanion.databinding.BottomSheetEditTripBinding
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType

class EditTripDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEditTripBinding? = null
    private val binding get() = _binding!!

    private var trip: Trip? = null
    private var onTripUpdated: ((Trip) -> Unit)? = null

    companion object {
        private const val ARG_TRIP_ID = "trip_id"
        private const val ARG_TRIP_TITLE = "trip_title"
        private const val ARG_TRIP_DESTINATION = "trip_destination"
        private const val ARG_TRIP_TYPE = "trip_type"
        private const val ARG_TRIP_NOTES = "trip_notes"
        private const val ARG_TRIP_START_DATE = "trip_start_date"
        private const val ARG_TRIP_END_DATE = "trip_end_date"
        private const val ARG_TRIP_TOTAL_DISTANCE = "trip_total_distance"
        private const val ARG_TRIP_TOTAL_DURATION = "trip_total_duration"
        private const val ARG_TRIP_PHOTO_COUNT = "trip_photo_count"
        private const val ARG_TRIP_IS_TRACKING = "trip_is_tracking"

        fun newInstance(trip: Trip, onUpdate: (Trip) -> Unit): EditTripDialogFragment {
            return EditTripDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TRIP_ID, trip.id)
                    putString(ARG_TRIP_TITLE, trip.title)
                    putString(ARG_TRIP_DESTINATION, trip.destination)
                    putString(ARG_TRIP_TYPE, trip.tripType.name)
                    putString(ARG_TRIP_NOTES, trip.notes)
                    putLong(ARG_TRIP_START_DATE, trip.startDate.time)
                    trip.endDate?.let { putLong(ARG_TRIP_END_DATE, it.time) }
                    putFloat(ARG_TRIP_TOTAL_DISTANCE, trip.totalDistance)
                    putLong(ARG_TRIP_TOTAL_DURATION, trip.totalDuration)
                    putInt(ARG_TRIP_PHOTO_COUNT, trip.photoCount)
                    putBoolean(ARG_TRIP_IS_TRACKING, trip.isTracking)
                }
                onTripUpdated = onUpdate
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetEditTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            trip = Trip(
                id = args.getLong(ARG_TRIP_ID),
                title = args.getString(ARG_TRIP_TITLE, ""),
                destination = args.getString(ARG_TRIP_DESTINATION, ""),
                tripType = TripType.valueOf(args.getString(ARG_TRIP_TYPE, TripType.LOCAL.name)),
                notes = args.getString(ARG_TRIP_NOTES, ""),
                startDate = java.util.Date(args.getLong(ARG_TRIP_START_DATE)),
                endDate = if (args.containsKey(ARG_TRIP_END_DATE)) java.util.Date(args.getLong(ARG_TRIP_END_DATE)) else null,
                totalDistance = args.getFloat(ARG_TRIP_TOTAL_DISTANCE),
                totalDuration = args.getLong(ARG_TRIP_TOTAL_DURATION),
                photoCount = args.getInt(ARG_TRIP_PHOTO_COUNT),
                isTracking = args.getBoolean(ARG_TRIP_IS_TRACKING)
            )
        }

        populateFields()

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener { saveTrip() }
    }

    private fun populateFields() {
        trip?.let { t ->
            binding.etTitle.setText(t.title)
            binding.etDestination.setText(t.destination)
            binding.etNotes.setText(t.notes)

            when (t.tripType) {
                TripType.LOCAL -> binding.chipLocal.isChecked = true
                TripType.DAY_TRIP -> binding.chipDay.isChecked = true
                TripType.MULTI_DAY -> binding.chipMultiDay.isChecked = true
                TripType.OTHER -> binding.chipLocal.isChecked = true
            }
        }
    }

    private fun saveTrip() {
        val title = binding.etTitle.text?.toString()?.trim()
        val destination = binding.etDestination.text?.toString()?.trim()
        val notes = binding.etNotes.text?.toString()?.trim() ?: ""

        if (title.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.error_saving_trip, Toast.LENGTH_SHORT).show()
            return
        }

        if (destination.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.error_saving_trip, Toast.LENGTH_SHORT).show()
            return
        }

        val tripType = when (binding.chipGroupType.checkedChipId) {
            R.id.chip_local -> TripType.LOCAL
            R.id.chip_day -> TripType.DAY_TRIP
            R.id.chip_multi_day -> TripType.MULTI_DAY
            else -> TripType.LOCAL
        }

        trip?.let { oldTrip ->
            val updatedTrip = oldTrip.copy(
                title = title,
                destination = destination,
                tripType = tripType,
                notes = notes
            )
            onTripUpdated?.invoke(updatedTrip)
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
