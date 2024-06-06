package com.sofascoremini.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoremini.R
import com.sofascoremini.data.remote.Result
import com.sofascoremini.databinding.FragmentEventsBinding
import com.sofascoremini.ui.main.adapters.MatchEventAdapter
import com.sofascoremini.ui.main.adapters.MatchEventItem
import com.sofascoremini.util.offsetToDateHeader

class EventsFragment : Fragment() {

    private val mainListViewModel by activityViewModels<MainListViewModel>()
    private lateinit var binding: FragmentEventsBinding
    private val matchEventAdapter by lazy { MatchEventAdapter() }
    companion object {
        private const val ARG_OFFSET = "offset"

        fun newInstance(offset: Int): EventsFragment {
            val fragment = EventsFragment()
            val args = Bundle()
            args.putInt(ARG_OFFSET, offset)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val offset = arguments?.getInt(ARG_OFFSET) ?: 0

        binding.eventRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = matchEventAdapter
        }


        binding.date.text = offsetToDateHeader(offset)

        mainListViewModel.mainList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val groupedEvents = result.data.groupBy { it.tournament }
                    val sortedEvents = mutableListOf<MatchEventItem>()

                    groupedEvents.forEach { (tournament, events) ->
                        val sortedEventsForTournament = events.sortedBy { it.startDate }
                        val tournamentHeader = MatchEventItem.Header(tournament)
                        sortedEvents.add(tournamentHeader)
                        sortedEvents.addAll(sortedEventsForTournament.map {
                            MatchEventItem.EventItem(
                                it
                            )
                        })
                    }
                    matchEventAdapter.updateItems(sortedEvents)
                    binding.numOfEvents.text = getString(R.string.d_events, result.data.size)
                }

                is Result.Error -> {
                    Toast.makeText(requireContext(), "A network error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}