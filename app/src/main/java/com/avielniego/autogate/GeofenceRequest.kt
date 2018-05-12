package com.avielniego.autogate

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceRequest(private val context: Context) {

    companion object {
        private const val GEOFENCE_REQUEST_ID = "GEOFENCE_REQUEST_ID"
        private val LOG_TAG = GeofenceRequest::class.java.simpleName

        private val LOCATION = CircularRegion(32.0674372,34.7819375, 100f)
    }

    @SuppressLint("MissingPermission")
    fun request() {
        val mGeofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
        val intent = Intent(context, GeofenceListenerService::class.java)
        val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mGeofencingClient.addGeofences(buildGeofencingRequest(), pendingIntent)
                .addOnFailureListener { Log.d(LOG_TAG, "Failed adding geofence") }
                .addOnSuccessListener { Log.d(LOG_TAG, "Geofence added successfully") }
    }

    private fun buildGeofencingRequest(): GeofencingRequest? {
        return GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(buildGeofence())
                .build()
    }

    private fun buildGeofence(): Geofence? {
        return Geofence.Builder()
                .setRequestId(GEOFENCE_REQUEST_ID)
                .setCircularRegion(LOCATION.long, LOCATION.lat, LOCATION.radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()
    }

    data class CircularRegion(val long: Double, val lat: Double, val radius: Float)
}