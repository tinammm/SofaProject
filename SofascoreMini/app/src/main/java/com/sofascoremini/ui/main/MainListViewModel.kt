package com.sofascoremini.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofascoremini.data.MainListRepository
import com.sofascoremini.data.models.EventResponse
import kotlinx.coroutines.launch
import com.sofascoremini.data.remote.Result
import com.sofascoremini.util.offsetToDate

class MainListViewModel : ViewModel() {

    private val _mainList = MutableLiveData<Result<List<EventResponse>>>()
    val mainList: LiveData<Result<List<EventResponse>>> get() = _mainList

    private val sportsRepository = MainListRepository

    init {
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
}