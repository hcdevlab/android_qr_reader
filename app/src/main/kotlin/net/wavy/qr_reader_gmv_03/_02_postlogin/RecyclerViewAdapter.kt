
package net.wavy.qr_reader_gmv_03._02_postlogin

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.wavy.qr_reader_gmv_03.R
import net.wavy.qr_reader_gmv_03._04_room_db.recycler.RowEntityRecycler
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find

/*
	Created by wang on 23/09/18.
*/
class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.DataViewHolder>()
{
	// Para agregar vía 'setter' en vez de usar el 'constructor'.
	var liveDataList: List<RowEntityRecycler> = emptyList<RowEntityRecycler>()
		set(value)
		{
			field = value
			notifyDataSetChanged()
		};

	/*------------------------------------------------------------------------------------------*/
	// Los métodos implementados.
	// 1 de 3
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder
	{
		val rootView: DataViewHolder = DataViewHolder(RecyclerItemUI().createView(AnkoContext.create(parent.context, parent)))
		// rootView.view.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
		// rootView.view.width = RecyclerView.LayoutParams.MATCH_PARENT
		rootView.view.layoutParams.width = RecyclerView.LayoutParams.MATCH_PARENT
		return rootView
	}

	// 2 de 3
	// En este método se unen los valores con lo obtenido de la consulta.
	override fun onBindViewHolder(holder: DataViewHolder, position: Int)
	{
		// Obtenemos cada uno de los 'JSONObject' dentro del 'JSONArray' recibido en el 'constructor'.
		// val rowObject: RowObject = liveDataList.value!![position]
		// val rowObject: RowObject = liveDataList.get(position)
		val rowEntityRecycler: RowEntityRecycler = liveDataList.get(position)

		// Cambiamos el color
		if(rowEntityRecycler.state == true)
		{
			// holder.view.backgroundColor = Color.GREEN
			holder.itemView.backgroundColor = Color.parseColor("#f55555") // Funciona.
			// holder.textViewNumber.textColor = Color.parseColor("#ffffff")
			// holder.textViewName.textColor = Color.parseColor("#ffffff")
			// holder.view.background.setColorFilter(Color.GREEN, PorterDuff.Mode.DARKENSRC_ATOP)
			// holder.itemView.background = layoutBorderRadiusTrue()
		}
		else
		{
			// holder.view.backgroundColor = Color.LTGRAY
			// holder.itemView.backgroundColor = Color.LTGRAY
			holder.itemView.backgroundColor = Color.parseColor("#2c5773") // Funciona.
			// holder.view.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
			// holder.itemView.background = layoutBorderRadiusFalse()
		}

		/*----------------------------------------------------------------------------------------------------*/
		/*
			Este ejercicio trabaja con 'Dietetic_Shop_Kotlin_04_MySQL'.
			Para obtener las filas de la base de datos se usan los nombres ('keys') asignados en el 'JsonArray'
			devuelto por la 'fun':
			'fun getImageList(connection: SQLConnection, routingContext: RoutingContext)'
			Es decir, los 'values' ('Strings') que se capturan abajo utilizan esos 'keys'.
		*/
		/*----------------------------------------------------------------------------------------------------*/

		/*----------------------------------------------------------------------------------------------------*/
		// Obtenemos los primeros dos campos necesarios para el método 'bind'.
		// val stringId: String = rowEntityRecycler.id!!.toString()
		val stringId: String = rowEntityRecycler.serial.toString()
		// val stringDescription: String = rowObject.description
		val stringName: String = rowEntityRecycler.name
		// val stringState: String = rowEntityRecycler.state.toString()
		/*----------------------------------------------------------------------------------------------------*/

		// holder.bind(stringId, stringName, stringState)
		holder.bind(stringId, stringName)
		// notifyItemChanged(position)
	}

	// 3 de 3
	override fun getItemCount(): Int
	{
		// return liveDataList.value!!.size
		return liveDataList.size
	}
	/*------------------------------------------------------------------------------------------*/

	/*------------------------------------------------------------------------------------------*/
	inner class DataViewHolder(var view: View) : RecyclerView.ViewHolder(view)
	{
		/*
			01) dentro la clase que hereda de 'AnkoComponent<ViewGroup>':
			'RecyclerItemUI'
			... definimos componentes con 'ids' (que se declaran en 'ids.xml')

			02) en esta clase capturamos esos componentes a través del parámetro
			'var view: View' que recibe en el 'constructor' (esta misma clase interna),
			usando:
			'view.find<T>(R.id.resource)'

			03) finalmente creamos el método que colocará dentro del componente al
			correspondiente valor, y que será usado en:
			'onBindViewHolder(holder: DataViewHolder, position: Int)'
		*/

		// Creamos los componentes.
		val textViewNumber: TextView = view.find<TextView>(R.id.recyclerItemNumber)
		val textViewName: TextView = view.find<TextView>(R.id.recyclerItemName)
		// val textViewState: TextView = view.find<TextView>(R.id.recyclerItemState)

		// Asignamos los componentes creados en el paso anterior.
		// fun bind(number: String, name: String, state: String)
		fun bind(number: String, name: String)
		{
			textViewNumber.text = number
			textViewName.text = name
			// textViewState.text = state
		}
	} // Fin de la 'inner class' 'DataViewHolder'.
	/*------------------------------------------------------------------------------------------*/

	fun layoutBorderRadiusTrue() = GradientDrawable().apply {
		shape = GradientDrawable.RECTANGLE
		cornerRadius = 15f
		setColorFilter(Color.parseColor("#f55555"), PorterDuff.Mode.SRC_ATOP)
	}

	fun layoutBorderRadiusFalse() = GradientDrawable().apply {
		shape = GradientDrawable.RECTANGLE
		cornerRadius = 15f
		setColorFilter(Color.parseColor("#2c5773"), PorterDuff.Mode.SRC_ATOP)
	}
}