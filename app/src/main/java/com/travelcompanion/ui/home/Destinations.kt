package com.travelcompanion.ui.home

// Data model per le destinazioni e lista suggerita
data class Destination(
    val id: Int,
    val city: String,
    val country: String,
    // URL opzionale per immagine remota (ha priorità su imageResName)
    val imageUrl: String? = null,
    // nome opzionale della risorsa drawable (es. "colosseum"); se null si usa il placeholder
    val imageResName: String? = null
)

object SuggestedDestinations {
    val destinations = listOf(
        Destination(
            id = 1,
            city = "New York",
            country = "USA",
            imageResName = null // usare placeholder
        ),
        Destination(
            id = 2,
            city = "Paris",
            country = "France",
            imageResName = null
        ),
        Destination(
            id = 3,
            city = "Torino",
            country = "Italia",
            imageResName = null
        ),
        Destination(
            id = 4,
            city = "Bologna",
            country = "Italia",
            imageResName = null
        ),
        Destination(
            id = 5,
            city = "Madrid",
            country = "Spain",
            imageResName = null
        ),
        Destination(
            id = 6,
            city = "Rome",
            country = "Italia",
            imageResName = "colosseum" // risorsa esistente
        ),
        Destination(
            id = 7,
            city = "London",
            country = "UK",
            imageResName = null
        ),
        Destination(
            id = 8,
            city = "Barcelona",
            country = "Spain",
            imageResName = null
        )
    )
}
