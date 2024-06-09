package com.sofascoremini.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class IncidentResponse(
    val id: Int,
    val player: Player?,
    val teamSide: Side?,
    val scoringTeam: Side?,
    val homeScore: Int?,
    val awayScore: Int?,
    val goalType: GoalType?,
    val color: IncidentColor?,
    val text: String?,
    val time: Int,
    val type: IncidentType
) : Serializable

enum class Side(val side: String){
    @SerializedName("home")
    HOME("home"),
    @SerializedName("away")
    AWAY("away")
}

enum class GoalType(val type: String) {
    @SerializedName("regular")
    REGULAR("regular"),
    @SerializedName("owngoal")
    OWN_GOAL("owngoal"),
    @SerializedName("penalty")
    PENALTY("penalty"),
    @SerializedName("onepoint")
    ONE_POINT("onepoint"),
    @SerializedName("twopoint")
    TWO_POINT("twopoint"),
    @SerializedName("threepoint")
    THREE_POINT("threepoint"),
    @SerializedName("touchdown")
    TOUCHDOWN("touchdown"),
    @SerializedName("safety")
    SAFETY("safety"),
    @SerializedName("fieldgoal")
    FIELD_GOAL("fieldgoal"),
    @SerializedName("extrapoint")
    EXTRA_POINT("extrapoint");
}

enum class IncidentType(val type: String){
    @SerializedName("card")
    CARD("card"),
    @SerializedName("goal")
    GOAL("goal"),
    @SerializedName("period")
    PERIOD("period")
}

enum class IncidentColor(val color: String){
    @SerializedName("yellow")
    YELLOW("yellow"),
    @SerializedName("yellowred")
    YELLOW_RED("yellowred"),
    @SerializedName("red")
    RED("red")
}

data class Player(
    val id: Int,
    val name: String,
    val slug: String,
    val country: Country,
    val position: String
)