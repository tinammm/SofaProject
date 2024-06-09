package com.sofascoremini.data

import com.sofascoremini.data.models.SportSlug
import com.sofascoremini.data.remote.ApiService
import com.sofascoremini.util.safeResponse
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class LeaguesRepository @Inject constructor(
    private val api: ApiService
){
    suspend fun getTournamentsForSport(slug: SportSlug) = safeResponse {
        api.getTournamentsForSport(slug.slug)
    }
}