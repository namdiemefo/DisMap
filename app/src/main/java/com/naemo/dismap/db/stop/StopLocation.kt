package com.naemo.dismap.db.stop

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "Stop_Location")
class StopLocation(
    val longitude: Double?,
    val latitude: Double?
) {
    @PrimaryKey(autoGenerate = false)
    var id = 0
}

@Dao
interface StopLocationDao {

    @Query("SELECT * FROM Stop_Location")
    fun loadStop(): LiveData<StopLocation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStop(location: StopLocation)

}

@Database(entities = [StopLocation::class], version = 2, exportSchema = false)
abstract class StopLocationDatabase : RoomDatabase() {

    abstract fun stopLocationDao(): StopLocationDao

    companion object {
        @Volatile
        private var instance: StopLocationDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, StopLocationDatabase::class.java, "Stop_Location")
                .fallbackToDestructiveMigration()
                .build()

    }

}