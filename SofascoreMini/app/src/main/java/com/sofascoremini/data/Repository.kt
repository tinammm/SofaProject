package com.sofascoremini.data


import com.sofascoremini.data.remote.Network
import com.sofascoremini.util.safeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {
    private val api = Network.getInstance()

    suspend fun getEventsForSportsAndDate(slug: String, date: String) =
        withContext(Dispatchers.IO) {
            safeResponse {
                api.getEventsForSportsAndDate(slug, date)
            }
        }

}