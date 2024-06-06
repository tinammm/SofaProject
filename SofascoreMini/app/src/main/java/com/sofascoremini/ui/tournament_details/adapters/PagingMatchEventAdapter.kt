package com.sofascoremini.ui.tournament_details.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sofascoremini.data.models.EventResponse
import com.sofascoremini.databinding.MatchCellItemBinding
import com.sofascoremini.databinding.RoundHeaderBindingBinding
import com.sofascoremini.ui.main.adapters.MatchEventAdapter
import com.sofascoremini.ui.main.adapters.MatchEventItem

class PagingMatchEventAdapter : PagingDataAdapter<MatchEventItem, RecyclerView.ViewHolder>(
    MatchEventAdapter.MatchEventItemDiffCallback()
) {
    var onEventClick: ((event: EventResponse) -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is MatchEventAdapter.EventItemViewHolder -> {
                holder.bind(item as MatchEventItem.EventItem) {
                    onEventClick?.invoke(it)
                }
            }
            is MatchEventAdapter.RoundViewHolder -> {
                val roundItem = item as? MatchEventItem.RoundHeader
                roundItem?.let {
                    holder.bind(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EVENT -> {
                val binding = MatchCellItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MatchEventAdapter.EventItemViewHolder(binding)
            }
            VIEW_TYPE_ROUND -> {
                val binding = RoundHeaderBindingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MatchEventAdapter.RoundViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MatchEventItem.EventItem -> VIEW_TYPE_EVENT
            is MatchEventItem.RoundHeader -> VIEW_TYPE_ROUND
            else -> throw IllegalArgumentException("Invalid item type at position $position")
        }
    }

    companion object {
        private const val VIEW_TYPE_EVENT = 1
        private const val VIEW_TYPE_ROUND = 3
    }
}