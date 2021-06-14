package com.gaps.restaurant

import android.app.Dialog
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gaps.restaurant.classes.Address
import com.gaps.restaurant.classes.DNASnackBar
import com.gaps.restaurant.db.MenuDAO
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.analytics.FirebaseAnalytics
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
    private var fusedLocationClient: FusedLocationProviderClient?=null
    private var lastLocation: Location?=null
    var houseName:String=""
    var streetAddress:String=""
    private var userLocationMarker: Marker? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.gaps.restaurant.R.layout.activity_maps)
        val status = GooglePlayServicesUtil
            .isGooglePlayServicesAvailable(baseContext)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available
            val requestCode = 10
            val dialog: Dialog = GooglePlayServicesUtil.getErrorDialog(
                status, this,
                requestCode
            )
            dialog.show()
        }
        else {
            cardViewAddressBox.visibility = View.VISIBLE
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            val mapFragment = supportFragmentManager
                .findFragmentById(com.gaps.restaurant.R.id.map) as SupportMapFragment
            Log.d("vmap-mapFragment", mapFragment.toString())
            mapFragment.getMapAsync(this)
            Log.d("vmap-mapFragment1", mapFragment.toString())

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            Log.d("vmap-fusedLocation", fusedLocationClient.toString())

            fabAddressDone.setOnClickListener {
                try{
                    if (etHouseName.text.toString() == "" || etStreetAddress.text.toString() == "") {
                        Toast.makeText(this, "Please fill the details", Toast.LENGTH_SHORT).show()

                    }
                    else if(lastLocation==null)
                        Toast.makeText(this, "Click on map to place the marker", Toast.LENGTH_SHORT).show()
                    else {
                        houseName = etHouseName.text.toString()
                        streetAddress = etStreetAddress.text.toString()


                        if (lastLocation!!.latitude > 0) {
                            val add = Address(
                                houseName = houseName,
                                streetAddress = streetAddress,
                                latitude = lastLocation!!.latitude,
                                longitude = lastLocation!!.longitude
                            )
                            CoroutineScope(Dispatchers.Main).launch {
                                db.addAddress(add)

                                finish()


                            }
                        } else {
                            DNASnackBar.show(
                                application,
                                "There's some problem with your network connection."
                            )
                        }

                    }
                }
                catch(err:Exception){
                    DNASnackBar.show(this,"Click on the map to place the marker")
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





        }
        else{


        map.isMyLocationEnabled = true



        fusedLocationClient!!.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))

            }
            if(location==null){
                recreate()
            }

        }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Log.d("vmap-MAPGOOGLE",map.toString())
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



            markerOptions.icon(bitmapDescriptorFromVector(this,vectorResId = com.gaps.restaurant.R.drawable.location_marker))
            markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
            userLocationMarker = map.addMarker(markerOptions)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))

        } else {
            //use the previously created marker
            userLocationMarker!!.position = latLng

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
        }
        if (p != null) {
            lastLocation?.latitude  = p.latitude
            lastLocation?.longitude   =p.longitude
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