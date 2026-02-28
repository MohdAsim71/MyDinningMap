package com.codinglance.mydinningmap

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import androidx.core.content.edit

class PreferenceManager(context: Context) {
    private val prefs = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)

    fun saveLocation(latLng: LatLng) {
        prefs.edit {
            putFloat("last_lat", latLng.latitude.toFloat())
                .putFloat("last_long", latLng.longitude.toFloat())
        }
    }

    fun getLastLocation(): LatLng? {
        val lat = prefs.getFloat("last_lat", -1f)
        val lng = prefs.getFloat("last_long", -1f)

        // Return null if no location was ever saved (using -1 as a flag)
        return if (lat == -1f || lng == -1f) null else LatLng(lat.toDouble(), lng.toDouble())
    }
}