package com.sofascoremini.ui.tournament_details.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sofascoremini.databinding.FragmentMatchesBinding
import com.sofascoremini.ui.tournament_details.TournamentDetailsFragmentDirections
import com.sofascoremini.ui.tournament_details.TournamentViewModel
import com.sofascoremini.ui.tournament_details.adapters.PagingMatchEventAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MatchesFragment : Fragment() {

    private lateinit var binding: FragmentMatchesBinding
    private val matchesViewModel by activityViewModels<TournamentViewModel>()

    private val matchesPagingAdapter by lazy {
        PagingMatchEventAdapter(requireContext()).apply {
            onEventClick = {
                val action =
                    TournamentDetailsFragmentDirections.actionTournamentDetailsFragmentToEventDetailsFragment(
                        it
                    )
                findNavController().navigate(action)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pagingRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = matchesPagingAdapter
        }


        matchesPagingAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        matchesViewModel.eventsLiveData.observe(viewLifecycleOwner) { pagingData ->
            lifecycleScope.launch {
                matchesPagingAdapter.submitData(pagingData)
            }

        }

        lifecycleScope.launch {
            matchesPagingAdapter.loadStateFlow.collectLatest {
                binding.loadingProgressBar.isVisible = (it.refresh is LoadState.Loading)
                binding.loadingProgressBarAppend.isVisible = (it.append is LoadState.Loading)
            }
        }
    }
}