
package net.wavy.qr_reader_gmv_03._03_location_livedata

import android.Manifest
import androidx.core.app.ActivityCompat
import android.app.Activity
import android.content.pm.PackageManager
import java.lang.ref.WeakReference


internal class PermissionsRequester private constructor(private val activityWeakReference: WeakReference<Activity>)
{

	fun hasPermissions(): Boolean
	{
		val activity = activityWeakReference.get()
		if (activity != null)
		{
			for (permission in PERMISSIONS)
			{
				if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
				{
					return false
				}
			}
			return true
		}
		return false
	}

	fun requestPermissions()
	{
		val activity = activityWeakReference.get()
		if (activity != null)
		{
			ActivityCompat.requestPermissions(activity, PERMISSIONS, 0)
		}
	}

	companion object
	{
		private val PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

		fun newInstance(activity: Activity): PermissionsRequester
		{
			val activityWeakReference = WeakReference(activity)
			return PermissionsRequester(activityWeakReference)
		}
	}
}

