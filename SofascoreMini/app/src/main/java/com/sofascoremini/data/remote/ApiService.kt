package com.sofascoremini.data.remote

import com.sofascoremini.data.models.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiService {
    @GET("sport/{slug}/events/{date}")
    suspend fun getEventsForSportsAndDate(
        @Path("slug") slug: String,
        @Path("date") date: String
    ): List<EventResponse>

}