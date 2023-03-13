package com.example.hloomap.utils

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat
import com.example.hloomap.utils.Constants.PERMISSION_CODE
import javax.inject.Inject

class PermissionManager @Inject constructor() {

     fun requestPermissions(context: Activity) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_CODE
        )
    }
}