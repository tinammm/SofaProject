package com.sofascoremini.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sofascoremini.R
import com.sofascoremini.data.remote.Network.provideBaseUrl


fun ImageView.loadTeamImage(id: Int){
    Glide.with(this.context)
        .load("${provideBaseUrl()}team/$id/image")
        .placeholder(R.drawable.loading_spinner)
        .into(this)
}

fun ImageView.loadTournamentImage(id: Int){
    Glide.with(this.context)
        .load("${provideBaseUrl()}tournament/$id/image")
        .placeholder(R.drawable.loading_spinner)
        .into(this)
}

fun ImageView.loadCountryImage(id: String?){
    Glide.with(this.context)
        .load("https://flagcdn.com/w160/${id}.png")
        .placeholder(R.drawable.loading_spinner)
        .into(this)

}