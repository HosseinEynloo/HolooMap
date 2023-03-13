package com.example.hloomap.ui.modules

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import javax.inject.Inject

class CustomLocation @Inject constructor() :Location("")  {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomLocation> {
        override fun createFromParcel(parcel: Parcel): CustomLocation {
            return CustomLocation(parcel)
        }

        override fun newArray(size: Int): Array<CustomLocation?> {
            return arrayOfNulls(size)
        }
    }
}