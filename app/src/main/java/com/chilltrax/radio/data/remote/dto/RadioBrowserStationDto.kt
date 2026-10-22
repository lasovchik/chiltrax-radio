package com.chilltrax.radio.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioBrowserStationDto(
    @SerialName("stationuuid") val stationUuid: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("url_resolved") val resolvedUrl: String = "",
    @SerialName("tags") val tags: String = "",
    @SerialName("country") val country: String = "",
)
