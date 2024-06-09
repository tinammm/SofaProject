package com.sofascoremini.ui.event_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofascoremini.data.EventRepository
import com.sofascoremini.data.models.EventResponse
import com.sofascoremini.data.models.IncidentResponse
import com.sofascoremini.data.remote.Result
import com.sofascoremini.ui.main.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EventViewModel.MyViewModelFactory::class)
class EventViewModel @AssistedInject constructor(
    private val eventRepository: EventRepository,
    @Assisted private val event: EventResponse
) : ViewModel() {

    @AssistedFactory
    interface MyViewModelFactory {
        fun create(event: EventResponse): EventViewModel
    }

    private val _eventIncidents = MutableLiveData<UiState<List<IncidentResponse>>>()
    val eventIncidents: LiveData<UiState<List<IncidentResponse>>> = _eventIncidents

    private val _eventDetails = MutableLiveData<EventResponse>()
    val eventDetails: LiveData<EventResponse> = _eventDetails

    init {
        getEventIncidents()
    }

    fun getEventIncidents(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (!isRefreshing) {
                _eventIncidents.value = UiState.Loading
            }
            _eventIncidents.value =
                when (val result = eventRepository.getEventIncidents(event.id)) {
                    is Result.Success -> {
                        if (result.data.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(result.data.reversed())
                        }
                    }

                    is Result.Error -> {
                        UiState.Error("A network error occurred")
                    }
                }
        }
    }

    fun getEventDetails() {
        viewModelScope.launch {
            _eventDetails.value =
                when (val result = eventRepository.getEventDetails(event.id)) {
                    is Result.Success -> {
                        result.data
                    }

                    is Result.Error -> {
                        null
                    }
                }
        }
    }
}