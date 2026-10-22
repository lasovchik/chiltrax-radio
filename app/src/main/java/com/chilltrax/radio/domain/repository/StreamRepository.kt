package com.chilltrax.radio.domain.repository

import com.chilltrax.radio.domain.model.StationSuggestion

interface StreamRepository {
    suspend fun isReachable(url: String): Boolean
    suspend fun lookupByUrl(url: String): StationSuggestion?
}
