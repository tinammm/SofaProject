package com.sofascoremini.ui.tournament_details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoremini.databinding.FragmentStandingsBinding
import com.sofascoremini.ui.main.UiState
import com.sofascoremini.ui.tournament_details.adapters.StandingsAdapter
import com.sofascoremini.util.setUpVisibility

class StandingsFragment : Fragment() {

    private lateinit var binding: FragmentStandingsBinding
    private val standingsViewModel: TournamentViewModel by activityViewModels()
    private val standingsAdapter: StandingsAdapter by lazy { StandingsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentStandingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        standingsViewModel.getTournamentStandings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.standingsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = standingsAdapter
        }

        standingsViewModel.tournamentStandings.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UiState.Success -> {
                    binding.apply {
                        setUpVisibility(true, standingsRecycler)
                        setUpVisibility(false, loadingProgressBar)
                    }

                    standingsAdapter.updateItems(
                        result.data.first().sortedStandingsRows,
                        result.data.first().tournament.sport.slug
                    )
                }

                is UiState.Empty -> {}

                is UiState.Loading -> {
                    binding.apply {
                        setUpVisibility(true, loadingProgressBar)
                        setUpVisibility(false, standingsRecycler)
                    }
                }

                is UiState.Error -> {
                    binding.apply {
                        setUpVisibility(
                            false,
                            standingsRecycler,
                            loadingProgressBar
                        )
                    }
                    Toast.makeText(requireContext(), "A network error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}