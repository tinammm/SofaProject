package com.sofascoremini.ui.tournament_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import com.sofascoremini.data.EventPagingSource
import com.sofascoremini.data.ITEMS_PER_PAGE
import com.sofascoremini.data.TournamentRepository
import com.sofascoremini.data.models.TournamentStandingsResponse
import com.sofascoremini.data.models.Type
import com.sofascoremini.data.remote.Result
import com.sofascoremini.ui.main.UiState
import com.sofascoremini.ui.main.adapters.MatchEventItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TournamentViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _tournamentStandings = MutableLiveData<UiState<List<TournamentStandingsResponse>>>()
    val tournamentStandings: LiveData<UiState<List<TournamentStandingsResponse>>> =
        _tournamentStandings

    private val _tournamentId = MutableLiveData<Int>()

    val eventsLiveData: LiveData<PagingData<MatchEventItem>> = _tournamentId.switchMap { id ->
        Pager(
            PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                prefetchDistance = 3,
                enablePlaceholders = false
            )
        ) {
            EventPagingSource(tournamentRepository, id)
        }.flow
            .map { pagingData ->
                pagingData.insertSeparators { before, after ->
                    if (before == null) {
                        MatchEventItem.RoundHeader(0)
                    } else if (before is MatchEventItem.EventItem && after is MatchEventItem.EventItem && before.event.round != after.event.round) {
                        MatchEventItem.RoundHeader(after.event.round)
                    } else {
                        null
                    }
                }
            }.asLiveData()
            .cachedIn(viewModelScope)
    }

    fun setTournamentId(id: Int) {
        _tournamentId.value = id
    }

    fun getTournamentStandings() {
        viewModelScope.launch {
            _tournamentStandings.value = UiState.Loading
            _tournamentStandings.value = when (val result =
                tournamentRepository.getTournamentStandings(_tournamentId.value ?: 0)) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(result.data.filter { it.type == Type.TOTAL })
                    }
                }

                is Result.Error -> {
                    UiState.Error("A network error occurred")
                }
            }
        }
    }
}