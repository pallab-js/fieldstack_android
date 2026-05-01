package com.fieldstack.android.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun getLastLocation(
        context: Context,
        onResult: (lat: Double, lng: Double) -> Unit,
        onError: (String) -> Unit,
    ) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) onResult(loc.latitude, loc.longitude)
                else {
                    // lastLocation null — request a fresh one
                    client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                        .addOnSuccessListener { fresh ->
                            if (fresh != null) onResult(fresh.latitude, fresh.longitude)
                            else onError("Location unavailable")
                        }
                        .addOnFailureListener { onError(it.message ?: "Location error") }
                }
            }
            .addOnFailureListener { onError(it.message ?: "Location error") }
    }
}
