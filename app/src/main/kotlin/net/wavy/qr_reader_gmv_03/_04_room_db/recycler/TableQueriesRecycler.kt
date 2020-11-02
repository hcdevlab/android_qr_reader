
package net.wavy.qr_reader_gmv_03._04_room_db.recycler

import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.wavy.qr_reader_gmv_03._04_room_db.RoomDataBase
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*

class TableQueriesRecycler(val externalContext: Context)
{
	fun provideAppDataBase(): RoomDataBase
	{
		val db: RoomDataBase = Room.databaseBuilder(externalContext, RoomDataBase::class.java, "roomDataBase").build()
		return db
	}

	/*val dataList: List<String> = arrayListOf(
			"Anexo 9°Piso",
			"Farmacia 2°subsuelo equipos",
			"Venezuela 4°piso",
			"Venezuela 1°piso",
			"Venezuela Bombas 1°subsuelo",
			"Terraza 10°piso Bombas",
			"10°Piso Tablero terraza",
			"9°piso tablero habitaciones 911 y 913",
			"9°piso Equipos",
			"8°piso cuarto tecnico",
			"7°piso Tablero habitaciones 711 y 713",
			"7°piso Equipos",
			"6°piso cuarto tecnico",
			"5°piso trasplante de médula pediatrica",
			"5°piso terraza quirofano N°6 y N°7",
			"5°piso cuarto técnico",
			"4°piso Cuarto tecnico",
			"3°piso Alarma",
			"3°piso Cuarto tecnico",
			"2°piso Cuarto tecnico",
			"1°piso tablero nuevo",
			"1°subsuelo Tablero nuevo",
			"2°subsuelo cuarto técnico",
			"3°subsuelo sala de maquinas Maquinas de frio",
			"Universidad Centro de computos",
			"Universidad 4°piso")*/

	val range: IntRange = 1..20
	val r = range.asSequence()

	// https://code.luasoftware.com/tutorials/android/android-room-batch-insert-or-update/
	// Se invoca desde 'FragmentHaul'.
	fun createRecyclerTable(dbRecyclerViewModel: DBRecyclerViewModel, dataList: List<String>)
	{
		// async(UI)
		GlobalScope.launch(Dispatchers.Default)
		{
			// val query = async(CommonPool)
			val query = GlobalScope.launch(Dispatchers.Default)
			{
				// Antes de crear una nueva tabla eliminamos el contenido (si existe).
				// provideAppDataBase().clearAllTables()
				provideAppDataBase().getDaoRecycler().deleteAll(dbRecyclerViewModel.observableLiveData.value)
				val today = Date()
				val items = arrayListOf<RowEntityRecycler>()

				/*for (name in dataList)
				{
					val rowEntityRecycler = RowEntityRecycler(null, , name, today, null, false)
					items.add(rowEntityRecycler)
				}*/

				/*for (i in 1..dataList.size)
				{
					val rowEntityRecycler = RowEntityRecycler(null, i, dataList.get(i), today, null, false)
					items.add(rowEntityRecycler)
				}*/

				dataList.forEachIndexed { index, element ->
					val rowEntityRecycler = RowEntityRecycler(null, (index + 1), element, today, null, false)
					items.add(rowEntityRecycler)
				}

				// displayLog("Lista: " + items.toArray().toString())
				// Ejecutamos una inserción múltiple, ya que es un 'ArrayList'.
				provideAppDataBase().getDaoRecycler().insertAll(*items.toTypedArray())
				// Activamos el 'observer'.
				setObservableLiveData(dbRecyclerViewModel)
			}
			query.join()
		}
	}

	fun setObservableLiveData(dbRecyclerViewModel: DBRecyclerViewModel)
	{
		// launch(UI)
		// async(UI)
		GlobalScope.launch(Dispatchers.Main)
		{
			// 'Deferred' significa 'diferido', es similar a un 'Future' o una 'Promise'.
			// 01 - BACKGROUND THREAD: hacemos la 'query' a la base de datos:
			val dataDeferred: Deferred<List<RowEntityRecycler>> = bg()
			{
				provideAppDataBase().getDaoRecycler().fetchAllData()
			}

			// 02 - UI THREAD: pasamos la información al 'UI thread':
			// Asignamos el contenido de la base de datos a un 'MutableLiveData<List<RowEntity>>'.
			dbRecyclerViewModel.observableLiveData.postValue(dataDeferred.await())
			displayLog("DB setLiveDataList: " + dbRecyclerViewModel.observableLiveData.value.toString())
		}
	}

	/*
		Con esta 'function' cambiamos el 'boolean' que sirve para modificar el color de fondo
		que indica si un punto del recorrido fue almacenado o no.
	*/
	fun changeState(name: String, state: Boolean)
	{
		// async(UI)
		GlobalScope.launch(Dispatchers.Main)
		{
			// val query = async(CommonPool)
			val query = GlobalScope.launch(Dispatchers.Default)
			{
				provideAppDataBase().getDaoRecycler().updateState(name, state)
			}.join()
		}
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
			Log.v("TAG Table Recycler", message.substring(start, end))
		}
	}
}