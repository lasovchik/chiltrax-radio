package com.chilltrax.radio.domain.usecase

import com.chilltrax.radio.domain.model.StationSuggestion
import com.chilltrax.radio.domain.repository.ConnectivityObserver
import com.chilltrax.radio.domain.repository.StreamRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveConnectivityUseCase @Inject constructor(
    private val connectivityObserver: ConnectivityObserver,
) {
    operator fun invoke(): Flow<Boolean> = connectivityObserver.isOnline
}

class CheckStreamUseCase @Inject constructor(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(url: String): Boolean = streamRepository.isReachable(url)
}

class LookupStationUseCase @Inject constructor(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(url: String): StationSuggestion? = streamRepository.lookupByUrl(url)
}
