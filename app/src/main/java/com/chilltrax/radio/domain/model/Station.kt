package com.chilltrax.radio.domain.model

data class Station(
    val id: Long = 0L,
    val name: String,
    val streamUrl: String,
    val genre: String,
    val isDefault: Boolean = false,
) {
    val displayGenre: String
        get() = genre.ifBlank { "Chillout" }
}
