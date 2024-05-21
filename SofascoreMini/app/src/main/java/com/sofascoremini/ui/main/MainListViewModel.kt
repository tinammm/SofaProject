package com.sofascoremini.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofascoremini.data.Repository
import com.sofascoremini.data.models.Country
import com.sofascoremini.data.models.EventResponse
import com.sofascoremini.data.models.Score
import com.sofascoremini.data.models.Status
import com.sofascoremini.data.models.Team
import com.sofascoremini.data.models.Tournament
import com.sofascoremini.data.models.WinnerCode
import kotlinx.coroutines.launch
import com.sofascoremini.data.remote.Result
import com.sofascoremini.util.offsetToDate
import com.sofascoremini.util.offsetToDateHeader

class MainListViewModel : ViewModel() {

    private val _mainList = MutableLiveData<Result<List<EventResponse>>>()
    val mainList: LiveData<Result<List<EventResponse>>> get() = _mainList

    private val sportsRepository = Repository()

    init {
        Log.d("test", "init")
        getEventsForSportsAndDate(
            "football", offsetToDate(0)
        )
    }

    fun getEventsForSportsAndDate(slug: String, date: String) {
        viewModelScope.launch {
            _mainList.postValue(
                sportsRepository.getEventsForSportsAndDate(
                    slug,
                    date
                )
            )
        }
    }

    fun test() {
        _mainList.value = Result.Success(
            listOf<EventResponse>(
                EventResponse(
                    id = 0,
                    tournament = Tournament(id = 1, name = "HNL", country = Country(1, "Croatia")),
                    homeTeam = Team(1, "Hajduk"),
                    awayTeam = Team(2, "Dinamo"),
                    status = Status.IN_PROGRESS,
                    startDate = "2024-05-21T11:50:00+02:00",
                    homeScore = Score(
                        total = 1,
                        period1 = 1,
                        period2 = null,
                        period3 = null,
                        period4 = null,
                        overtime = null
                    ),
                    awayScore = Score(
                        total = 0,
                        period1 = 0,
                        period2 = null,
                        period3 = null,
                        period4 = null,
                        overtime = null
                    ),
                    winnerCode = null
                ),
                EventResponse(
                    id = 0,
                    tournament = Tournament(id = 1, name = "HNL", country = Country(1, "Croatia")),
                    homeTeam = Team(1, "Hajduk"),
                    awayTeam = Team(2, "Dinamo"),
                    status = Status.NOT_STARTED,
                    startDate = "2024-05-21T13:50:00+02:00",
                    homeScore = Score(
                        total = null,
                        period1 = null,
                        period2 = null,
                        period3 = null,
                        period4 = null,
                        overtime = null
                    ),
                    awayScore = Score(
                        total = null,
                        period1 = null,
                        period2 = null,
                        period3 = null,
                        period4 = null,
                        overtime = null
                    ),
                    winnerCode = null
                )
            )
        )
    }
}