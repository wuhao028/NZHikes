package com.hao.data.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hao.data.data.converter.CoordinateListConverter
import com.hao.data.data.converter.StringListConverter
import com.hao.data.data.model.Track
import com.hao.data.local.HikeDao
import com.hao.data.model.Hike

/**
 * Room database for storing track information
 */
@Database(
    entities = [Track::class, Hike::class],
    version = 2, // Incremented version due to schema change
    exportSchema = false
)
@TypeConverters(CoordinateListConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun hikeDao(): HikeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Simple migration strategy
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
