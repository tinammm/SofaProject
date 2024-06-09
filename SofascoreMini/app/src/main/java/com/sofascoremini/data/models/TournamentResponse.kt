package com.sofascoremini.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TournamentStandingsResponse(
    val id: Int,
    val tournament: Tournament,
    val type: Type,
    val sortedStandingsRows: List<Standings>
) : Serializable

data class Standings(
    val id: Int,
    val team: TeamDetails,
    val points: Int?,
    val scoresFor: Int,
    val scoresAgainst: Int,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val percentage: Float?
)

data class TeamDetails(
    val id: Int,
    val name: String,
    val country: Country
)

enum class SportSlug(val slug: String, val text: String) {
    @SerializedName("football")
    FOOTBALL("football", "Football"),

    @SerializedName("basketball")
    BASKETBALL("basketball", "Basketball"),

    @SerializedName("american-football")
    AM_FOOTBALL("american-football", "Am. Football")
}

enum class Type(val type: String) {
    @SerializedName("home")
    HOME("home"),

    @SerializedName("away")
    AWAY("away"),

    @SerializedName("total")
    TOTAL("total")
}

enum class Span(val span: String) {
    LAST("last"),
    NEXT("next")
}
