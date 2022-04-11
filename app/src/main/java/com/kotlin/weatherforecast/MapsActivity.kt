package com.kotlin.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kotlin.weatherforecast.databinding.ActivityMapsBinding
import com.kotlin.weatherforecast.db.ConcreteLocalSource
import com.kotlin.weatherforecast.home.HomeActivity
import com.kotlin.weatherforecast.home.favourites.viewmodel.FavouritesViewModel
import com.kotlin.weatherforecast.home.favourites.viewmodel.FavouritesViewModelFactory
import com.kotlin.weatherforecast.model.FavLocationsModel
import com.kotlin.weatherforecast.model.WeatherRepository
import com.kotlin.weatherforecast.network.ApiClient
import com.kotlin.weatherforecast.utils.Constants
import com.kotlin.weatherforecast.utils.MyPreference
import java.io.IOException
import java.security.AccessController.getContext
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapDialogCommunicator {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    val LOCATION_PERMISSION_ID = 10
    var currentLocation : Location? = null

    lateinit var mapDialog: MapDialog

    lateinit var preference: MyPreference

    lateinit var latLocation : String
    lateinit var longLocation : String

    lateinit var viewModel: FavouritesViewModel
    lateinit var vmFactory : FavouritesViewModelFactory


   // var id :Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = MyPreference.getInstance(this)!!
        mapDialog = MapDialog(this)

        vmFactory = FavouritesViewModelFactory(
            WeatherRepository.getRepoInstance(
                ApiClient.getClientInstance()!!,
                ConcreteLocalSource(this),
                this
            ),this
        )

        viewModel = ViewModelProvider(this,vmFactory).get(FavouritesViewModel::class.java)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocation()

    }


    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        if (checkPermissions()) {
            Log.d("TAG", "fetchLocation: 97 ${locationEnabled()}")
            if (locationEnabled()) {
                Log.d("TAG", "fetchLocation: 99")
                fusedLocationProviderClient?.lastLocation?.addOnCompleteListener { location ->
                    if (location != null){
                        this.currentLocation = location.result
                        val mapFragment = supportFragmentManager
                            .findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)
                    }
                    // requestNewLocationData()
                }
            } else {
                displayPromptForEnablingGPS(this)
            }
        } else {
            requestLocationPermission()
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

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
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
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
                fetchLocation()
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener {
            mMap.clear()
            Log.d("TAG", "onMapReady: CLICK LAT ${it.latitude}")
            Log.d("TAG", "onMapReady: CLICK LONG ${it.longitude}")

            latLocation = it.latitude.toString()
            longLocation = it.longitude.toString()


            mMap.addMarker(
                MarkerOptions().position(it).draggable(true).title("my location")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(it))
            //mMap.animateCamera(new CameraUpdateFactory.newLatLngZoom(myHome,16.0f));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16.0f))
            mapDialog.show(supportFragmentManager, "MyCustomFragment")
        }
        fusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
            val mLocation = task.result
            if (mLocation != null) {
                val myHome = LatLng(mLocation.latitude, mLocation.longitude)
                Log.d("TAG", "onMapReady: LAT ${mLocation.latitude}")
                Log.d("TAG", "onMapReady: LONG ${mLocation.longitude}")
                mMap.addMarker(
                    MarkerOptions().position(myHome).draggable(true).title("my location")
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myHome))
                //mMap.animateCamera(new CameraUpdateFactory.newLatLngZoom(myHome,16.0f));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myHome, 16.0f))
               // mMap.uiSettings.isZoomControlsEnabled = true;

            }
        }

    }

    //after clicking on save button on dialog of map after choosing the location
    override fun pickedLocation() {
        if (intent.getIntExtra("from fav",0) == 1){
            Log.d("TAG", "pickedLocation: inside PICKED LOCATION FROM FAV")
            getAddressFromLocation(latLocation,longLocation)
        }
        else{

            preference.saveData(Constants.LAT,latLocation)
            preference.saveData(Constants.LONG,longLocation)
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }

    fun getAddressFromLocation(lat : String, long : String)  {
        var countryName : String
        val geocoder = Geocoder(this, Locale.getDefault()) //get default language
        try {

           val addresses = geocoder.getFromLocation(lat.toDouble(), long.toDouble(), 1)
            countryName = addresses[0].getAddressLine(0)

            Log.d("TAG", "getAddressFromLocation: $countryName")
           // binding.govTxt.text = (addresses[0].getAddressLine(0) + " " + addresses[0].countryName)
            //binding.govTxt.text = it.timezone
            viewModel.insertLocation(FavLocationsModel(id = lat+long,lat = lat.toDouble(),lon = long.toDouble(), location = countryName))
            Toast.makeText(this,"Location added",Toast.LENGTH_SHORT).show()
           // id++

            finish()


        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
