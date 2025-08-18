package com.hao.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hao.data.data.converter.CoordinateListConverter
import com.hao.data.data.converter.StringListConverter
import com.hao.data.data.local.TrackDao
import com.hao.data.data.model.RemoteTrack
import com.hao.data.model.Campsite
import com.hao.data.model.Hut
import com.hao.data.model.LocalTrack
import com.hao.data.util.readJsonFromAssets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [RemoteTrack::class, LocalTrack::class, Campsite::class, Hut::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(CoordinateListConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun hikeDao(): HikeDao
    abstract fun campsiteDao(): CampsiteDao
    abstract fun hutDao(): HutDao

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
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    prePopulate(context, it)
                }
            }
        }

        private suspend fun prePopulate(context: Context, database: AppDatabase) {
            val gson = Gson()
            // Pre-populate campsites
            val campsiteJson = readJsonFromAssets(context, "allCampsites.json")
            val campsiteType = object : TypeToken<List<Campsite>>() {}.type
            val campsites: List<Campsite> = gson.fromJson(campsiteJson, campsiteType)
            database.campsiteDao().insertAll(campsites)

            // Pre-populate huts
            val hutJson = readJsonFromAssets(context, "allHuts.json")
            val hutType = object : TypeToken<List<Hut>>() {}.type
            val huts: List<Hut> = gson.fromJson(hutJson, hutType)
            database.hutDao().insertAll(huts)
        }
    }
}
