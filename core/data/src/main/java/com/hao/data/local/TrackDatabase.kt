package com.hao.data.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hao.data.data.converter.CoordinateListConverter
import com.hao.data.data.converter.StringListConverter
import com.hao.data.data.model.Track

/**
 * Room database for storing track information
 */
@Database(
    entities = [Track::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CoordinateListConverter::class, StringListConverter::class)
abstract class TrackDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    companion object {
        @Volatile
        private var INSTANCE: TrackDatabase? = null

        fun getDatabase(context: Context): TrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackDatabase::class.java,
                    "tracks_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
