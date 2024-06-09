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
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.sofascoremini.MainActivity
import com.sofascoremini.R
import com.sofascoremini.data.models.Status
import com.sofascoremini.data.models.WinnerCode
import com.sofascoremini.databinding.FragmentEventDetailsBinding
import com.sofascoremini.ui.settings.DATE
import com.sofascoremini.util.calculateMinutesPassed
import com.sofascoremini.util.extractDate
import com.sofascoremini.util.extractHourMinute
import com.sofascoremini.util.getColorFromAttribute
import com.sofascoremini.util.loadImage
import com.sofascoremini.util.setTextColor

class EventDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailsBinding
    private val safeArgs: EventDetailsFragmentArgs by navArgs()
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }

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
            labelImageUrl = "https://academy-backend.sofascore.dev/tournament/${safeArgs.event.tournament.id}/image"
        )

        binding.eventToolbar.apply {
            val event = safeArgs.event
            awayTeamLogo.apply {
                loadImage("https://academy-backend.sofascore.dev/team/${event.awayTeam.id}/image")
            }

            homeTeamLogo.apply {
                loadImage("https://academy-backend.sofascore.dev/team/${event.homeTeam.id}/image")
            }

            awayTeamName.text = event.awayTeam.name
            homeTeamName.text = event.homeTeam.name

            when (event.status) {
                Status.FINISHED -> {
                    val winColor = getColorFromAttribute(binding.root.context, R.attr.n_lv_1)
                    val loseColor = getColorFromAttribute(binding.root.context, R.attr.n_lv_2)
                    date.visibility = View.GONE
                    scoreHome.text = event.homeScore.total.toString()
                    scoreAway.text = event.awayScore.total.toString()
                    time.text = "Full Time"
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