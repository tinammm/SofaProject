package com.sofascoremini.ui.main.adapters

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.sofascoremini.databinding.DateTabItemBinding
import com.sofascoremini.ui.settings.DATE
import com.sofascoremini.util.formatDate
import com.sofascoremini.util.formatDay
import java.time.LocalDate
import java.util.Locale

class DatesAdapter(
    private val dates: List<LocalDate>,
    private val context: Context,
    private val onDateSelected: (LocalDate) -> Unit
) :
    RecyclerView.Adapter<DatesAdapter.DateItemViewHolder>() {
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    var selectedPosition = dates.size / 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateItemViewHolder {
        return DateItemViewHolder(
            DateTabItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DateItemViewHolder, position: Int) {
        holder.bind(dates[position])
        holder.itemView.setOnClickListener {
            val previousSelection = selectedPosition
            onDateSelected(dates[position])
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(previousSelection)
            notifyItemChanged(selectedPosition)
        }

    }

    override fun getItemCount(): Int = dates.size

    inner class DateItemViewHolder(private val binding: DateTabItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(date: LocalDate) {
            val format = preferences.getString(DATE, "DD / MM / YYYY") ?: "DD / MM / YYYY"
            binding.root.isSelected = bindingAdapterPosition == selectedPosition
            binding.date.text = formatDate(date, format)
            binding.dayOfWeek.text = if (date == LocalDate.now()) {
                "TODAY"
            } else {
                formatDay(date, "EEE").uppercase(Locale.getDefault())
            }
            binding.date.apply {
                setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            }
            binding.dayOfWeek.apply {
                setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            }
        }
    }
}