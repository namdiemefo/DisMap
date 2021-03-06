package com.naemo.dismap.db.start

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


data class StartLocation(
    val start_latitude: Double?,
    val start_longitude: Double?
)

/*
@Dao
interface StartLocationDao {

    @Query("SELECT * FROM Start_Location")
    fun loadStart(): LiveData<StartLocation>

    @Query("SELECT * FROM Start_Location")
    fun start(): StartLocation

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStart(location: StartLocation)

}

@Database(entities = [StartLocation::class], version = 3, exportSchema = false)
abstract class StartLocationDatabase : RoomDatabase() {

    abstract fun startLocationDao(): StartLocationDao

    companion object {
        @Volatile
        private var instance: StartLocationDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, StartLocationDatabase::class.java, "Start_Location")
                .fallbackToDestructiveMigration()
                .build()

    }

}
*/
