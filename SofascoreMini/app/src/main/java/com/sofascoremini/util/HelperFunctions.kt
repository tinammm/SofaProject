package com.sofascoremini.util

import android.content.Context
import android.util.TypedValue
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

fun setUpAppTheme(theme: String) {
    if (theme == "light")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    else
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
}


fun extractHourMinute(startDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = inputFormat.parse(startDate)
    return outputFormat.format(date ?: Date())
}

fun calculateMinutesPassed(startDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
    val date = inputFormat.parse(startDate)
    val currentTime = Date()
    val minutesPassed = (currentTime.time - (date?.time ?: Date().time))/(1000 * 60).toDouble()
    return (minutesPassed.roundToInt().toString() + "'")
}

fun getColorFromAttribute(context: Context, attrColor: Int) : Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attrColor, typedValue, true)
    return ContextCompat.getColor(context, typedValue.resourceId)
}

fun offsetToDate(offset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, offset)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(calendar.time)
}


fun offsetToDateHeader(offset: Int): String {
    return if (offset == 0) {
        "Today"
    } else {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, offset)

        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dayOfWeek = dayFormat.format(calendar.time)

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = dateFormat.format(calendar.time)

        "$dayOfWeek, $date"
    }
}
