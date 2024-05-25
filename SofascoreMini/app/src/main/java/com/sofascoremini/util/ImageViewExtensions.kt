package com.sofascoremini.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sofascoremini.R

fun ImageView.loadImage(url: String, placeholder: Int = R.drawable.loading_spinner) {
    Glide.with(this.context)
        .load(url)
        .placeholder(placeholder)
        .into(this)
}