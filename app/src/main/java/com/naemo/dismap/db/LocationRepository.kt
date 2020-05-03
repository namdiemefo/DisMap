package com.naemo.dismap.db

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.naemo.dismap.db.start.StartLocation
import com.naemo.dismap.db.start.StartLocationDao
import com.naemo.dismap.db.start.StartLocationDatabase
import com.naemo.dismap.db.stop.StopLocation
import com.naemo.dismap.db.stop.StopLocationDao
import com.naemo.dismap.db.stop.StopLocationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class LocationRepository(application: Application) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var startLocationDao: StartLocationDao? = null
    private var stopLocationDao: StopLocationDao? = null

    init {
        val startDatabase = StartLocationDatabase.invoke(application)
        val stopDatabase = StopLocationDatabase.invoke(application)
        startLocationDao = startDatabase.startLocationDao()
        stopLocationDao = stopDatabase.stopLocationDao()
    }

    fun retrieveStartLocation(): LiveData<StartLocation>? {
        return startLocationDao?.loadStart()
    }

    fun retrieveStopLocation(): LiveData<StopLocation>? {
        return stopLocationDao?.loadStop()
    }

    fun saveStartLocation(location: StartLocation) {
        launch {
            saveStart(location)
        }
    }

    private suspend fun saveStart(location: StartLocation) {
        Log.d("startLag", location.latitude.toString())
        Log.d("startLog", location.longitude.toString())
        withContext(IO){
            startLocationDao?.saveStart(location)
        }
    }

    fun saveStopLocation(location: StopLocation) {
        launch {
            saveStop(location)
        }
    }

    private suspend fun saveStop(location: StopLocation) {
        Log.d("stopLag", location.latitude.toString())
        Log.d("stopLog", location.longitude.toString())
        withContext(IO){
            stopLocationDao?.saveStop(location)
        }
    }
}