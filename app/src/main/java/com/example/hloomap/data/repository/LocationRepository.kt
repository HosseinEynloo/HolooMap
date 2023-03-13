package com.example.hloomap.data.repository

import com.example.hloomap.data.local.LocationDao
import com.example.hloomap.data.models.LocationEntity
import javax.inject.Inject

class LocationRepository @Inject constructor(private val dao: LocationDao) {
    suspend fun saveLocation(entity: LocationEntity) = dao.insertLocation(entity)
    suspend fun updateLocation(entity: LocationEntity) = dao.updateLocation(entity)
    fun getLocation(id: Int) = dao.getLocation(id)
    fun getLocationsList() = dao.getAllLocations()
}