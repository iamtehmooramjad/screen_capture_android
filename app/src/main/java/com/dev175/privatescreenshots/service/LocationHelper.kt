package com.dev175.privatescreenshots.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle


class LocationHelper {

    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false

    var LOCATION_REFRESH_TIME = 1000 // 3 seconds. The Minimum Time to get location update
    var LOCATION_REFRESH_DISTANCE = 0 // 0 meters. The Minimum Distance to be changed to get location update
    lateinit var mLocationManager: LocationManager
    lateinit var locationListener: LocationListener

    @SuppressLint("MissingPermission")
    fun startListeningUserLocation(context: Context, myListener: MyLocationListener) {
        mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGPSEnabled && !isNetworkEnabled) {
        } else {
            canGetLocation = true
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    myListener.onLocationChanged(location) // calling listener to inform that updated location is available
                }

                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            }
            if (isNetworkEnabled){
                mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_REFRESH_TIME.toLong(),
                    LOCATION_REFRESH_DISTANCE.toFloat(),
                    locationListener
                )
            }
            else if (isGPSEnabled){

                mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME.toLong(),
                    LOCATION_REFRESH_DISTANCE.toFloat(),
                    locationListener
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun stopListeningUpdates(){
        mLocationManager.removeUpdates(locationListener)
    }
}
interface MyLocationListener {
    fun onLocationChanged(location: Location?)
}