package com.sofascoremini.ui.tournament_details.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sofascoremini.data.models.SportSlug
import com.sofascoremini.data.models.Standings
import com.sofascoremini.databinding.HeaderStandingsItemBinding
import com.sofascoremini.databinding.StandingsItemBinding
import com.sofascoremini.util.round
import com.sofascoremini.util.setUpVisibility


sealed class StandingsItem {
    data class Item(val standing: Standings) : StandingsItem()
    data object Header : StandingsItem()
}

class StandingsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val standings: MutableList<StandingsItem> = mutableListOf()
    private var slug: SportSlug = SportSlug.FOOTBALL

    companion object {
        private const val VIEW_TYPE_STANDING = 0
        private const val VIEW_TYPE_HEADER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (standings[position]) {
            is StandingsItem.Item -> VIEW_TYPE_STANDING
            is StandingsItem.Header -> VIEW_TYPE_HEADER
        }
    }

    inner class StandingItemViewHolder(private val binding: StandingsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(standingItem: StandingsItem.Item) {
            binding.apply {
                val standing = standingItem.standing
                val goalsRatio = "${standing.scoresFor}:${standing.scoresAgainst}"
                teamNumber.text = standings.indexOf(standingItem).toString()
                teamName.text = standing.team.name
                played.text = standing.played.toString()
                wins.text = standing.wins.toString()
                losses.text = standing.losses.toString()
                diff.text = (standing.wins - standing.losses).toString()
                goals.text = goalsRatio
                points.text = standing.points.toString()
                percent.text = standing.percentage?.round(2).toString()
                gamesBehind.text =
                    calculateGamesBehind((standings[1] as StandingsItem.Item).standing, standing)

                when (slug) {
                    SportSlug.FOOTBALL -> setUpVisibility(false, percent, gamesBehind)
                    SportSlug.AM_FOOTBALL -> setUpVisibility(false, points, goals, gamesBehind)
                    SportSlug.BASKETBALL -> setUpVisibility(false, points, goals)
                }
            }
        }
    }

    inner class StandingHeaderViewHolder(private val binding: HeaderStandingsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                when (slug) {
                    SportSlug.FOOTBALL -> setUpVisibility(false, percent, gamesBehind)
                    SportSlug.AM_FOOTBALL -> setUpVisibility(false, points, goals, gamesBehind)
                    SportSlug.BASKETBALL -> setUpVisibility(false, points, goals)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_STANDING -> StandingItemViewHolder(
                StandingsItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_HEADER -> StandingHeaderViewHolder(
                HeaderStandingsItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StandingsAdapter.StandingItemViewHolder -> holder.bind(standings[position] as StandingsItem.Item)
            is StandingsAdapter.StandingHeaderViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int = standings.size


    fun updateItems(newStandings: List<Standings>, sportSlug: SportSlug) {
        val standingsItems = mutableListOf<StandingsItem>()
        standingsItems.add(StandingsItem.Header)
        standingsItems.addAll(newStandings.map { StandingsItem.Item(it) })

        val diffCallback = StandingsDiffCallback(standings, standingsItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        slug = sportSlug

        standings.clear()
        standings.addAll(standingsItems)

        diffResult.dispatchUpdatesTo(this)
    }

    fun calculateGamesBehind(leader: Standings, team: Standings): String {
        return (((leader.wins - team.wins) + (team.losses - leader.losses)) / 2.0).toString()
    }

    class StandingsDiffCallback(
        private val oldList: List<StandingsItem>,
        private val newList: List<StandingsItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            if (oldItem::class != newItem::class) return false
            if (oldItem is StandingsItem.Item && newItem is StandingsItem.Item) {
                return oldItem.standing.id == newItem.standing.id
            }
            return true
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}