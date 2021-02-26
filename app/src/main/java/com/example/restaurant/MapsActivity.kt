@file:Suppress("DEPRECATION")

package com.example.restaurant

import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.restaurant.classes.Address
import com.example.restaurant.db.MenuDAO
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MapsActivity  : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapClickListener
       {
    @Inject lateinit var db:MenuDAO
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    var houseName:String=""
    var streetAddress:String=""
    private var handler:Handler = Handler()
    private var userLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.restaurant.R.layout.activity_maps)
        cardViewAddressBox.visibility= View.VISIBLE
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(com.example.restaurant.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        fabAddressDone.setOnClickListener {
            if(etHouseName.text.toString() == "" || etStreetAddress.text.toString() == "" ){
                Toast.makeText(this, "Please fill the details", Toast.LENGTH_SHORT).show()

            }
            else{
                houseName= etHouseName.text.toString()
                streetAddress= etStreetAddress.text.toString()
                val add= Address(houseName = houseName, streetAddress = streetAddress, latitude = lastLocation.latitude, longitude = lastLocation.longitude)

                CoroutineScope(Dispatchers.Main).launch{
                        db.addAddress(add)

                    finish()





                }

            }

        }



    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

            recreate()



        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))

            }

        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)


        map.uiSettings.isZoomControlsEnabled = true


        setUpMap()



    }








    override fun onMapClick(p0: LatLng?) {
        placeMarker(p0)
    }

    private fun placeMarker(p: LatLng?) {
        val latLng = p
        if (userLocationMarker == null) {
            //Create a new marker
            val markerOptions = MarkerOptions()
            if (latLng != null) {
                markerOptions.position(latLng)
            }



            markerOptions.icon(bitmapDescriptorFromVector(this,vectorResId = com.example.restaurant.R.drawable.location_marker))
            markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
            userLocationMarker = map.addMarker(markerOptions)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))

        } else {
            //use the previously created marker
            userLocationMarker!!.position = latLng

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
        }
        if (p != null) {
            lastLocation.latitude=p.latitude
            lastLocation.longitude=p.longitude
            Log.d("location", "$lastLocation")
        }

    }
           private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
               return ContextCompat.getDrawable(context, vectorResId)?.run {
                   setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                   val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
                   draw(Canvas(bitmap))
                   BitmapDescriptorFactory.fromBitmap(bitmap)
               }
           }
}