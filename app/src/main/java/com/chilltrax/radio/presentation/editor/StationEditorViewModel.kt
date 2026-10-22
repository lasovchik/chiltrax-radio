package com.chilltrax.radio.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chilltrax.radio.R
import com.chilltrax.radio.domain.model.Station
import com.chilltrax.radio.domain.usecase.AddStationUseCase
import com.chilltrax.radio.domain.usecase.CheckStreamUseCase
import com.chilltrax.radio.domain.usecase.GetStationUseCase
import com.chilltrax.radio.domain.usecase.LookupStationUseCase
import com.chilltrax.radio.domain.usecase.UpdateStationUseCase
import com.chilltrax.radio.domain.validation.StationInputValidator
import com.chilltrax.radio.presentation.common.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StationEditorUiState(
    val isEditing: Boolean = false,
    val name: String = "",
    val streamUrl: String = "",
    val genre: String = "",
    val nameError: Boolean = false,
    val urlError: Boolean = false,
    val isSaving: Boolean = false,
    val isChecking: Boolean = false,
    val isLoading: Boolean = false,
)

sealed interface EditorEvent {
    data object Saved : EditorEvent
    data class Message(val message: UiMessage) : EditorEvent
}

@HiltViewModel
class StationEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStationUseCase: GetStationUseCase,
    private val addStationUseCase: AddStationUseCase,
    private val updateStationUseCase: UpdateStationUseCase,
    private val checkStreamUseCase: CheckStreamUseCase,
    private val lookupStationUseCase: LookupStationUseCase,
) : ViewModel() {

    private val stationId: Long = savedStateHandle.get<Long>(ARG_STATION_ID) ?: NO_STATION_ID

    private val _uiState = MutableStateFlow(
        StationEditorUiState(
            isEditing = stationId != NO_STATION_ID,
            isLoading = stationId != NO_STATION_ID,
        ),
    )
    val uiState: StateFlow<StationEditorUiState> = _uiState.asStateFlow()

    private val _events = Channel<EditorEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        if (stationId != NO_STATION_ID) {
            loadStation()
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = false) }
    }

    fun onUrlChange(value: String) {
        _uiState.update { it.copy(streamUrl = value, urlError = false) }
    }

    fun onGenreChange(value: String) {
        _uiState.update { it.copy(genre = value) }
    }

    fun onSave() {
        val state = _uiState.value
        val name = state.name.trim()
        val url = state.streamUrl.trim()
        val genre = state.genre.trim()

        val nameError = name.isBlank()
        val urlError = !StationInputValidator.isValidStreamUrl(url)

        if (nameError || urlError) {
            _uiState.update { it.copy(nameError = nameError, urlError = urlError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, nameError = false, urlError = false) }

            val station = Station(
                id = if (state.isEditing) stationId else 0L,
                name = name,
                streamUrl = url,
                genre = genre,
            )

            runCatching {
                if (state.isEditing) {
                    updateStationUseCase(station)
                } else {
                    addStationUseCase(station)
                }
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                _events.send(EditorEvent.Saved)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isSaving = false) }
                _events.send(
                    EditorEvent.Message(
                        UiMessage.Text(
                            throwable.message ?: "Не удалось сохранить станцию",
                        ),
                    ),
                )
            }
        }
    }

    fun onCheckStream() {
        val url = _uiState.value.streamUrl.trim()
        if (!StationInputValidator.isValidStreamUrl(url)) {
            _uiState.update { it.copy(urlError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true) }
            val available = runCatching { checkStreamUseCase(url) }.getOrDefault(false)
            _uiState.update { it.copy(isChecking = false) }
            _events.send(
                EditorEvent.Message(
                    UiMessage.Resource(
                        if (available) R.string.stream_available else R.string.stream_unavailable,
                    ),
                ),
            )
        }
    }

    fun onLookup() {
        val url = _uiState.value.streamUrl.trim()
        if (!StationInputValidator.isValidStreamUrl(url)) {
            _uiState.update { it.copy(urlError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true) }
            val suggestion = runCatching { lookupStationUseCase(url) }.getOrNull()
            _uiState.update { current ->
                current.copy(
                    isChecking = false,
                    name = suggestion?.name?.takeIf { it.isNotBlank() } ?: current.name,
                    genre = suggestion?.genre?.takeIf { it.isNotBlank() } ?: current.genre,
                )
            }
            _events.send(
                EditorEvent.Message(
                    UiMessage.Resource(
                        if (suggestion != null) {
                            R.string.catalog_found
                        } else {
                            R.string.catalog_not_found
                        },
                    ),
                ),
            )
        }
    }

    private fun loadStation() {
        viewModelScope.launch {
            val station = runCatching { getStationUseCase(stationId) }.getOrNull()
            if (station == null) {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(EditorEvent.Message(UiMessage.Resource(R.string.station_not_found)))
                return@launch
            }
            _uiState.update {
                it.copy(
                    name = station.name,
                    streamUrl = station.streamUrl,
                    genre = station.genre,
                    isLoading = false,
                )
            }
        }
    }

    companion object {
        const val ARG_STATION_ID = "stationId"
        const val NO_STATION_ID = -1L
    }
}
