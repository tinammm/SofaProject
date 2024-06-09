package com.sofascoremini.ui.leagues.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sofascoremini.data.models.Tournament
import com.sofascoremini.databinding.LeagueItemBinding
import com.sofascoremini.util.loadTournamentImage

class LeagueAdapter :
    RecyclerView.Adapter<LeagueAdapter.LeagueItemViewHolder>() {
    private var items: List<Tournament> = emptyList()
    var onTournamentClick: ((tournament: Tournament) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueItemViewHolder {
        return LeagueItemViewHolder(
            LeagueItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class LeagueItemViewHolder(private val binding: LeagueItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tournament: Tournament) {
            binding.leagueIcon.loadTournamentImage(tournament.id)
            binding.leagueName.text = tournament.name
            binding.root.setOnClickListener {
                onTournamentClick?.invoke(tournament)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: LeagueItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size


    fun updateItems(newItems: List<Tournament>) {
        val diffResult = DiffUtil.calculateDiff(LeagueDiffCallback(items, newItems))
        diffResult.dispatchUpdatesTo(this)
        items = newItems
    }

    class LeagueDiffCallback(
        private val oldItems: List<Tournament>,
        private val newItems: List<Tournament>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return oldItem == newItem
        }

    }

}
