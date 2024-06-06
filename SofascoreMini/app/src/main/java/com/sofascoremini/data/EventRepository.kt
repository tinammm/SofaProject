package com.sofascoremini.data

import com.sofascoremini.data.remote.ApiService
import com.sofascoremini.util.safeResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getEventIncidents(id: Int){
        safeResponse {
            api.getEventIncidents(id)
        }
    }
}