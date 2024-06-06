package com.sofascoremini.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascoremini.R
import com.sofascoremini.databinding.FragmentMainListBinding
import com.sofascoremini.ui.main.adapters.ViewPagerAdapter
import com.sofascoremini.util.offsetToDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainListFragment : Fragment() {

    private lateinit var binding: FragmentMainListBinding
    private val mainListViewModel by activityViewModels<MainListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topNavigation.setOnItemSelectedListener {
            val offset = binding.pager.currentItem - 7
            val date = offsetToDate(offset)

            when (it.itemId) {
                R.id.action_football -> {
                    mainListViewModel.getEventsForSportsAndDate("football", date)
                    true
                }

                R.id.action_basketball -> {
                    mainListViewModel.getEventsForSportsAndDate("basketball", date)
                    true
                }

                R.id.action_am_football -> {
                    mainListViewModel.getEventsForSportsAndDate(
                        "american-football", date
                    )
                    true
                }

                else -> false
            }
        }

        binding.pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            val offset = position - 7
            val calendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, offset)
            }
            val dateFormat = SimpleDateFormat("dd.MM.", Locale.getDefault())
            val date = dateFormat.format(calendar.time)
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val day = dayFormat.format(calendar.time)

            tab.text = if (offset == 0) {
                "TODAY \n$date"
            } else {
                "$day \n$date"
            }
        }.attach()

        binding.pager.doOnAttach {
            binding.pager.setCurrentItem(7, true)
        }

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val offset = position - 7
                val date = offsetToDate(offset)
                when (binding.topNavigation.selectedItemId) {
                    R.id.action_football -> {
                        mainListViewModel.getEventsForSportsAndDate("football", date)
                    }
                    R.id.action_basketball -> {
                        mainListViewModel.getEventsForSportsAndDate("basketball", date)
                    }
                    R.id.action_am_football -> {
                        mainListViewModel.getEventsForSportsAndDate("american-football", date)
                    }
                }
            }
        })


    }
}