package com.sofascoremini.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EventResponse(
    val id: Int,
    val tournament: Tournament,
    val homeTeam: Team,
    val awayTeam: Team,
    val status: Status,
    val startDate: String?,
    val homeScore: Score,
    val awayScore: Score,
    val winnerCode: WinnerCode?,
    val round: Int
) : Serializable

enum class Status(val status: String) {
    @SerializedName("finished")
    FINISHED(status = "finished"),
    @SerializedName("inprogress")
    IN_PROGRESS(status = "inprogress"),
    @SerializedName("notstarted")
    NOT_STARTED(status = "notstarted")
}

enum class WinnerCode(val code: String) {
    @SerializedName("home")
    HOME(code = "home"),

    @SerializedName("away")
    AWAY(code = "away"),

    @SerializedName("draw")
    DRAW(code = "draw")
}

data class Country(
    val id: Int,
    val name: String
) : Serializable

data class Score(
    val total: Int?,
    val period1: Int?,
    val period2: Int?,
    val period3: Int?,
    val period4: Int?,
    val overtime: Int?
)

data class Sport(
    val id: Int,
    val name: String,
    val slug: SportSlug
) : Serializable

data class Tournament(
    val id: Int,
    val name: String,
    val sport: Sport,
    val country: Country
) : Serializable

data class Team(
    val id: Int,
    val name: String
)