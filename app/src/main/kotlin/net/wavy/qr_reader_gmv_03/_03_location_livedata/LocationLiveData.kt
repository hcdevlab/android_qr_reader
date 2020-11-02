
package net.wavy.qr_reader_gmv_03._03_location_livedata

import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*


class LocationLiveData(val context: Context) : MutableLiveData<Location>(),
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		LocationListener
{
	private val googleApiClient: GoogleApiClient
	private val fusedLocationClient: FusedLocationProviderClient

	init
	{
		googleApiClient = GoogleApiClient.Builder(context, this, this)
				.addApi(LocationServices.API)
				.addOnConnectionFailedListener(this)
				.addConnectionCallbacks(this)
				.build()

		fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
	}

	override fun onActive()
	{
		googleApiClient.connect()
	}

	override fun onInactive()
	{
		if (googleApiClient.isConnected)
		{
			LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallBack)
		}
		googleApiClient.disconnect()
	}

	private val locationCallBack = object : LocationCallback()
	{
		override fun onLocationResult(location: LocationResult)
		{
			super.onLocationResult(location)
			onLocationChanged(location.lastLocation)
		}
	}

	override fun onConnected(connectionHint: Bundle?)
	{
		if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
		{
			fusedLocationClient.lastLocation
					.addOnSuccessListener { location ->

						if (location != null)
						{
							setValue(location)
						}
					}

			val locationRequest: LocationRequest = LocationRequest.create()
			locationRequest.apply {
				priority = LocationRequest.PRIORITY_HIGH_ACCURACY
				interval = 10000
				fastestInterval = 2000
			}

			if (hasActiveObservers())
			{
				LocationServices
						.getFusedLocationProviderClient(context)
						.requestLocationUpdates(locationRequest, locationCallBack, null)
			}
		}
	}

	override fun onLocationChanged(location: Location)
	{
		value = location
	}

	override fun onConnectionSuspended(cause: Int)
	{
	}

	override fun onConnectionFailed(connectionResult: ConnectionResult)
	{
	}

	fun displayLog(message: String)
	{
		val maxLogSize = 1000
		val stringLength = message.length
		for (i in 0..stringLength / maxLogSize)
		{
			val start = i * maxLogSize
			var end = (i + 1) * maxLogSize
			end = if (end > message.length) message.length else end
			Log.v("TAG LocationLiveData", message.substring(start, end))
		}
	}
}