package com.example.hloomap.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hloomap.utils.Constants

@Entity(tableName = Constants.LOCATION_TABLE)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    var locationId:Int=0,
//    @ColumnInfo(name = "latitude")
    var latitude:Double=0.0,
//    @ColumnInfo(name = "longitude")
    var longitude:Double=0.0,
    var description:String="",
    var title:String="",
    )