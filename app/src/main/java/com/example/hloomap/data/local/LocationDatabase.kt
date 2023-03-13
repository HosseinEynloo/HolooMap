package com.example.hloomap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hloomap.data.models.LocationEntity

@Database(entities = [LocationEntity::class], version = 1)
abstract class LocationDatabase : RoomDatabase(){
    abstract fun dao(): LocationDao
}