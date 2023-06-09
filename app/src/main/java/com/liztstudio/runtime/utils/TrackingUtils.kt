package com.liztstudio.runtime.utils

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.liztstudio.runtime.services.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtils {

    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }

    fun getFormattedTime(ms: Long, includeMillis: Boolean = false): String {
        var millisecond = ms
        var hours = TimeUnit.MILLISECONDS.toHours(millisecond)
        millisecond -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond)
        millisecond -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecond)
        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds:"
        }
        millisecond -= TimeUnit.SECONDS.toMillis(seconds)
        millisecond /= 10
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                "${if (millisecond < 10) "0" else ""}$millisecond:"
    }

    fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0f
        for (i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }
}