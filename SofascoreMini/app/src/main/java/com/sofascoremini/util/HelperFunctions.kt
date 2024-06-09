package com.sofascoremini.util

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sofascoremini.data.models.JsonCountry
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

fun setUpAppTheme(theme: String) {
    if (theme == "light")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    else
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
}

@RequiresApi(Build.VERSION_CODES.O)
fun extractDate(startDate: String, dateFormatPattern: String): String {
    val zonedDateTime = ZonedDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)
    val localDate = zonedDateTime.toLocalDate()
    val outputFormatter = DateTimeFormatter.ofPattern(
        datePreferenceMapper(dateFormatPattern) + ".YYYY",
        Locale.getDefault()
    )
    return localDate.format(outputFormatter)
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
    val minutesPassed = (currentTime.time - (date?.time ?: Date().time)) / (1000 * 60).toDouble()
    return (minutesPassed.roundToInt().toString() + "'")
}

fun getColorFromAttribute(context: Context, attrColor: Int): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attrColor, typedValue, true)
    return ContextCompat.getColor(context, typedValue.resourceId)
}


fun setTextColor(color: Int, vararg views: TextView) {
    views.forEach { it.setTextColor(color) }
}

fun Float.round(decimals: Int = 2): Float = "%.${decimals}f".format(this).toFloat()

fun setUpVisibility(isVisible: Boolean, vararg views: View) {
    views.forEach { it.visibility = if (isVisible) View.VISIBLE else View.GONE}
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateDateRange(): List<LocalDate> {
    val today = LocalDate.now()
    val dates = mutableListOf<LocalDate>()
    for (i in -7..7) {
        dates.add(today.plusDays(i.toLong()))
    }
    return dates
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(date: LocalDate, dateFormatPattern: String): String {
    val dateFormat =
        DateTimeFormatter.ofPattern(datePreferenceMapper(dateFormatPattern), Locale.getDefault())
    return date.format(dateFormat)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDay(date: LocalDate, dayFormatPattern: String): String {
    val dayFormat = DateTimeFormatter.ofPattern(dayFormatPattern, Locale.getDefault())
    return date.format(dayFormat)
}

fun datePreferenceMapper(format: String): String {
    return if (format == "DD / MM / YYYY") "dd.MM"
    else "MM.dd"
}

fun readJsonFromAssets(context: Context, fileName: String): String {
    return context.assets.open(fileName).bufferedReader().use { it.readText() }
}

fun parseJsonToModel(jsonString: String): List<JsonCountry> {
    val gson = Gson()
    return gson.fromJson(jsonString, object : TypeToken<List<JsonCountry>>() {}.type)
}
