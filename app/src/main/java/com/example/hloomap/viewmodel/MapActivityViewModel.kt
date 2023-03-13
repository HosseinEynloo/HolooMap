package com.example.hloomap.viewmodel

import android.app.Activity
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hloomap.data.models.LocationEntity
import com.example.hloomap.data.repository.LocationRepository
import com.example.hloomap.ui.modules.MapActions
import com.example.hloomap.utils.HolooResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapActivityViewModel @Inject constructor(
    private val mapActions: MapActions,
    private val repository: LocationRepository
) : ViewModel() {

    val destinationLocation = MutableLiveData<HolooResponse<Location>>()
    val currentLocation = MutableLiveData<HolooResponse<Location>>()
    val locationMarked = MutableLiveData<LatLng>()

    // DataBase
    val locationsList = MutableLiveData<HolooResponse<MutableList<LocationEntity>>>()


    fun loadCurrentLocation(
        activity: Activity
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            mapActions.currentLocation(activity).collect {
                currentLocation.postValue(it)
            }
        }

    fun loadDestination(latitude: Double, longitude: Double) =
        viewModelScope.launch(Dispatchers.IO) {
            mapActions.setDestination(latitude, longitude).collect {
                destinationLocation.postValue(it)
            }
        }

    fun loadDirection(activity: Activity,mMap:GoogleMap) {
        mapActions.direction(mMap, activity)
    }


    fun saveLocation(location: LocationEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveLocation(location)
        }


    fun getAllLocations() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLocationsList().collect {
                locationsList.postValue(HolooResponse.success(it))
            }
        }


    fun getLocationMarked(latLng: LatLng){
        locationMarked.postValue(latLng)
    }



}