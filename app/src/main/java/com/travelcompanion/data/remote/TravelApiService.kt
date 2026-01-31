package com.travelcompanion.data.remote

import retrofit2.http.GET

interface TravelApiService {
    @GET("destinations/popular")
    suspend fun getPopularDestinations(): List<PlaceDto>
}
