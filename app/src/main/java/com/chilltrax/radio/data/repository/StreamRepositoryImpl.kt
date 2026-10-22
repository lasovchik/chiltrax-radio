package com.chilltrax.radio.data.repository

import com.chilltrax.radio.data.remote.RadioBrowserApi
import com.chilltrax.radio.data.remote.StreamApi
import com.chilltrax.radio.domain.model.StationSuggestion
import com.chilltrax.radio.domain.repository.StreamRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class StreamRepositoryImpl @Inject constructor(
    private val streamApi: StreamApi,
    private val radioBrowserApi: RadioBrowserApi,
) : StreamRepository {

    override suspend fun isReachable(url: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val response = streamApi.probe(url)
            response.isSuccessful || response.code() in 200..399
        }.getOrDefault(false)
    }

    override suspend fun lookupByUrl(url: String): StationSuggestion? = withContext(Dispatchers.IO) {
        runCatching {
            radioBrowserApi.stationsByUrl(url)
                .firstOrNull { it.name.isNotBlank() }
                ?.let { dto ->
                    StationSuggestion(
                        name = dto.name.trim(),
                        genre = dto.tags
                            .split(',')
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .take(3)
                            .joinToString(", "),
                    )
                }
        }.getOrNull()
    }
}
