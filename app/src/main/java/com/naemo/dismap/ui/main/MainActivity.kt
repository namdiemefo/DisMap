package com.naemo.dismap.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.naemo.dismap.BR
import com.naemo.dismap.R
import com.naemo.dismap.databinding.ActivityMainBinding
import com.naemo.dismap.db.Db
import com.naemo.dismap.db.start.StartLocation
import com.naemo.dismap.db.stop.StopLocation
import com.naemo.dismap.ui.base.BaseActivity
import com.naemo.dismap.utils.AppUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

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
    var id: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doBinding()
    }

    private fun initViews() {
        button = mBinder?.buttonMain!!
        field = mBinder?.myDistance!!
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val db = Db(1, 0.0, 0.0, 0.0, 0.0)
        getViewModel()?.save(db)
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
        getViewModel()?.textCalculating()
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
                        id?.let { saveToStartDb(location, it) }
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
                        id?.let { saveToStopDb(location, it) }
                        mBinder?.buttonMain?.text = getString(R.string.start)
                    }
                    Log.d("check", "check1")
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

    private fun saveToStartDb(location: Location, id: Int) {
        Log.d("start", "save function")
        val longitude = location.longitude
        Log.d("star7", longitude.toString())
        val latitude = location.latitude
        Log.d("star8", latitude.toString())
        getViewModel()?.updateStartLong(longitude, id)
        getViewModel()?.updateStartLat(latitude, id)
    }

    private fun saveToStopDb(location: Location, id: Int) {
        Log.d("stop", "save function")
        val longitude = location.longitude
        Log.d("star5", longitude.toString())
        val latitude = location.latitude
        getViewModel()?.updateStopLong(longitude, id)
        Log.d("star6", latitude.toString())
        getViewModel()?.updateStopLat(latitude, id)

    }

    private fun getCoordinates() {
        Log.d("check", "check2")
        launch {
            val db = getViewModel()?.getDb()
            calcDistance(db)
        }


    }

    private fun calcDistance(it: Db?) {
        val startLatitude = it?.start_latitude
        Log.d("star1", startLatitude.toString())
        val startLongitude = it?.start_longitude
        Log.d("star2", startLongitude.toString())
        val stopLatitude = it?.stop_latitude
        Log.d("star3", stopLatitude.toString())
        val stopLongitude = it?.stop_longitude
        Log.d("star4", stopLongitude.toString())

        val startLocation = StartLocation(startLatitude, startLongitude)
        val stopLocation = StopLocation(stopLatitude, stopLongitude)

      getViewModel()?.calcDistance(startLocation, stopLocation)

    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val location: Location? = p0?.lastLocation
            val buttonText = mBinder?.buttonMain?.text.toString()
            if (buttonText == "Start") {
                Log.d("start", "no location call back")
                location?.let {
                    id?.let { it1 -> saveToStartDb(it, it1) }
                }
            } else if (buttonText == "Stop") {
                Log.d("stop", "no location call back")
                location?.let { id?.let { it1 -> saveToStopDb(it, it1) } }
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
                //fetchLocation()
            }
        }
    }

}
