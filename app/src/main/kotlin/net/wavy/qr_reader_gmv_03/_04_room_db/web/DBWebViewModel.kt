
package net.wavy.qr_reader_gmv_03._04_room_db.web

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class DBWebViewModel(application: Application): AndroidViewModel(application)
{
	val mediatorLiveData = MediatorLiveData<List<RowEntityWeb>>()
	var observableLiveData: LiveData<List<RowEntityWeb>> = MutableLiveData()
	val tableQueriesWeb: TableQueriesWeb = TableQueriesWeb(application)

	init
	{
		observableLiveData = tableQueriesWeb.provideAppDataBase().getDaoWeb().fetchAllRowsLiveData()

		mediatorLiveData.addSource(observableLiveData) { mediatorLiveData.postValue(it) }
	}

	fun refresh()
	{
		mediatorLiveData.removeSource(observableLiveData)
		observableLiveData = tableQueriesWeb.provideAppDataBase().getDaoWeb().fetchAllRowsLiveData()
		mediatorLiveData.addSource(observableLiveData) { mediatorLiveData.postValue(it) }
	}
}