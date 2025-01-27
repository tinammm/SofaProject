package com.sofascoremini.data


import com.sofascoremini.data.remote.ApiService
import com.sofascoremini.util.safeResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainListRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getEventsForSportsAndDate(slug: String, date: String) =
        safeResponse {
            api.getEventsForSportsAndDate(slug, date)
        }
}