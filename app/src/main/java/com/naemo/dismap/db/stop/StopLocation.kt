package com.naemo.dismap.db.stop



data class StopLocation(
    val latitude: Double?,
    val longitude: Double?
)


/*
@Dao
interface StopLocationDao {

    @Query("SELECT * FROM Stop_Location")
    fun loadStop(): LiveData<StopLocation>

    @Query("SELECT * FROM Stop_Location")
    fun stop(): StopLocation

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStop(location: StopLocation)

}

@Database(entities = [StopLocation::class], version = 3, exportSchema = false)
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
*/
