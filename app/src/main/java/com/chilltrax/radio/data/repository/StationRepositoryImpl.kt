package com.chilltrax.radio.data.repository

import com.chilltrax.radio.data.local.StationDao
import com.chilltrax.radio.data.local.toDomain
import com.chilltrax.radio.data.local.toEntity
import com.chilltrax.radio.domain.model.Station
import com.chilltrax.radio.domain.repository.StationRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class StationRepositoryImpl @Inject constructor(
    private val stationDao: StationDao,
) : StationRepository {

    override fun observeStations(): Flow<List<Station>> =
        stationDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getStation(id: Long): Station? = stationDao.getById(id)?.toDomain()

    override suspend fun addStation(station: Station): Long = stationDao.insert(station.toEntity())

    override suspend fun updateStation(station: Station) = stationDao.update(station.toEntity())

    override suspend fun deleteStation(id: Long) = stationDao.deleteById(id)
}
