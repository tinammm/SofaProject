package com.sofascoremini.ui.event_details.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sofascoremini.R
import com.sofascoremini.data.models.EventResponse
import com.sofascoremini.data.models.GoalType
import com.sofascoremini.data.models.IncidentColor
import com.sofascoremini.data.models.IncidentResponse
import com.sofascoremini.data.models.Side
import com.sofascoremini.data.models.SportSlug
import com.sofascoremini.data.models.Status
import com.sofascoremini.databinding.BasketballGoalItemBinding
import com.sofascoremini.databinding.CardItemBinding
import com.sofascoremini.databinding.FootballGoalItemBinding
import com.sofascoremini.databinding.PeriodItemBinding
import com.sofascoremini.util.getColorFromAttribute
import com.sofascoremini.util.getRandomRedCardReason
import com.sofascoremini.util.getRandomYellowCardReason

sealed class IncidentItem {
    data class Card(val cardIncident: IncidentResponse) : IncidentItem()
    data class Goal(val goalIncident: IncidentResponse) : IncidentItem()
    data class Period(val periodIncident: IncidentResponse) : IncidentItem()
}

class IncidentsAdapter(val event: EventResponse) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = emptyList<IncidentItem>()

    companion object {
        private const val VIEW_TYPE_CARD = 0
        const val VIEW_TYPE_GOAL = 1
        private const val VIEW_TYPE_PERIOD = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is IncidentItem.Card -> VIEW_TYPE_CARD
            is IncidentItem.Goal -> VIEW_TYPE_GOAL
            is IncidentItem.Period -> VIEW_TYPE_PERIOD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_CARD -> CardItemViewHolder(
                CardItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_GOAL -> {
                if (event.tournament.sport.slug == SportSlug.BASKETBALL) BasketballGoalItemViewHolder(
                    BasketballGoalItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
                else FootballGoalItemViewHolder(
                    FootballGoalItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            VIEW_TYPE_PERIOD -> PeriodItemViewHolder(
                PeriodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }


    inner class CardItemViewHolder(private val binding: CardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: IncidentItem.Card) {
            val incident = card.cardIncident
            val cardColorIcon = when (incident.color) {
                IncidentColor.YELLOW -> R.drawable.yellow_card
                else -> R.drawable.red_card
            }

            val foulType = when (incident.color) {
                IncidentColor.YELLOW -> getRandomYellowCardReason(incident.id.toLong())
                else -> getRandomRedCardReason(incident.id.toLong())
            }

            val incidentTime =
                binding.root.context.getString(R.string.minutes_format, incident.time)

            binding.homeTeam.isVisible = incident.teamSide == Side.HOME
            binding.awayTeam.isVisible = incident.teamSide == Side.AWAY

            if (incident.teamSide == Side.HOME) {
                binding.typeItemStart.icon.setImageResource(cardColorIcon)
                binding.typeItemStart.minutes.text = incidentTime
                binding.homePlayerName.text = incident.player?.name
                binding.homeCardType.text = foulType
            } else {
                binding.typeItemEnd.icon.setImageResource(cardColorIcon)
                binding.typeItemEnd.minutes.text = incidentTime
                binding.awayPlayerName.text = incident.player?.name
                binding.awayCardType.text = foulType
            }
        }

    }

    inner class FootballGoalItemViewHolder(private val binding: FootballGoalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goalIncident: IncidentItem.Goal) {
            val goal = goalIncident.goalIncident

            binding.homeTeam.isVisible = goal.scoringTeam == Side.HOME
            binding.awayTeam.isVisible = goal.scoringTeam == Side.AWAY

            val incidentTime = binding.root.context.getString(R.string.minutes_format, goal.time)
            val score =
                binding.root.context.getString(R.string.score_text, goal.homeScore, goal.awayScore)
            if (goal.scoringTeam == Side.HOME) {
                binding.typeItemStart.icon.setImageResource(getGoalIcon(goal.goalType))
                binding.typeItemStart.minutes.text = incidentTime
                binding.homePlayerName.text = goal.player?.name
                binding.scoreHome.text = score
            } else {
                binding.typeItemEnd.icon.setImageResource(getGoalIcon(goal.goalType))
                binding.typeItemEnd.minutes.text = incidentTime
                binding.awayPlayerName.text = goal.player?.name
                binding.scoreAway.text = score
            }

        }

    }

    inner class BasketballGoalItemViewHolder(private val binding: BasketballGoalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goalIncident: IncidentItem.Goal) {
            val goal = goalIncident.goalIncident
            binding.typeItemStart.isVisible = goal.scoringTeam == Side.HOME
            binding.typeItemEnd.isVisible = goal.scoringTeam == Side.AWAY
            binding.iconStart.setImageResource(getGoalIcon(goal.goalType))
            binding.iconEnd.setImageResource(getGoalIcon(goal.goalType))

            val score =
                binding.root.context.getString(R.string.score_text, goal.homeScore, goal.awayScore)
            binding.scoreHome.apply {
                isVisible = goal.scoringTeam == Side.HOME
                text = score
            }
            binding.scoreAway.apply {
                isVisible = goal.scoringTeam == Side.AWAY
                text = score
            }

            val incidentTime = binding.root.context.getString(R.string.minutes_format, goal.time)
            binding.minutes.text = incidentTime
        }

    }

    inner class PeriodItemViewHolder(private val binding: PeriodItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(period: IncidentItem.Period) {
            binding.periodText.apply {
                text = period.periodIncident.text
                val periods = items.filterIsInstance<IncidentItem.Period>()

                if (event.status == Status.IN_PROGRESS && periods.indexOf(period) == 0)
                    setTextColor(
                        getColorFromAttribute(
                            binding.root.context, R.attr.colorTertiary
                        )
                    )
                else setTextColor(getColorFromAttribute(binding.root.context, R.attr.n_lv_1))
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is IncidentsAdapter.PeriodItemViewHolder -> {
                holder.bind(items[position] as IncidentItem.Period)
            }

            is IncidentsAdapter.BasketballGoalItemViewHolder -> {
                holder.bind(items[position] as IncidentItem.Goal)
            }

            is IncidentsAdapter.FootballGoalItemViewHolder -> {
                holder.bind(items[position] as IncidentItem.Goal)
            }

            is IncidentsAdapter.CardItemViewHolder -> {
                holder.bind(items[position] as IncidentItem.Card)
            }
        }
    }


    fun updateItems(newItems: List<IncidentItem>) {
        val diffResult = DiffUtil.calculateDiff(IncidentsDiffCallback(items, newItems))
        diffResult.dispatchUpdatesTo(this)
        items = newItems
    }

    class IncidentsDiffCallback(
        private val oldItems: List<IncidentItem>, private val newItems: List<IncidentItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return when {
                oldItem is IncidentItem.Goal && newItem is IncidentItem.Goal -> oldItem.goalIncident.id == newItem.goalIncident.id

                oldItem is IncidentItem.Card && newItem is IncidentItem.Card -> oldItem.cardIncident.id == newItem.cardIncident.id

                oldItem is IncidentItem.Period && newItem is IncidentItem.Period -> oldItem.periodIncident.id == newItem.periodIncident.id

                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return oldItem == newItem
        }

    }


    private fun getGoalIcon(goalType: GoalType?): Int {
        return when (goalType) {
            GoalType.REGULAR -> R.drawable.goal_icon
            GoalType.OWN_GOAL -> R.drawable.autogoal
            GoalType.PENALTY -> R.drawable.penalty_score
            GoalType.ONE_POINT -> if (event.tournament.sport.slug == SportSlug.BASKETBALL) R.drawable.basketball_1_point else R.drawable.rugby_point_1
            GoalType.TWO_POINT -> if (event.tournament.sport.slug == SportSlug.BASKETBALL) R.drawable.basketball_2_point else R.drawable.rugby_point_2
            GoalType.THREE_POINT -> if (event.tournament.sport.slug == SportSlug.BASKETBALL) R.drawable.basketball_3_point else R.drawable.rugby_point_3
            GoalType.TOUCHDOWN -> R.drawable.touchdown
            GoalType.FIELD_GOAL -> R.drawable.field_goal
            GoalType.SAFETY -> R.drawable.safety
            GoalType.EXTRA_POINT -> R.drawable.extra_point
            else -> R.drawable.goal_icon
        }
    }
}