package com.example.restaurant

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.restaurant.api.Network
import com.example.restaurant.classes.Address
import com.example.restaurant.db.MenuDAO
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject


@AndroidEntryPoint
class MapsActivityOrders  : AppCompatActivity(), OnMapReadyCallback
     {
         @Inject lateinit var api: Network
         private lateinit var handler: Handler
         private lateinit var runnable: Runnable
         private var interval: Long = 1000L
        private lateinit var map: GoogleMap
        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private lateinit var lastLocation: Location
        lateinit var phone:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        cardViewAddressBox.visibility= View.GONE
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        phone= intent.extras?.getString("DbPhone","").toString()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
        CoroutineScope(Main).launch {
            val a=api.getRider(phone).body()

            if (a != null) {
                a.latitude?.let { a.longitude?.let { it1 -> LatLng(it.toDouble(), it1.toDouble()) } }?.let {
                    placeMarkerOnMap(
                        it
                    )
                }
            }
            Log.d("GetLocationActivated",a.toString()+"kjhk"+phone)

        }

        /*handler = Handler(Looper.getMainLooper())
        runnable=Runnable{
            updateMap(handler)
        }*/


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

         private fun updateMap(handler: Handler) {
             CoroutineScope(IO).launch {
                 val a=api.getRider(phone).body()

                 lastLocation.latitude= a?.latitude?.toDouble()!!
                 lastLocation.longitude= a.longitude?.toDouble()!!
                 Log.d("GetLocationActivated",a.toString()+"kjhk")

             }
             placeMarkerOnMap(LatLng(lastLocation.latitude,lastLocation.longitude))
             handler.postDelayed(runnable,5000)
         }

         companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = false

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)

        // 2

        map.addMarker(markerOptions)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true


        setUpMap()



    }




}
