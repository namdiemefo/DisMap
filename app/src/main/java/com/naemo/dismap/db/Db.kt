package com.naemo.dismap.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "Db")
data class Db(
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    @ColumnInfo(name = "start_longitude")
    val start_longitude: Double?,
    @ColumnInfo(name = "start_latitude")
    val start_latitude: Double?,
    @ColumnInfo(name = "stop_longitude")
    val stop_longitude: Double?,
    @ColumnInfo(name = "stop_latitude")
    val stop_latitude: Double?
)

@Dao
interface DbDao {

    @Query("SELECT * FROM Db")
    fun loadDb(): Db

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveDb(db: Db)

    @Query("UPDATE Db SET start_longitude=:startLongitude WHERE id=:id")
    fun saveStartLong(startLongitude: Double?, id: Int)

    @Query("UPDATE Db SET start_latitude=:startLatitude WHERE id= :id")
    fun saveStartLat(startLatitude: Double?, id: Int)

    @Query("UPDATE Db SET stop_longitude=:stopLongitude WHERE id = :id")
    fun saveStopLong(stopLongitude: Double?, id: Int)

    @Query("UPDATE Db SET stop_latitude=:stopLatitude WHERE id = :id")
    fun saveStopLat(stopLatitude: Double?, id: Int)



}

@Database(entities = [Db::class], version = 6, exportSchema = false)
abstract class DbDatabase : RoomDatabase() {

    abstract fun dbDao(): DbDao

    companion object {
        @Volatile
        private var instance: DbDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, DbDatabase::class.java, "Db")
                .fallbackToDestructiveMigration()
                .build()

    }

}
