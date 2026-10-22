package com.chilltrax.radio.di

import com.chilltrax.radio.data.repository.NetworkMonitor
import com.chilltrax.radio.data.repository.StationRepositoryImpl
import com.chilltrax.radio.data.repository.StreamRepositoryImpl
import com.chilltrax.radio.domain.repository.ConnectivityObserver
import com.chilltrax.radio.domain.repository.StationRepository
import com.chilltrax.radio.domain.repository.StreamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStationRepository(impl: StationRepositoryImpl): StationRepository

    @Binds
    @Singleton
    abstract fun bindStreamRepository(impl: StreamRepositoryImpl): StreamRepository

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(impl: NetworkMonitor): ConnectivityObserver
}
