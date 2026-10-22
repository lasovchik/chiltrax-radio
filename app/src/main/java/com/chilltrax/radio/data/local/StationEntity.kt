package com.chilltrax.radio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chilltrax.radio.domain.model.Station

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val streamUrl: String,
    val genre: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)

fun StationEntity.toDomain(): Station = Station(
    id = id,
    name = name,
    streamUrl = streamUrl,
    genre = genre,
    isDefault = isDefault,
)

fun Station.toEntity(): StationEntity = StationEntity(
    id = id,
    name = name.trim(),
    streamUrl = streamUrl.trim(),
    genre = genre.trim(),
    isDefault = isDefault,
)
