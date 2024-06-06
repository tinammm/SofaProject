package com.sofascoremini.ui.main.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sofascoremini.ui.main.EventsFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 15

    override fun createFragment(position: Int): Fragment {
        val offset = position - 7
        return EventsFragment.newInstance(offset)
    }
}