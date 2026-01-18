class TripsAdapter(
    private val onTripClick: (Trip) -> Unit
) : RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    private var trips = emptyList<Trip>()

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textTripTitle)
        val dates: TextView = itemView.findViewById(R.id.textTripDates)
        val type: TextView = itemView.findViewById(R.id.textTripType)
        val destination: TextView = itemView.findViewById(R.id.textTripDestination)
        val duration: TextView = itemView.findViewById(R.id.textTripDuration)
        val photos: TextView = itemView.findViewById(R.id.textTripPhotos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]

        holder.title.text = trip.title
        holder.dates.text = formatDates(trip.startDate, trip.endDate)
        holder.type.text = getTripTypeString(trip.tripType)
        holder.destination.text = trip.destination
        holder.duration.text = formatDuration(trip.totalDuration)
        holder.photos.text = "${trip.photoCount} foto"

        holder.itemView.setOnClickListener {
            onTripClick(trip)
        }
    }

    override fun getItemCount(): Int = trips.size

    fun setTrips(trips: List<Trip>) {
        this.trips = trips
        notifyDataSetChanged()
    }

    private fun formatDates(start: Long, end: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.ITALIAN)
        return "${dateFormat.format(Date(start))} – ${dateFormat.format(Date(end))} ${SimpleDateFormat("yyyy", Locale.ITALIAN).format(Date(end))}"
    }

    private fun formatDuration(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
        return "${hours}h ${minutes}min"
    }

    private fun getTripTypeString(type: TripType): String = when(type) {
        TripType.LOCAL -> "Locale"
        TripType.DAY_TRIP -> "Giornaliero"
        TripType.MULTI_DAY -> "Multi-giorno"
    }
}