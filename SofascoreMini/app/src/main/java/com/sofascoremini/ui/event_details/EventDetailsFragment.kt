package com.sofascoremini.ui.event_details

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoremini.MainActivity
import com.sofascoremini.R
import com.sofascoremini.data.models.EventResponse
import com.sofascoremini.data.models.IncidentType
import com.sofascoremini.data.models.Status
import com.sofascoremini.data.models.WinnerCode
import com.sofascoremini.databinding.FragmentEventDetailsBinding
import com.sofascoremini.ui.event_details.adapters.IncidentItem
import com.sofascoremini.ui.event_details.adapters.IncidentsAdapter
import com.sofascoremini.ui.main.UiState
import com.sofascoremini.ui.settings.DATE
import com.sofascoremini.util.calculateMinutesPassed
import com.sofascoremini.util.extractDate
import com.sofascoremini.util.extractHourMinute
import com.sofascoremini.util.getColorFromAttribute
import com.sofascoremini.util.loadTeamImage
import com.sofascoremini.util.setTextColor
import com.sofascoremini.util.setUpVisibility
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback

@AndroidEntryPoint
class EventDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailsBinding
    private val safeArgs: EventDetailsFragmentArgs by navArgs()
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }

    private val eventViewModel by viewModels<EventViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                    EventViewModel.MyViewModelFactory> { factory ->
                factory.create(safeArgs.event)
            }
        }
    )

    private val incidentsAdapter by lazy {
        IncidentsAdapter(safeArgs.event)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        (requireActivity() as MainActivity).setUpAppBar(
            logoVisibility = false,
            hasNavIcon = true,
            navTint = R.attr.n_lv_1,
            backgroundColor = R.attr.surface1,
            hasEventLabel = true,
            label = createAppBarLabel(),
            labelImageId = safeArgs.event.tournament.id,
            navigateFunction = {
                val action =
                    EventDetailsFragmentDirections.actionEventDetailsFragmentToTournamentDetailsFragment(
                        safeArgs.event.tournament
                    )
                findNavController().navigate(action)
            }
        )

        setupToolbarConstants()
        setupToolbarVariables(safeArgs.event)

        binding.incidentsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = incidentsAdapter
        }

        binding.tournamentButton.setOnClickListener {
            val action =
                EventDetailsFragmentDirections.actionEventDetailsFragmentToTournamentDetailsFragment(
                    safeArgs.event.tournament
                )
            findNavController().navigate(action)
        }

        binding.swipeRefresh.setOnRefreshListener {
            eventViewModel.getEventIncidents(true)
            eventViewModel.getEventDetails()
            binding.swipeRefresh.isRefreshing = false
        }

        eventViewModel.eventDetails.observe(viewLifecycleOwner){
            setupToolbarVariables(it)
        }

        eventViewModel.eventIncidents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UiState.Success -> {
                    binding.apply {
                        setUpVisibility(false, loadingProgressBar.root)
                        setUpVisibility(true, incidentsRecycler)
                        incidentsAdapter.updateItems(result.data.map { incident ->
                            when (incident.type) {
                                IncidentType.CARD -> IncidentItem.Card(incident)
                                IncidentType.GOAL -> IncidentItem.Goal(incident)
                                IncidentType.PERIOD -> IncidentItem.Period(incident)
                            }
                        })
                    }
                }

                is UiState.Empty -> {
                    binding.apply {
                        setUpVisibility(false, loadingProgressBar.root, incidentsRecycler)
                        setUpVisibility(true, emptyPlaceholder)
                    }
                }

                is UiState.Loading -> {
                    binding.apply {
                        setUpVisibility(true, loadingProgressBar.root)
                        setUpVisibility(false, emptyPlaceholder, incidentsRecycler)
                    }
                }

                is UiState.Error -> {
                    binding.apply {
                        setUpVisibility(
                            false,
                            emptyPlaceholder,
                            loadingProgressBar.root,
                            incidentsRecycler
                        )
                    }
                    Toast.makeText(requireContext(), "A network error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    private fun setupToolbarConstants() {
        binding.eventToolbar.apply {
            val event = safeArgs.event
            awayTeamLogo.loadTeamImage(event.awayTeam.id)
            homeTeamLogo.loadTeamImage(event.homeTeam.id)
            awayTeamName.text = event.awayTeam.name
            homeTeamName.text = event.homeTeam.name
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupToolbarVariables(event: EventResponse) {
        binding.eventToolbar.apply {
            when (event.status) {
                Status.FINISHED -> {
                    val winColor = getColorFromAttribute(binding.root.context, R.attr.n_lv_1)
                    val loseColor = getColorFromAttribute(binding.root.context, R.attr.n_lv_2)
                    date.visibility = View.GONE
                    scoreHome.text = event.homeScore.total.toString()
                    scoreAway.text = event.awayScore.total.toString()
                    time.text = requireContext().getString(R.string.full_time)
                    when (event.winnerCode) {
                        WinnerCode.HOME -> {
                            setTextColor(loseColor, scoreAway, connector, time)
                            setTextColor(winColor, scoreHome)
                        }

                        WinnerCode.AWAY -> {
                            setTextColor(winColor, scoreAway)
                            setTextColor(loseColor, scoreHome, connector, time)
                        }

                        else -> setTextColor(loseColor, scoreHome, scoreAway, connector, time)
                    }
                }

                Status.NOT_STARTED -> {
                    val format = preferences.getString(DATE, "DD / MM / YYYY") ?: "DD / MM / YYYY"
                    score.visibility = View.GONE
                    date.text = extractDate(event.startDate.toString(), format)
                    time.text = extractHourMinute(event.startDate.toString())
                }

                Status.IN_PROGRESS -> {
                    val highlightColor =
                        getColorFromAttribute(binding.root.context, R.attr.colorTertiary)
                    scoreHome.text = event.homeScore.total.toString()
                    scoreAway.text = event.awayScore.total.toString()
                    date.visibility = View.GONE
                    time.text = calculateMinutesPassed(event.startDate.toString())
                    setTextColor(highlightColor, time, scoreHome, scoreAway, connector)
                }
            }
        }
    }


    private fun createAppBarLabel(): String {
        val separator = ", "
        val tournament = safeArgs.event.tournament
        return buildString {
            append(tournament.sport.slug.text)
            append(separator)
            append(tournament.country.name)
            append(separator)
            append(tournament.name)
            append(separator)
            append(getString(R.string.round, safeArgs.event.round))
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("menu.clear()"))
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
}