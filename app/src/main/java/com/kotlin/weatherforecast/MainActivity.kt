package com.kotlin.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.kotlin.weatherforecast.home.HomeActivity
import com.kotlin.weatherforecast.home.homefragment.viewmodel.HomeViewModel
import com.kotlin.weatherforecast.utils.Constants
import com.kotlin.weatherforecast.utils.MyPreference
import java.util.*

class MainActivity : AppCompatActivity(), DialogActivityCommunicator {
    lateinit var initialSetupDialog: InitialSetupDialog

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val LOCATION_PERMISSION_ID = 10

    lateinit var preference: MyPreference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preference = MyPreference.getInstance(this)!!
        setLocale(this,preference.getCurrentLang(Constants.LANGUAGE))
        setContentView(R.layout.activity_main)



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        initialSetupDialog = InitialSetupDialog(this)
        initialSetupDialog.show(supportFragmentManager, "MyCustomFragment")

//        initialSetupDialog.ok_btn.setOnClickListener {
//            startActivity(Intent(this, HomeActivity::class.java))
//        }

//        InitialSetupDialog().ok_btn.setOnClickListener {
//            startActivity(Intent(this, HomeActivity::class.java))
//
//        }
    }

    override fun fetchGpsLocation() {
        getLastLocation()
    }

    override fun fetchMapLocation() {
        startActivity(Intent(this, MapsActivity::class.java))
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (locationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val mLocation = task.result
                    requestNewLocationData()
                }
            } else {
                displayPromptForEnablingGPS(this)
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun locationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                requestLocationPermission()
            }
        }
    }

    fun displayPromptForEnablingGPS(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        val message = "Do you want open GPS setting?"
        builder.setMessage(message)
            .setPositiveButton(
                "OK"
            ) { d, id ->
                activity.startActivity(Intent(action))
                d.dismiss()
            }
            .setNegativeButton(
                "Cancel"
            ) { d, id -> d.cancel() }
        builder.create().show()
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        @SuppressLint("RestrictedApi") val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.equals(null)) {
                    return
                }
                val mLocation = locationResult.lastLocation

                saveLatLongToShared(mLocation.latitude.toString(),mLocation.longitude.toString())

                Log.d("TAG", "onLocationResult: LAT ${mLocation.latitude}")
                Log.d("TAG", "onLocationResult: LONG ${mLocation.longitude}")

                 startActivity(Intent(this@MainActivity,HomeActivity::class.java))

            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private fun saveLatLongToShared(lat: String, long : String) {
       preference.saveData(Constants.LAT,lat)
       preference.saveData(Constants.LONG,long)
    }
    fun setLocale(activity: Activity, languageCode: String?) {//ar ,en
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
    }

}