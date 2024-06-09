package com.sofascoremini.data.remote

import com.sofascoremini.data.models.EventResponse
import com.sofascoremini.data.models.IncidentResponse
import com.sofascoremini.data.models.TournamentStandingsResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiService {
    @GET("sport/{slug}/events/{date}")
    suspend fun getEventsForSportsAndDate(
        @Path("slug") slug: String,
        @Path("date") date: String
    ): List<EventResponse>

    @GET("tournament/{id}/standings")
    suspend fun getTournamentStandings(
        @Path("id") id: Int
    ): List<TournamentStandingsResponse>

    @GET("tournament/{id}/events/{span}/{page}")
    suspend fun getTournamentMatches(
        @Path("id") slug: Int,
        @Path("span") span: String,
        @Path("page") page: Int
    ): List<EventResponse>

    @GET("event/{id}/incidents")
    suspend fun getEventIncidents(
        @Path("id") slug: Int
    ): List<IncidentResponse>

    @GET("event/{id}")
    suspend fun getEventDetails(
        @Path("id") slug: Int
    ): EventResponse
}