package com.naemo.dismap.ui.main

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import com.google.android.gms.common.api.GoogleApiClient
import com.naemo.dismap.R
import com.naemo.dismap.db.LocationRepository
import com.naemo.dismap.db.start.StartLocation
import com.naemo.dismap.db.stop.StopLocation
import com.naemo.dismap.ui.base.BaseViewModel
import dagger.Module
import dagger.Provides

class MainViewModel(application: Application) : BaseViewModel<MainNavigator>(application) {

    var distance = ObservableField("")

    private var repository = LocationRepository(application)

    fun textCalculating() {
        distance.set("Calculating distance ....")
    }

    fun textDistance(meters: String) {
        distance.set(meters+"m")
    }

    fun retrieveStart(): LiveData<StartLocation>? {
        return repository.retrieveStartLocation()
    }

    fun retrieveStop(): LiveData<StopLocation>? {
        return repository.retrieveStopLocation()
    }

    fun saveStart(startLocation: StartLocation) {
        Log.d("start", "go to repo")
        repository.saveStartLocation(startLocation)
    }

    fun saveStop(stopLocation: StopLocation) {
        Log.d("stop", "go to repo")
        repository.saveStopLocation(stopLocation)
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