package com.codinglance.mydinningmap


import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

object DateUtils {

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("EEE, MMM d • hh:mm a", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    fun formatTime(timestamp: Long): String =
        timeFormat.format(Date(timestamp))

    fun formatDateTime(timestamp: Long): String =
        dateTimeFormat.format(Date(timestamp))

    fun formatShortDate(timestamp: Long): String =
        shortDateFormat.format(Date(timestamp))

    /** Format duration in minutes to human-readable string */
    fun formatDuration(minutes: Int): String = when {
        minutes == 0    -> "Passing through"
        minutes < 60    -> "$minutes min"
        minutes == 60   -> "1 hr"
        minutes % 60 == 0 -> "${minutes / 60} hrs"
        else -> "${minutes / 60}h ${minutes % 60}m"
    }
}

object DistanceUtils {

    /** Format km distance to readable string */
    fun formatDistance(km: Float): String = when {
        km == 0f      -> "Starting point"
        km < 1f       -> "${(km * 1000).toInt()} m"
        km < 10f      -> "${"%.1f".format(km)} km"
        else          -> "${km.toInt()} km"
    }

    /**
     * Haversine formula to compute distance between two lat/lon points in km
     */
    fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (R * c).toFloat()
    }
}