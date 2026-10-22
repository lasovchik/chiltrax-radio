package com.chilltrax.radio.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chilltrax.radio.R
import com.chilltrax.radio.domain.model.Station
import com.chilltrax.radio.domain.usecase.DeleteStationUseCase
import com.chilltrax.radio.domain.usecase.ObserveConnectivityUseCase
import com.chilltrax.radio.domain.usecase.ObserveStationsUseCase
import com.chilltrax.radio.playback.RadioPlayer
import com.chilltrax.radio.presentation.common.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RadioUiState(
    val stations: List<Station> = emptyList(),
    val selectedStation: Station? = null,
    val currentStationId: Long? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isConnected: Boolean = false,
    val isOnline: Boolean = true,
    val volume: Float = 1f,
    val isLoading: Boolean = true,
)

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val observeStations: ObserveStationsUseCase,
    observeConnectivity: ObserveConnectivityUseCase,
    private val deleteStationUseCase: DeleteStationUseCase,
    private val player: RadioPlayer,
) : ViewModel() {

    private val selectedStationId = MutableStateFlow<Long?>(null)

    private val _events = Channel<UiMessage>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val uiState: StateFlow<RadioUiState> = combine(
        observeStations(),
        selectedStationId,
        player.state,
        observeConnectivity(),
    ) { stations, selectedId, playback, isOnline ->
        val selected = stations.firstOrNull { it.id == selectedId }
            ?: stations.firstOrNull()

        RadioUiState(
            stations = stations,
            selectedStation = selected,
            currentStationId = playback.currentStationId,
            isPlaying = playback.isPlaying && selected != null &&
                playback.currentStationId == selected.id,
            isBuffering = playback.isBuffering && selected != null &&
                playback.currentStationId == selected.id,
            isConnected = playback.isConnected,
            isOnline = isOnline,
            volume = playback.volume,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RadioUiState(),
    )

    init {
        player.connect()

        viewModelScope.launch {
            observeStations().collect { stations ->
                val currentSelection = selectedStationId.value
                if (currentSelection == null || stations.none { it.id == currentSelection }) {
                    selectedStationId.value = stations.firstOrNull()?.id
                }
            }
        }

        viewModelScope.launch {
            player.errors.collect { error ->
                _events.send(UiMessage.Text("Ошибка воспроизведения: $error"))
            }
        }
    }

    fun onStationClick(station: Station) {
        selectedStationId.value = station.id
        if (!uiState.value.isOnline) {
            viewModelScope.launch {
                _events.send(UiMessage.Resource(R.string.connection_error))
            }
            return
        }
        player.play(station)
    }

    fun onTogglePlayback() {
        val state = uiState.value
        val station = state.selectedStation ?: return

        when {
            state.isPlaying -> player.pause()
            !state.isOnline -> viewModelScope.launch {
                _events.send(UiMessage.Resource(R.string.connection_error))
            }
            state.currentStationId == station.id && state.isConnected -> player.resume()
            else -> player.play(station)
        }
    }

    fun onVolumeChange(value: Float) {
        player.setVolume(value)
    }

    fun onDeleteStation(station: Station) {
        viewModelScope.launch {
            runCatching { deleteStationUseCase(station.id) }
                .onSuccess {
                    if (player.state.value.currentStationId == station.id) {
                        player.stop()
                    }
                    _events.send(UiMessage.Resource(R.string.station_deleted))
                }
                .onFailure {
                    _events.send(UiMessage.Resource(R.string.delete_failed))
                }
        }
    }

}
