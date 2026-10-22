package com.chilltrax.radio.domain.usecase

import com.chilltrax.radio.domain.model.Station
import com.chilltrax.radio.domain.repository.StationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveStationsUseCase @Inject constructor(
    private val repository: StationRepository,
) {
    operator fun invoke(): Flow<List<Station>> = repository.observeStations()
}

class GetStationUseCase @Inject constructor(
    private val repository: StationRepository,
) {
    suspend operator fun invoke(id: Long): Station? = repository.getStation(id)
}

class AddStationUseCase @Inject constructor(
    private val repository: StationRepository,
) {
    suspend operator fun invoke(station: Station): Long = repository.addStation(station)
}

class UpdateStationUseCase @Inject constructor(
    private val repository: StationRepository,
) {
    suspend operator fun invoke(station: Station) = repository.updateStation(station)
}

class DeleteStationUseCase @Inject constructor(
    private val repository: StationRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteStation(id)
}
