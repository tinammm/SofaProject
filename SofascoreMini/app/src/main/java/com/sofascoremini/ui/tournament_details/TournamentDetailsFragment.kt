package com.sofascoremini.ui.tournament_details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascoremini.databinding.FragmentTournamentDetailsBinding
import com.sofascoremini.ui.tournament_details.adapters.ViewPagerAdapter
import com.sofascoremini.util.loadCountryImage
import com.sofascoremini.util.loadTournamentImage
import com.sofascoremini.util.parseJsonToModel
import com.sofascoremini.util.readJsonFromAssets
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TournamentDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTournamentDetailsBinding
    private val safeArgs: TournamentDetailsFragmentArgs by navArgs()
    private val tournamentViewModel by activityViewModels<TournamentViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTournamentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        tournamentViewModel.setTournamentId(safeArgs.tournament.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val jsonString = readJsonFromAssets(requireContext(), "countries.json")
        val countryList = parseJsonToModel(jsonString)

        binding.apply {
            val country = countryList.find {
                it.name == safeArgs.tournament.country.name
            }
            tournamentToolbar.name.text = safeArgs.tournament.name
            tournamentToolbar.countryName.text = safeArgs.tournament.country.name
            tournamentToolbar.logo.loadTournamentImage(safeArgs.tournament.id)
            tournamentToolbar.countryLogo.loadCountryImage(country?.code?.lowercase())
        }

        binding.pager.adapter = ViewPagerAdapter(requireActivity())
        val tabText = listOf("Matches", "Standings")
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = tabText[position]
        }.attach()

    }

    @Deprecated("Deprecated in Java", ReplaceWith("menu.clear()"))
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}