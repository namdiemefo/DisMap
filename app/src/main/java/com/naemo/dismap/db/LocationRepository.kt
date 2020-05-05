package com.naemo.dismap.db

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class LocationRepository(application: Application) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


    private var dbDao: DbDao? = null

    init {
        val dbDatabase = DbDatabase.invoke(application)
        dbDao = dbDatabase.dbDao()
    }


    suspend fun getDb(): Db? {
        val db = getMyDb()
        return db
    }

    private suspend fun getMyDb(): Db? {
       return withContext(IO) {
            dbDao?.loadDb()
        }
    }

    fun saveDb(db: Db) {
        launch {
            save(db)
        }
    }

    private suspend fun save(db: Db) {
        withContext(IO) {
            Log.d("star11", db.id.toString())
            Log.d("star12", db.stop_latitude.toString())
            dbDao?.saveDb(db)
        }
    }

    fun updateStartLongitude(startLongitude: Double?, id: Int) {
        launch {
            updateStartLong(startLongitude, id)
        }
    }

    private suspend fun updateStartLong(startLongitude: Double?, id: Int) {
        withContext(IO){
            dbDao?.saveStartLong(startLongitude, id)
        }
    }

    fun updateStartLatitude(startLatitude: Double?, id: Int) {
        launch {
            updateStartLat(startLatitude, id)
        }
    }

    private suspend fun updateStartLat(startLatitude: Double?, id: Int) {
        withContext(IO){
            dbDao?.saveStartLat(startLatitude, id)
        }
    }

    fun updateStopLongitude(stopLongitude: Double?, id: Int) {
        launch {
            updateStopLong(stopLongitude, id)
        }
    }

    private suspend fun updateStopLong(stopLongitude: Double?, id: Int) {
        withContext(IO){
            dbDao?.saveStopLong(stopLongitude, id)
        }
    }

    fun updateStopLatitude(stopLatitude: Double?, id: Int) {
        launch {
            updateStopLat(stopLatitude, id)
        }
    }

    private suspend fun updateStopLat(stopLatitude: Double?, id: Int) {
        Log.d("star23", stopLatitude.toString())
        withContext(IO){
            dbDao?.saveStopLat(stopLatitude, id)
        }
    }

}