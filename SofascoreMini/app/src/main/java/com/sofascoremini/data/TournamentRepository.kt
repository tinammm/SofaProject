package com.sofascoremini.data

import com.sofascoremini.data.models.Span
import com.sofascoremini.data.remote.ApiService
import com.sofascoremini.util.safeResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TournamentRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getTournamentStandings(id: Int) =
        safeResponse {
            api.getTournamentStandings(id)
        }

    suspend fun getTournamentMatches(id: Int, span: Span, page: Int) =
        safeResponse {
            api.getTournamentMatches(id, span.span, page)
        }
}