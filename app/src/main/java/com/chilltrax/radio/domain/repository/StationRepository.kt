package com.chilltrax.radio.domain.repository

import com.chilltrax.radio.domain.model.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {
    fun observeStations(): Flow<List<Station>>
    suspend fun getStation(id: Long): Station?
    suspend fun addStation(station: Station): Long
    suspend fun updateStation(station: Station)
    suspend fun deleteStation(id: Long)
}
