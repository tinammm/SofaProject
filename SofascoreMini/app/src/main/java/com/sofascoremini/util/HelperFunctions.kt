package com.sofascoremini.util

import androidx.appcompat.app.AppCompatDelegate

fun setUpAppTheme(theme: String){
    if (theme == "light")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    else
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
}