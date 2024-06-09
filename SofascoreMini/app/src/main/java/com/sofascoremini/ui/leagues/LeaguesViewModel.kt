package com.sofascoremini.ui.leagues

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofascoremini.data.LeaguesRepository
import com.sofascoremini.data.models.SportSlug
import com.sofascoremini.data.models.Tournament
import com.sofascoremini.data.remote.Result
import com.sofascoremini.ui.main.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaguesViewModel @Inject constructor (
    private val leaguesRepository: LeaguesRepository
) : ViewModel(){

    private val _leaguesList = MutableLiveData<UiState<List<Tournament>>>()
    val leaguesList: LiveData<UiState<List<Tournament>>> = _leaguesList

    init {
        getLeaguesForSport()
    }

    fun getLeaguesForSport(sportSlug: SportSlug = SportSlug.FOOTBALL){
        viewModelScope.launch {
            _leaguesList.value = UiState.Loading
            _leaguesList.value = when (val result =
                leaguesRepository.getTournamentsForSport(sportSlug)) {
                is Result.Success -> UiState.Success(result.data.sortedBy { it.name })
                is Result.Error -> {
                    UiState.Error("A network error occurred")
                }
            }
        }
    }
}
