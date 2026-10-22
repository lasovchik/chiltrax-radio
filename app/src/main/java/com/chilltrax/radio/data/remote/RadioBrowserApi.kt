package com.chilltrax.radio.data.remote

import com.chilltrax.radio.data.remote.dto.RadioBrowserStationDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RadioBrowserApi {
    @GET("json/stations/byurl")
    suspend fun stationsByUrl(@Query("url") url: String): List<RadioBrowserStationDto>
}
