package com.chilltrax.radio.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chilltrax.radio.data.local.StationDao
import com.chilltrax.radio.data.local.StationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStationDatabase(
        @ApplicationContext context: Context,
    ): StationDatabase = Room.databaseBuilder(
        context,
        StationDatabase::class.java,
        StationDatabase.NAME,
    )
        .addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL(
                        """
                        INSERT INTO stations (name, streamUrl, genre, isDefault, createdAt)
                        VALUES (?, ?, ?, 1, ?)
                        """.trimIndent(),
                        arrayOf<Any?>(
                            StationDatabase.DEFAULT_STATION_NAME,
                            StationDatabase.DEFAULT_STREAM_URL,
                            StationDatabase.DEFAULT_GENRE,
                            System.currentTimeMillis(),
                        ),
                    )
                }
            },
        )
        .build()

    @Provides
    fun provideStationDao(database: StationDatabase): StationDao = database.stationDao()
}
