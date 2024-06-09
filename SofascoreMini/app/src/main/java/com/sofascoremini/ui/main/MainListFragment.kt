package com.sofascoremini.ui.main


import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoremini.R
import com.sofascoremini.data.models.SportSlug
import com.sofascoremini.databinding.FragmentMainListBinding
import com.sofascoremini.ui.main.adapters.DatesAdapter
import com.sofascoremini.ui.main.adapters.MatchEventAdapter
import com.sofascoremini.ui.settings.DATE
import com.sofascoremini.util.CenterLayoutManager
import com.sofascoremini.util.formatDate
import com.sofascoremini.util.formatDay
import com.sofascoremini.util.generateDateRange
import com.sofascoremini.util.setUpVisibility
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MainListFragment : Fragment() {

    private lateinit var binding: FragmentMainListBinding
    private val mainListViewModel: MainListViewModel by activityViewModels()
    private val matchEventAdapter by lazy {
        MatchEventAdapter().apply {
            onTournamentClick = {
                val action = MainListFragmentDirections.actionMainListFragmentToTournamentDetailsFragment(it)
                findNavController().navigate(action)
            }
            onEventClick = {
                val action = MainListFragmentDirections.actionMainListFragmentToEventDetailsFragment(it)
                findNavController().navigate(action)
            }
        }
    }
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }
    private val datesAdapter by lazy {
        DatesAdapter(
            context = requireContext(),
            dates = generateDateRange(),
            onDateSelected = { selectedDate ->
                val selectedSport = mainListViewModel.selectedSport.value ?: SportSlug.FOOTBALL
                mainListViewModel.getEventsForSportsAndDate(
                    selectedSport, selectedDate
                )
            }
        )
    }

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainListBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler.post {
            binding.eventRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = matchEventAdapter
            }

            binding.dateTab.apply {
                layoutManager =
                    CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                adapter = datesAdapter
                this.smoothScrollToPosition(datesAdapter.selectedPosition)
            }

            binding.swipeRefresh.setOnRefreshListener {
                val selectedDate = mainListViewModel.selectedDate.value ?: LocalDate.now()
                val selectedSport = mainListViewModel.selectedSport.value ?: SportSlug.FOOTBALL
                mainListViewModel.getEventsForSportsAndDate(
                    selectedSport, selectedDate, true
                )
                binding.swipeRefresh.isRefreshing = false
            }

            binding.topNavigation.setOnItemSelectedListener {
                val selectedDate = mainListViewModel.selectedDate.value ?: LocalDate.now()
                when (it.itemId) {
                    R.id.action_football -> {
                        mainListViewModel.getEventsForSportsAndDate(
                            SportSlug.FOOTBALL,
                            selectedDate
                        )
                        true
                    }

                    R.id.action_basketball -> {
                        mainListViewModel.getEventsForSportsAndDate(
                            SportSlug.BASKETBALL,
                            selectedDate
                        )
                        true
                    }

                    R.id.action_am_football -> {
                        mainListViewModel.getEventsForSportsAndDate(
                            SportSlug.AM_FOOTBALL,
                            selectedDate
                        )
                        true
                    }

                    else -> false
                }
            }
        }


        mainListViewModel.mainList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UiState.Success -> {
                    binding.apply {
                        setUpVisibility(true, eventRecyclerView, dateAndCount)
                        setUpVisibility(false, loadingProgressBar, emptyPlaceholder)
                        numOfEvents.text = getString(R.string.d_events, result.data.size)
                    }
                    matchEventAdapter.updateItems(result.data)
                }

                is UiState.Empty -> {
                    binding.apply {
                        setUpVisibility(true, emptyPlaceholder)
                        setUpVisibility(false, loadingProgressBar, eventRecyclerView, dateAndCount)
                    }
                }

                is UiState.Loading -> {
                    binding.apply {
                        setUpVisibility(true, loadingProgressBar)
                        setUpVisibility(false, emptyPlaceholder, eventRecyclerView, dateAndCount)
                    }
                }

                is UiState.Error -> {
                    binding.apply {
                        setUpVisibility(
                            false,
                            emptyPlaceholder,
                            eventRecyclerView,
                            dateAndCount,
                            loadingProgressBar
                        )
                    }
                    Toast.makeText(requireContext(), "A network error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        mainListViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            val format = preferences.getString(DATE, "DD / MM / YYYY") ?: "DD / MM / YYYY"
            val dateHeader = formatDay(date, "E") + ", " + formatDate(date, format)
            binding.date.text = dateHeader
        }

    }
}