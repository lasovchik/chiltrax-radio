package com.chilltrax.radio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StationEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class StationDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao

    companion object {
        const val NAME = "chilltrax.db"

        const val DEFAULT_STATION_NAME = "Chilltrax"
        const val DEFAULT_STREAM_URL = "https://streamssl.chilltrax.com/stream.mp3"
        const val DEFAULT_GENRE = "Chillout"
    }
}
