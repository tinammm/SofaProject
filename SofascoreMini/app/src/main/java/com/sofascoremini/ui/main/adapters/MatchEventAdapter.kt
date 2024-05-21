package com.sofascoremini.ui.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sofascoremini.R
import com.sofascoremini.data.models.EventResponse
import com.sofascoremini.data.models.Status
import com.sofascoremini.data.models.Tournament
import com.sofascoremini.data.models.WinnerCode
import com.sofascoremini.databinding.MatchCellItemBinding
import com.sofascoremini.databinding.TournamentHeaderItemBinding
import com.sofascoremini.util.*

sealed class MatchEventItem {
    data class EventItem(val event: EventResponse) : MatchEventItem()
    data class Header(val tournament: Tournament) : MatchEventItem()
}

class MatchEventAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = emptyList<MatchEventItem>()

    companion object {
        private const val VIEW_TYPE_TOURNAMENT = 0
        private const val VIEW_TYPE_EVENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MatchEventItem.EventItem -> VIEW_TYPE_EVENT
            is MatchEventItem.Header -> VIEW_TYPE_TOURNAMENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_TOURNAMENT -> HeaderItemViewHolder(
                TournamentHeaderItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_EVENT -> EventItemViewHolder(
                MatchCellItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items[position] is MatchEventItem.EventItem) {
            (holder as EventItemViewHolder).bind(items[position] as MatchEventItem.EventItem)
        } else {
            (holder as HeaderItemViewHolder).bind(items[position] as MatchEventItem.Header)
        }
    }


    class EventItemViewHolder(val binding: MatchCellItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "ResourceType")
        fun bind(eventItem: MatchEventItem.EventItem) {

            binding.startTime.text = extractHourMinute(eventItem.event.startDate.toString())
            binding.homeTeamName.text = eventItem.event.homeTeam.name
            Glide.with(binding.root.context)
                .load("https://academy-backend.sofascore.dev/team/${eventItem.event.homeTeam.id}/image")
                .placeholder(R.drawable.loading_spinner)
                .into(binding.homeTeamLogo)
            binding.awayTeamName.text = eventItem.event.awayTeam.name
            Glide.with(binding.root.context)
                .load("https://academy-backend.sofascore.dev/team/${eventItem.event.awayTeam.id}/image")
                .placeholder(R.drawable.loading_spinner)
                .into(binding.awayTeamLogo)
            binding.scoreHomeTeam.text = if (eventItem.event.homeScore.total != null) {
                eventItem.event.homeScore.total.toString()
            } else ""

            binding.scoreAwayTeam.text = if (eventItem.event.awayScore.total != null) {
                eventItem.event.awayScore.total.toString()
            } else ""

            when (eventItem.event.status) {
                Status.FINISHED -> {
                    binding.status.text = "FT"
                    val color = getColorFromAttribute(binding.root.context, R.attr.n_lv_2)
                    when (eventItem.event.winnerCode) {
                        WinnerCode.HOME -> {
                            binding.scoreAwayTeam.setTextColor(color)
                            binding.awayTeamName.setTextColor(color)
                        }

                        WinnerCode.AWAY -> {
                            binding.scoreHomeTeam.setTextColor(color)
                            binding.homeTeamName.setTextColor(color)
                        }

                        else -> {
                            binding.scoreHomeTeam.setTextColor(color)
                            binding.homeTeamName.setTextColor(color)
                            binding.scoreAwayTeam.setTextColor(color)
                            binding.awayTeamName.setTextColor(color)
                        }
                    }
                }

                Status.NOT_STARTED -> {
                    binding.status.text = "-"
                }

                Status.IN_PROGRESS -> {
                    binding.status.let {
                        it.text = calculateMinutesPassed(
                            eventItem.event.startDate.toString()
                        )
                        it.setTextColor(
                            getColorFromAttribute(
                                binding.root.context,
                                R.attr.colorTertiary
                            )
                        )
                    }

                    binding.scoreHomeTeam.setTextColor(
                        getColorFromAttribute(
                            binding.root.context,
                            R.attr.colorTertiary
                        )
                    )
                    binding.scoreAwayTeam.setTextColor(
                        getColorFromAttribute(
                            binding.root.context,
                            R.attr.colorTertiary
                        )
                    )
                }
            }
        }

    }

    class HeaderItemViewHolder(val binding: TournamentHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: MatchEventItem.Header) {
            binding.tournamentCountry.text = header.tournament.country.name
            binding.tournamentName.text = header.tournament.name
            Glide.with(binding.root.context)
                .load("https://academy-backend.sofascore.dev/tournament/${header.tournament.id}/image")
                .placeholder(R.drawable.loading_spinner)
                .into(binding.tournamentLogo)
        }
    }

    fun updateItems(newItems: List<MatchEventItem>) {
        val diffResult = DiffUtil.calculateDiff(MatchEventDiffCallback(items, newItems))
        diffResult.dispatchUpdatesTo(this)
        items = newItems
    }

    class MatchEventDiffCallback(
        private val oldItems: List<MatchEventItem>,
        private val newItems: List<MatchEventItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return if (oldItem is MatchEventItem.Header && newItem is MatchEventItem.Header) {
                oldItem.tournament.id == newItem.tournament.id
            } else if (oldItem is MatchEventItem.EventItem && newItem is MatchEventItem.EventItem) {
                oldItem.event.id == newItem.event.id
            } else false

        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return oldItem == newItem
        }

    }
}