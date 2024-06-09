package com.sofascoremini.ui.tournament_details.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sofascoremini.ui.tournament_details.MatchesFragment
import com.sofascoremini.ui.tournament_details.StandingsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    companion object {
        private const val NUM_PAGES = 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MatchesFragment()
            1 -> StandingsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getItemCount(): Int {
        return NUM_PAGES
    }
}