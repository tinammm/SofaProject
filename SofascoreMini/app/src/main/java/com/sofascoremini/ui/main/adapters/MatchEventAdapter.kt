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

            else -> throw IllegalArgumentException("Invalid view type")
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
            binding.homeTeamLogo.apply {
                loadImage("https://academy-backend.sofascore.dev/team/${eventItem.event.homeTeam.id}/image")
            }
            binding.awayTeamName.text = eventItem.event.awayTeam.name
            binding.awayTeamLogo.apply {
                loadImage("https://academy-backend.sofascore.dev/team/${eventItem.event.awayTeam.id}/image")
            }
            binding.scoreHomeTeam.text = eventItem.event.homeScore.total?.toString().orEmpty()
            binding.scoreAwayTeam.text = eventItem.event.awayScore.total?.toString().orEmpty()

            when (eventItem.event.status) {
                Status.FINISHED -> {
                    binding.status.text = "FT"
                    val color = getColorFromAttribute(binding.root.context, R.attr.n_lv_2)
                    when (eventItem.event.winnerCode) {
                        WinnerCode.HOME -> setTextColor(color, binding.scoreAwayTeam, binding.awayTeamName)
                        WinnerCode.AWAY -> setTextColor(color, binding.scoreHomeTeam, binding.homeTeamName)
                        else -> setTextColor(color, binding.scoreHomeTeam, binding.homeTeamName, binding.scoreAwayTeam, binding.awayTeamName)
                    }
                }

                Status.NOT_STARTED -> binding.status.text = "-"

                Status.IN_PROGRESS -> {
                    binding.status.let {
                        it.text = calculateMinutesPassed(eventItem.event.startDate.toString())
                        it.setTextColor(getColorFromAttribute(binding.root.context, R.attr.colorTertiary))
                    }
                    binding.scoreHomeTeam.setTextColor(getColorFromAttribute(binding.root.context, R.attr.colorTertiary))
                    binding.scoreAwayTeam.setTextColor(getColorFromAttribute(binding.root.context, R.attr.colorTertiary))
                }
            }
        }

    }

    class HeaderItemViewHolder(val binding: TournamentHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: MatchEventItem.Header) {
            binding.apply {
                tournamentCountry.text = header.tournament.country.name
                tournamentName.text = header.tournament.name
                tournamentLogo.apply {
                    loadImage("https://academy-backend.sofascore.dev/tournament/${header.tournament.id}/image")
                }
            }
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