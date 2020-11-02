
package net.wavy.qr_reader_gmv_03._03_location_livedata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import android.location.Location

class LocationViewModel(application: Application) : AndroidViewModel(application)
{
	val mediatorLiveData = MediatorLiveData<Location>()
	var observableLiveData: LocationLiveData

	init
	{
		observableLiveData = LocationLiveData(application.applicationContext)
		mediatorLiveData.addSource(observableLiveData) { mediatorLiveData.postValue(it) }
	}

	fun refresh()
	{
		mediatorLiveData.removeSource(observableLiveData)
		observableLiveData = LocationLiveData(getApplication())
		mediatorLiveData.addSource(observableLiveData) { mediatorLiveData.postValue(it) }
	}
}
