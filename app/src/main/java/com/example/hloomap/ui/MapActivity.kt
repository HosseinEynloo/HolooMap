package com.example.hloomap.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import com.example.hloomap.R
import com.example.hloomap.databinding.ActivityMapsBinding
import com.example.hloomap.utils.Constants.LATITUDE
import com.example.hloomap.utils.Constants.LONGITUDE
import com.example.hloomap.utils.Constants.MY_LOCATION_ZOOM
import com.example.hloomap.utils.Constants.PERMISSION_CODE
import com.example.hloomap.utils.Constants.TEHRAN_LATITUDE
import com.example.hloomap.utils.Constants.TEHRAN_LOCATION_ZOOM
import com.example.hloomap.utils.Constants.TEHRAN_LONGITUDE
import com.example.hloomap.utils.HolooResponse
import com.example.hloomap.utils.PermissionManager
import com.example.hloomap.utils.hideKeyboard
import com.example.hloomap.viewmodel.MapActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap


    @Inject
    lateinit var searchDestinationBSh: SearchDestinationBSh

    @Inject
    lateinit var locationListBottomSheet: LocationListBSh

    @Inject
    lateinit var permissionManager: PermissionManager
    private val viewModel: MapActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callOnMapReady()
        //GET Permission
        permissionManager.requestPermissions(this)

        // Handle Events
        binding.apply {
            currentLoc.setOnClickListener {
                if (isLocationEnabled() && checkGrantedPermission()) {
                    viewModel.loadCurrentLocation(this@MapActivity)
                } else {
                    if (!checkGrantedPermission())
                    permissionManager.requestPermissions(this@MapActivity)
                    showSnack(getString(R.string.EnableGpsMessage))
                }
            }

            listLocation.setOnClickListener {
                locationListBottomSheet.show(supportFragmentManager, "LocationListBottomSheet")
            }

            directionImg.setOnClickListener {
                viewModel.loadDirection(this@MapActivity,mMap)
                showSnack(getString(R.string.direction_message))
            }

            searchLocationBtn.setOnClickListener {
                searchDestinationBSh.show(supportFragmentManager, "SearchDestinationBSh")
            }

            checkBoxEnableLocations.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    viewModel.getAllLocations()
                } else {
                    mMap.clear()
                }
            })


            editTextSearch.setOnClickListener {
                it.hideKeyboard()
                showSnack("Coming Soon")
            }


        }



        // My Location Observer
        viewModel.currentLocation.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                HolooResponse.Status.LOADING -> {
                    // TODO:
                }
                HolooResponse.Status.SUCCESS -> {
                    val latLng = LatLng(it.data!!.latitude, it.data.longitude)
                    val markerOptions =
                        MarkerOptions().position(latLng).title(getString(R.string.IamHere))
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MY_LOCATION_ZOOM))
                    mMap.addMarker(markerOptions)
                }
                HolooResponse.Status.ERROR -> {
                    showSnack(it.message.toString())
                }
            }
        })
        // Destination Location Observer
        viewModel.destinationLocation.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                HolooResponse.Status.LOADING -> {
                    // TODO:
                }
                HolooResponse.Status.SUCCESS -> {
                    val latLng = LatLng(it.data!!.latitude, it.data.longitude)
                    val markerOptions =
                        MarkerOptions().position(latLng).title(getString(R.string.MyDestination))
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                    mMap.addMarker(markerOptions)
                }
                HolooResponse.Status.ERROR -> {
                    showSnack(it.message.toString())
                }
            }
        })


        viewModel.locationsList.observe(this, androidx.lifecycle.Observer {
            if (binding.checkBoxEnableLocations.isChecked) {
                showSnack("Enable saved Locations On Map!")
                for (entity in it.data!!) {
                    setupMarkerLocation(entity.latitude, entity.longitude, entity.title)
                }
            }
        })

    }

    private fun checkGrantedPermission(): Boolean {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant

            return false;
        }

        return true
    }

    private fun showSnack(errorMessage: String) {
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.RED).setTextColor(Color.WHITE)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Go to Tehran Map
        val latLng = LatLng(TEHRAN_LATITUDE, TEHRAN_LONGITUDE)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, TEHRAN_LOCATION_ZOOM))

        mMap.setOnMapClickListener{
            val markerOptions =
                MarkerOptions().position(it).title("my Destination")
            mMap.addMarker(markerOptions)

//            var bundle=Bundle()
//            bundle.putDouble(LATITUDE,it.latitude)
//            bundle.putDouble(LONGITUDE,it.longitude)
//            searchDestinationBSh.arguments=bundle
//            searchDestinationBSh.setupCoordinate(it)
            viewModel.getLocationMarked(it)
            searchDestinationBSh.show(supportFragmentManager,"searchDestinationBSh")
        }

        mMap.setOnMapLongClickListener {
            val markerOptions =
                MarkerOptions().position(it).title("my Destination")
            mMap.addMarker(markerOptions)

//            var bundle=Bundle()
//            bundle.putDouble(LATITUDE,it.latitude)
//            bundle.putDouble(LONGITUDE,it.longitude)
//            searchDestinationBSh.arguments=bundle
//            searchDestinationBSh.setupCoordinate(it)
            viewModel.getLocationMarked(it)
            searchDestinationBSh.show(supportFragmentManager,"searchDestinationBSh")
        }
    }



    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun callOnMapReady() {
        val supportMapFragment =
            (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)
        supportMapFragment!!.getMapAsync(this@MapActivity)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun setupMarkerLocation(latitude: Double, longitude: Double, title: String) {
        val latLng = LatLng(latitude, longitude)
        val markerOptions =
            MarkerOptions().position(latLng).title(title)
        mMap.addMarker(markerOptions)
    }









}