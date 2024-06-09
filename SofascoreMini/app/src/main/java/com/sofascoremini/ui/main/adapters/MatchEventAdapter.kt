package com.sofascoremini.ui.main.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sofascoremini.R
import com.sofascoremini.data.models.EventResponse
import com.sofascoremini.data.models.Status
import com.sofascoremini.data.models.Tournament
import com.sofascoremini.data.models.WinnerCode
import com.sofascoremini.databinding.ItemDividerBinding
import com.sofascoremini.databinding.MatchCellItemBinding
import com.sofascoremini.databinding.RoundHeaderBindingBinding
import com.sofascoremini.databinding.TournamentHeaderItemBinding
import com.sofascoremini.util.calculateMinutesPassed
import com.sofascoremini.util.extractHourMinute
import com.sofascoremini.util.getColorFromAttribute
import com.sofascoremini.util.loadImage
import com.sofascoremini.util.setTextColor

sealed class MatchEventItem {
    data class EventItem(val event: EventResponse) : MatchEventItem()
    data class Header(val tournament: Tournament) : MatchEventItem()
    data class RoundHeader(val round: Int) : MatchEventItem()
    data object Divider : MatchEventItem()
}

open class MatchEventAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = emptyList<MatchEventItem>()
    var onTournamentClick: ((tournament: Tournament) -> Unit)? = null
    var onEventClick: ((event: EventResponse) -> Unit)? = null

    companion object {
        private const val VIEW_TYPE_TOURNAMENT = 0
        const val VIEW_TYPE_EVENT = 1
        private const val VIEW_TYPE_DIVIDER = 2
        const val VIEW_TYPE_ROUND = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MatchEventItem.EventItem -> VIEW_TYPE_EVENT
            is MatchEventItem.Header -> VIEW_TYPE_TOURNAMENT
            is MatchEventItem.Divider -> VIEW_TYPE_DIVIDER
            is MatchEventItem.RoundHeader -> VIEW_TYPE_ROUND
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

            VIEW_TYPE_DIVIDER -> DividerViewHolder(
                ItemDividerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            VIEW_TYPE_ROUND -> RoundViewHolder(
                RoundHeaderBindingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }


    override fun getItemCount(): Int = items.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventItemViewHolder -> {
                holder.bind(items[position] as MatchEventItem.EventItem) {
                    onEventClick?.invoke(it)
                }
            }

            is HeaderItemViewHolder -> {
                holder.bind(items[position] as MatchEventItem.Header)
            }

            is RoundViewHolder -> {
                holder.bind(items[position] as MatchEventItem.RoundHeader)
            }

            is DividerViewHolder -> {}
        }
    }

    class DividerViewHolder(private val binding: ItemDividerBinding) :
        RecyclerView.ViewHolder(binding.root)

    class RoundViewHolder(private val binding: RoundHeaderBindingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(round: MatchEventItem.RoundHeader) {
            binding.roundNumber.text = binding.root.context.getString(R.string.round, round.round)
        }
    }

    class EventItemViewHolder(
        private val binding: MatchCellItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n", "ResourceType")
        fun bind(
            eventItem: MatchEventItem.EventItem,
            onEventClick: (event: EventResponse) -> Unit
        ) {

            binding.apply {
                root.setOnClickListener {
                    onEventClick.invoke(eventItem.event)
                }
                homeTeamName.text = eventItem.event.homeTeam.name
                homeTeamLogo.apply {
                    loadImage("https://academy-backend.sofascore.dev/team/${eventItem.event.homeTeam.id}/image")
                }
                awayTeamName.text = eventItem.event.awayTeam.name
                awayTeamLogo.apply {
                    loadImage("https://academy-backend.sofascore.dev/team/${eventItem.event.awayTeam.id}/image")
                }
                scoreHomeTeam.text = eventItem.event.homeScore.total?.toString().orEmpty()
                scoreAwayTeam.text = eventItem.event.awayScore.total?.toString().orEmpty()
                startTime.text = extractHourMinute(eventItem.event.startDate.toString())

                when (eventItem.event.status) {
                    Status.FINISHED -> {
                        status.text = "FT"
                        val winColor = getColorFromAttribute(root.context, R.attr.n_lv_1)
                        val loseColor = getColorFromAttribute(root.context, R.attr.n_lv_2)

                        when (eventItem.event.winnerCode) {
                            WinnerCode.HOME -> {
                                setTextColor(loseColor, scoreAwayTeam, awayTeamName)
                                setTextColor(winColor, scoreHomeTeam, homeTeamName)
                            }

                            WinnerCode.AWAY -> {
                                setTextColor(winColor, scoreAwayTeam, awayTeamName)
                                setTextColor(loseColor, scoreHomeTeam, homeTeamName)
                            }

                            else -> {
                                setTextColor(
                                    loseColor,
                                    scoreHomeTeam, homeTeamName,
                                    scoreAwayTeam, awayTeamName
                                )
                            }
                        }
                    }

                    Status.NOT_STARTED -> status.text = "-"

                    Status.IN_PROGRESS -> {
                        val liveColor = getColorFromAttribute(root.context, R.attr.n_lv_1)
                        status.let {
                            it.text = calculateMinutesPassed(eventItem.event.startDate.toString())
                            it.setTextColor(getColorFromAttribute(root.context, liveColor))
                        }
                        scoreHomeTeam.setTextColor(getColorFromAttribute(root.context, liveColor))
                        scoreAwayTeam.setTextColor(getColorFromAttribute(root.context, liveColor))
                    }
                }
            }

        }

    }

    inner class HeaderItemViewHolder(private val binding: TournamentHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: MatchEventItem.Header) {
            binding.apply {
                tournamentCountry.text = header.tournament.country.name
                tournamentName.text = header.tournament.name
                tournamentLogo.apply {
                    loadImage("https://academy-backend.sofascore.dev/tournament/${header.tournament.id}/image")
                }
            }
            binding.root.setOnClickListener {
                onTournamentClick?.invoke(header.tournament)
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
            return when {
                oldItem is MatchEventItem.Header && newItem is MatchEventItem.Header ->
                    oldItem.tournament.id == newItem.tournament.id

                oldItem is MatchEventItem.EventItem && newItem is MatchEventItem.EventItem ->
                    oldItem.event.id == newItem.event.id

                oldItem is MatchEventItem.Divider && newItem is MatchEventItem.Divider ->
                    true

                oldItem is MatchEventItem.RoundHeader && newItem is MatchEventItem.RoundHeader ->
                    oldItem.round == newItem.round

                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return oldItem == newItem
        }

    }

    class MatchEventItemDiffCallback : DiffUtil.ItemCallback<MatchEventItem>() {
        override fun areItemsTheSame(oldItem: MatchEventItem, newItem: MatchEventItem): Boolean {
            return when {
                oldItem is MatchEventItem.EventItem && newItem is MatchEventItem.EventItem ->
                    oldItem.event.id == newItem.event.id

                oldItem is MatchEventItem.Header && newItem is MatchEventItem.Header ->
                    oldItem.tournament.id == newItem.tournament.id

                oldItem is MatchEventItem.Divider && newItem is MatchEventItem.Divider ->
                    true

                oldItem is MatchEventItem.RoundHeader && newItem is MatchEventItem.RoundHeader ->
                    oldItem.round == newItem.round

                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: MatchEventItem, newItem: MatchEventItem): Boolean {
            return oldItem == newItem
        }
    }
}