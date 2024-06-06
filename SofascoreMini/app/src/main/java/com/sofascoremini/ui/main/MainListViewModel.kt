package com.sofascoremini.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofascoremini.data.MainListRepository
import com.sofascoremini.data.models.SportSlug
import com.sofascoremini.data.remote.Result
import com.sofascoremini.ui.main.adapters.MatchEventItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String?) : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MainListViewModel @Inject constructor(
    private val mainListRepository: MainListRepository
) : ViewModel() {

    private val _mainList = MutableLiveData<UiState<List<MatchEventItem>>>()
    val mainList: LiveData<UiState<List<MatchEventItem>>> = _mainList

    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _selectedSport = MutableLiveData<SportSlug>()
    val selectedSport: LiveData<SportSlug> = _selectedSport

    init {
        _selectedDate.value = LocalDate.now()
        _selectedSport.value = SportSlug.FOOTBALL
        getEventsForSportsAndDate(
            SportSlug.FOOTBALL, LocalDate.now()
        )
    }

    fun getEventsForSportsAndDate(slug: SportSlug, date: LocalDate, isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (!isRefreshing) {
                _mainList.value = UiState.Loading
            }
            _selectedDate.value = date
            _selectedSport.value = slug
            _mainList.value = when (val result =
                mainListRepository.getEventsForSportsAndDate(slug.slug, date.toString())) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        UiState.Empty
                    } else {
                        val groupedEvents = result.data.groupBy { it.tournament }
                        val sortedEvents = mutableListOf<MatchEventItem>()

                        groupedEvents.entries.forEachIndexed { index, entry ->
                            val sortedEventsForTournament = entry.value.sortedBy { it.startDate }
                            val tournamentHeader = MatchEventItem.Header(entry.key)

                            sortedEvents.add(tournamentHeader)
                            sortedEvents.addAll(sortedEventsForTournament.map {
                                MatchEventItem.EventItem(it)
                            })

                            if (index != groupedEvents.size - 1) {
                                sortedEvents.add(MatchEventItem.Divider)
                            }
                        }
                        UiState.Success(sortedEvents)
                    }
                }

                is Result.Error -> {
                    UiState.Error("A network error occurred")
                }
            }
        }
    }
}