package com.sofascoremini.ui.leagues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoremini.R
import com.sofascoremini.data.models.SportSlug
import com.sofascoremini.databinding.FragmentLeaguesBinding
import com.sofascoremini.ui.leagues.adapters.LeagueAdapter
import com.sofascoremini.ui.main.UiState
import com.sofascoremini.util.setUpVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaguesFragment : Fragment() {
    private lateinit var binding: FragmentLeaguesBinding
    private val leaguesViewModel: LeaguesViewModel by viewModels()
    private val leagueAdapter: LeagueAdapter by lazy {
        LeagueAdapter().apply {
            onTournamentClick = {
                val action =
                    LeaguesFragmentDirections.actionLeaguesFragmentToTournamentDetailsFragment(it)
                findNavController().navigate(action)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaguesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.leagueRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leagueAdapter
        }

        binding.topNavigation.root.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_football -> {
                    leaguesViewModel.getLeaguesForSport(SportSlug.FOOTBALL)
                    true
                }

                R.id.action_basketball -> {
                    leaguesViewModel.getLeaguesForSport(SportSlug.BASKETBALL)
                    true
                }

                R.id.action_am_football -> {
                    leaguesViewModel.getLeaguesForSport(SportSlug.AM_FOOTBALL,)
                    true
                }

                else -> false
            }
        }

        leaguesViewModel.leaguesList.observe(viewLifecycleOwner){ result ->
            when (result) {
                is UiState.Success -> {
                    leagueAdapter.updateItems(result.data)
                    binding.apply {
                        setUpVisibility(true, leagueRecycler)
                        setUpVisibility(false, loadingProgressBar.root)
                    }
                }

                is UiState.Empty -> {}

                is UiState.Loading -> {
                    binding.apply {
                        setUpVisibility(true, loadingProgressBar.root, leagueRecycler)
                    }
                }

                is UiState.Error -> {
                    binding.apply {
                        setUpVisibility(
                            false,
                            leagueRecycler,
                            loadingProgressBar.root
                        )
                    }
                    Toast.makeText(requireContext(), "A network error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }
}