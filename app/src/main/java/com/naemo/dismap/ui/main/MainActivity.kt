package com.naemo.dismap.ui.main

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.naemo.dismap.BR
import com.naemo.dismap.R
import com.naemo.dismap.databinding.ActivityMainBinding
import com.naemo.dismap.db.start.StartLocation
import com.naemo.dismap.db.stop.StopLocation
import com.naemo.dismap.ui.base.BaseActivity
import com.naemo.dismap.utils.AppUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator {

    private val PERMISSION_ID = 1

    var mainViewModel: MainViewModel? = null
        @Inject set

    var mLayoutId = R.layout.activity_main
        @Inject set

    var appUtils: AppUtils? = null
        @Inject set

    var mBinder: ActivityMainBinding? = null
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var button: Button
    lateinit var field: EditText
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doBinding()
    }

    private fun initViews() {
        button = mBinder?.buttonMain!!
        field = mBinder?.myDistance!!
        progressBar = mBinder?.progress!!
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun doBinding() {
        mBinder = getViewDataBinding()
        mBinder?.viewModel = mainViewModel
        mBinder?.navigator = this
        mBinder?.viewModel?.setNavigator(this)
        initViews()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): MainViewModel? {
        return mainViewModel
    }

    override fun getLayoutId(): Int {
        return mLayoutId
    }

    override fun fetchLocation() {
        val text = button.text.toString()
        if (text == "Start") {
            getStartLocation()
        } else if (text == "Stop") {
            getStopLocation()
        }

    }

    private fun getStartLocation() {
        progressBar.visibility = View.VISIBLE
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Log.d("start", "no location")
                        requestNewLocation()
                        mBinder?.buttonMain?.text = getString(R.string.stop)
                    } else {
                        Log.d("start", "location")
                        saveToStartDb(location)
                        mBinder?.buttonMain?.text = getString(R.string.stop)
                    }
                }
            } else {
                appUtils?.showSnackBar(this, main_frame, "Turn on location")
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermissions()
        }
    }

    private fun getStopLocation() {
        progressBar.visibility = View.GONE
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Log.d("stop", "no location")
                        requestNewLocation()
                        mBinder?.buttonMain?.text = getString(R.string.start)
                    } else {
                        Log.d("stop", "location")
                        saveToStopDb(location)
                        mBinder?.buttonMain?.text = getString(R.string.start)
                    }
                    getCoordinates()
                }
            } else {
                appUtils?.showSnackBar(this, main_frame, "Turn on location")
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestNewLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private fun saveToStartDb(location: Location) {
        Log.d("start", "save function")
        val longitude = location.longitude
        val latitude = location.latitude
        val startLocation = StartLocation(longitude, latitude)
        getViewModel()?.saveStart(startLocation)
    }

    private fun saveToStopDb(location: Location) {
        Log.d("stop", "save function")
        val longitude = location.longitude
        val latitude = location.latitude
        val stopLocation = StopLocation(longitude, latitude)
        getViewModel()?.saveStop(stopLocation)
    }

    private fun getCoordinates() {
       val from =  getViewModel()?.retrieveStart()
        from?.observe(this, Observer {
            getLocation(it)
        })
    }

    private fun getLocation(startLocation: StartLocation) {
        val to = getViewModel()?.retrieveStop()
        to?.observe(this, Observer {stopLocation ->
            calculateDistance(startLocation, stopLocation)
        })
    }

    private fun calculateDistance(startLocation: StartLocation, stopLocation: StopLocation) {
        val fromLat = startLocation.latitude
        val fromLong = startLocation.longitude
        val toLat = stopLocation.latitude
        val toLong = stopLocation.longitude
        val from = fromLat?.let { fromLong?.let { it1 -> LatLng(it, it1) } }
        val to = toLat?.let { toLong?.let { it1 -> LatLng(it, it1) } }

        val distance = SphericalUtil.computeDistanceBetween(from, to)
        val meters = distance.toString()
        getViewModel()?.textDistance(meters)

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val location: Location? = p0?.lastLocation
            val buttonText = mBinder?.buttonMain?.text.toString()
            if (buttonText == "Start") {
                Log.d("start", "no location call back")
                location?.let {
                    saveToStartDb(it)
                }
            } else if (buttonText == "Stop") {
                Log.d("stop", "no location call back")
                location?.let { saveToStopDb(it) }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("start", "permissions")
                fetchLocation()
            }
        }
    }
}
