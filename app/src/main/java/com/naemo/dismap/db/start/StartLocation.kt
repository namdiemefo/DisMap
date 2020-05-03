package com.naemo.dismap.db.start

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "Start_Location")
class StartLocation(
    val longitude: Double?,
    val latitude: Double?
) {
    @PrimaryKey(autoGenerate = false)
    var id = 0
}

@Dao
interface StartLocationDao {

    @Query("SELECT * FROM Start_Location")
    fun loadStart(): LiveData<StartLocation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStart(location: StartLocation)

}

@Database(entities = [StartLocation::class], version = 2, exportSchema = false)
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