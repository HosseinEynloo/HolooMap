package com.example.hloomap.data.local

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.hloomap.data.models.LocationEntity
import com.example.hloomap.utils.Constants
import com.example.hloomap.utils.Constants.LOCATION_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Update
    suspend fun updateLocation(location: LocationEntity)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)


    @Query("DELETE FROM $LOCATION_TABLE")
    suspend fun deleteAllLocations()

    @Query("SELECT * FROM $LOCATION_TABLE WHERE locationId LIKE :id")
    fun getLocation(id: Int): Flow<LocationEntity>


    @Query("SELECT * FROM $LOCATION_TABLE")
    fun getAllLocations(): Flow<MutableList<LocationEntity>>


    //    // ORDER BY LocationID DESC >>>> means: The last item at the top of the list
//    @Query("SELECT * FROM ${Constants.LOCATION_TABLE} ORDER BY locationId DESC")
//    fun getAllLocations(): MutableList<LocationEntity>



}