package com.sofascoremini.ui.tournament_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoremini.databinding.FragmentMatchesBinding
import com.sofascoremini.ui.tournament_details.adapters.PagingMatchEventAdapter
import kotlinx.coroutines.launch

class MatchesFragment : Fragment() {

    private lateinit var binding: FragmentMatchesBinding
    private val matchesViewModel: TournamentViewModel by activityViewModels()
    private val matchesPagingAdapter by lazy {
        PagingMatchEventAdapter().apply {
            onEventClick = {
                val action = TournamentDetailsFragmentDirections.actionTournamentDetailsFragmentToEventDetailsFragment(it)
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

        matchesViewModel.liveData.observe(viewLifecycleOwner) { pagingData ->
            lifecycleScope.launch {
                matchesPagingAdapter.submitData(pagingData)
            }
        }
    }
}