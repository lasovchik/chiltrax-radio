package com.chilltrax.radio.data.remote

import retrofit2.Response
import retrofit2.http.HEAD
import retrofit2.http.Url

interface StreamApi {
    @HEAD
    suspend fun probe(@Url url: String): Response<Unit>
}
