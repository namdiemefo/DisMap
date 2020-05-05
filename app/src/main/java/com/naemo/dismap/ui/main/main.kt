package com.naemo.dismap.ui.main

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.naemo.dismap.R
import com.naemo.dismap.db.Db
import com.naemo.dismap.db.LocationRepository
import com.naemo.dismap.db.start.StartLocation
import com.naemo.dismap.db.stop.StopLocation
import com.naemo.dismap.ui.base.BaseViewModel
import dagger.Module
import dagger.Provides


class MainViewModel(application: Application) : BaseViewModel<MainNavigator>(application){

    var distance = ObservableField("")

    private var repository = LocationRepository(application)

    fun textCalculating() {
        distance.set("Calculating distance ....")
    }

    fun calcDistance(startLocation: StartLocation, stopLocation: StopLocation) {
        val fromLat = startLocation.start_latitude
        val fromLong = startLocation.start_longitude
        val toLat = stopLocation.latitude
        val toLong = stopLocation.longitude
        val from = fromLat?.let { fromLong?.let { it1 -> LatLng(it, it1) } }
        val to = toLat?.let { toLong?.let { it1 -> LatLng(it, it1) } }

        val myDistance = SphericalUtil.computeDistanceBetween(from, to)
        val results = FloatArray(10)
       // val ss = Location.distanceBetween(fromLat!!, fromLong!!, toLat!!, toLong!!, results)
      //  val meters = myDistance.toString()
        distance.set(myDistance.toString()+"m")
    }

    suspend fun getDb(): Db? {
        return repository.getDb()
    }

    fun save(db: Db) {
        repository.saveDb(db)
    }

    fun updateStartLong(startLongitude: Double?, id: Int) {
        repository.updateStartLongitude(startLongitude, id)
    }

    fun updateStartLat(startLatitude: Double?, id: Int) {
        repository.updateStartLatitude(startLatitude, id)
    }

    fun updateStopLong(stopLongitude: Double?, id: Int) {
        repository.updateStopLongitude(stopLongitude, id)
    }

    fun updateStopLat(stopLatitude: Double?, id: Int) {
        Log.d("star13", stopLatitude.toString())
        repository.updateStopLatitude(stopLatitude, id)
    }



}

interface MainNavigator {

    fun fetchLocation()


}

@Module
class MainModule {

    @Provides
    fun providesMainViewModel(application: Application): MainViewModel {
        return MainViewModel(application)
    }

    @Provides
    fun layoutId(): Int {
        return R.layout.activity_main
    }
}