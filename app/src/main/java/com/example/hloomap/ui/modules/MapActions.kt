package com.example.hloomap.ui.modules

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.hloomap.R

import com.example.hloomap.utils.Constants.PERMISSION_CODE
import com.example.hloomap.utils.HolooResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONException
import java.util.ArrayList
import javax.inject.Inject


class MapActions @Inject constructor() {

    @Inject
    lateinit var currentLocation: CustomLocation
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    suspend fun currentLocation(
        activity: Activity,
    ): Flow<HolooResponse<Location>> {
        return callbackFlow {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_COARSE_LOCATION
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CODE
                )
                return@callbackFlow
            }
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity)
            val task = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener { locations ->
                if (locations != null) {
                    trySend(HolooResponse.success(locations))
                }
            }
            awaitClose()
        }
    }

    suspend fun setDestination(lat: Double, long: Double): Flow<HolooResponse<Location>> {
        return flow {
            currentLocation.latitude = lat
            currentLocation.longitude = long
            emit(HolooResponse.success(currentLocation))
        }.flowOn(Dispatchers.IO)
    }

    fun direction(mMap: GoogleMap, activity: Activity) {
        val requestQueue = Volley.newRequestQueue(activity)
        val url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
            .buildUpon()
            .appendQueryParameter("destination", "-6.9218571, 107.6048254")
            .appendQueryParameter("origin", "-6.9249233, 107.6345122")
            .appendQueryParameter("mode", "driving")
            .appendQueryParameter("key", "AIzaSyCvATz66ZyCOdcl0mt0MRuaUM8rblKxU5M")
            .toString()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                val status = response.getString("status")
//                Toast.makeText(this, status + "", Toast.LENGTH_SHORT).show()
                if (status == "OK") {
                    val routes = response.getJSONArray("routes")
                    var points: ArrayList<LatLng?>
                    var polylineOptions: PolylineOptions? = null
                    for (i in 0 until routes.length()) {
                        points = ArrayList()
                        polylineOptions = PolylineOptions()
                        val legs = routes.getJSONObject(i).getJSONArray("legs")
                        for (j in 0 until legs.length()) {
                            val steps = legs.getJSONObject(j).getJSONArray("steps")
                            for (k in 0 until steps.length()) {
                                val polyline = steps.getJSONObject(k).getJSONObject("polyline")
                                    .getString("points")
                                val list = decodePoly(polyline)
                                for (l in list.indices) {
                                    val position = LatLng(list[l].latitude, list[l].longitude)
                                    points.add(position)
                                }
                            }
                        }
                        polylineOptions.addAll(points)
                        polylineOptions.width(10f)
                        polylineOptions.color(
                            ContextCompat.getColor(
                                activity,
                                R.color.purple_500
                            )
                        )
                        polylineOptions.geodesic(true)
                    }
                    polylineOptions?.let { mMap.addPolyline(it) }
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(-6.9249233, 107.6345122))
                            .title("Marker 1")
                    )
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(-6.9218571, 107.6048254))
                            .title("Marker 1")
                    )
                    val bounds = LatLngBounds.Builder()
                        .include(LatLng(-6.9249233, 107.6345122))
                        .include(LatLng(-6.9218571, 107.6048254)).build()
                    val point = Point()
                    activity.getWindowManager().getDefaultDisplay().getSize(point)
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            point.x,
                            150,
                            30
                        )
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.retryPolicy = retryPolicy
        requestQueue.add(jsonObjectRequest)

    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b > 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }
}