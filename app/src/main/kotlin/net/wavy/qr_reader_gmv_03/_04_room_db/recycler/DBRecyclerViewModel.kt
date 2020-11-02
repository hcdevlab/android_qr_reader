
package net.wavy.qr_reader_gmv_03._04_room_db.recycler

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/*
	Created by luan on 26/09/18.
*/

// class DataViewModel(): ViewModel()
class DBRecyclerViewModel(application: Application): AndroidViewModel(application)
// class DataViewModel: AndroidViewModel
{
	// constructor(application: Application) : super(application)

	// El valor de esta 'property' es asignado en 'TableQueriesRecycler' - 'createRecyclerTable'.
	var observableLiveData: MutableLiveData<List<RowEntityRecycler>> = MutableLiveData()

	// Si declaramos el 'constructor' en la primera línea:
	// val db: AppDataBase = AppDataBase.getInstance(application)!!
	// Si declaramos el 'constructor' más abajo:
	// val db: AppDataBase = AppDataBase.getInstance(this.getApplication())!!
	val tableQueries: TableQueriesRecycler = TableQueriesRecycler(application)

	init
	{
		// DBOperations(getApplication()).setObservableLiveData(this)
		/*
			Si la lista existe en la base de datos, la mostrará luego de cerrar
			(y volver a abrir...) la aplicación.
			Si no existe no genera problemas.
		*/
		tableQueries.setObservableLiveData(this)
	}
}