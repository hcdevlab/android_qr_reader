package net.wavy.qr_reader_gmv_03._02_postlogin

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.wavy.qr_reader_gmv_03.R
import net.wavy.qr_reader_gmv_03._04_room_db.recycler.DBRecyclerViewModel
import net.wavy.qr_reader_gmv_03._04_room_db.recycler.RowEntityRecycler
import net.wavy.qr_reader_gmv_03._04_room_db.recycler.TableQueriesRecycler
import org.jetbrains.anko.*
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.selector

class FragmentHaul : Fragment()
{
	val recyclerViewAdapter: RecyclerViewAdapter = RecyclerViewAdapter()
	private val dbRecyclerViewModel: DBRecyclerViewModel by lazy { ViewModelProviders.of(this@FragmentHaul).get(DBRecyclerViewModel::class.java) }

	/*----------------------------------------------------------------------------------------------------*/
	companion object
	{
		fun newInstance(): FragmentHaul
		{
			return FragmentHaul()
		}
	}
	/*----------------------------------------------------------------------------------------------------*/

	/*----------------------------------------------------------------------------------------------------*/
	fun initViewModel()
	{
		dbRecyclerViewModel.observableLiveData.observe(this@FragmentHaul, object : Observer<List<RowEntityRecycler>>
		{
			override fun onChanged(lro: List<RowEntityRecycler>?)
			{
				/*
					En cuanto esté asignado 'observableLiveData' (después de sincronizar la base de datos)
					se hará visible la lista, ya que después de recibir y transformar los datos ejecutamos
					'setObservableLiveData' (dentro de 'DBOperations' - 'insertAllDataInDB').
				*/
				recyclerViewAdapter.liveDataList = lro!!
				recyclerViewAdapter.notifyDataSetChanged()
			}
		})
	}
	/*----------------------------------------------------------------------------------------------------*/

	/*----------------------------------------------------------------------------------------------------*/
	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)
	}
	/*----------------------------------------------------------------------------------------------------*/

	/*----------------------------------------------------------------------------------------------------*/
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		// Creamos la tabla.
		// TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel)
		initViewModel()
	}
	/*----------------------------------------------------------------------------------------------------*/

	/*----------------------------------------------------------------------------------------------------*/
	val dataListMorning: List<String> = arrayListOf(
			"7° piso - Sala de estar",
			"7° piso - Cocina",
			"6° piso",
			"5° piso",
			"5° piso",
			"5° piso",
			"4° piso",
			"3° piso",
			"3° piso")

	val dataListAfternoom: List<String> = arrayListOf(
			"5° piso - Terraza",
			"5° piso - Solarium",
			"4° piso",
			"3° piso - Recepción",
			"3° piso - Secretaría",
			"2° piso",
			"1° piso",
			"1° subsuelo",
			"2° subsuelo",
			"3° subsuelo",
			"Centro de computos")

	val dataListNight: List<String> = arrayListOf(
			"Anexo",
			"Farmacia",
			"4° piso",
			"1° piso",
			"1° subsuelo",
			"Terraza",
			"10° piso",
			"9° piso",
			"9° piso - Equipos",
			"8° piso",
			"7° piso",
			"7° piso",
			"6° piso",
			"5° piso",
			"4° piso",
			"3° piso",
			"3° piso",
			"2° piso",
			"1° piso",
			"1° subsuelo",
			"2° subsuelo",
			"3° subsuelo")
	/*----------------------------------------------------------------------------------------------------*/

	/*----------------------------------------------------------------------------------------------------*/
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		return UI {
			verticalLayout {
				// relativeLayout {
				lparams {
					width = matchParent
					// width = RecyclerView.LayoutParams.MATCH_PARENT
					height = matchParent
					padding = dip(10)
					/*leftPadding = dip(20)
					rightPadding = dip(20)*/
				}

				/*layoutParams.width = matchParent
				layoutParams.height = matchParent
				padding = dip(10)*/

				// backgroundResource = R.drawable.border
				// backgroundResource = R.drawable.fragment_inner_first_background
				// background = getFragmentBackground()

				backgroundColor = Color.parseColor("#333333")

				// gravity = Gravity.CENTER_VERTICAL.plus(Gravity.CENTER_HORIZONTAL)
				gravity = Gravity.CENTER_HORIZONTAL
				// padding = dip(15)

				//--------------------------------------------------------------------------------//
				// Componentes del 'Fragment'.
				/*val title = textView()
				{
					id = R.id.fragmentShiftNightTextView
					textSize = 16F
					// setTypeface(null, Typeface.BOLD_ITALIC)
					textColor = Color.parseColor("#ffffff")
					gravity = Gravity.CENTER_HORIZONTAL
				}*/

				// padding = dip(10)
				//--------------------------------------------------------------------------------//

				//--------------------------------------------------------------------------------//
				button("Generar lista") {
					id = R.id.buttonCreateTable
					// background = ContextCompat.getDrawable(context, R.drawable.button_style)

					/*onClick() {

						alert(message = "Nuevo recorrido", title = "Confirmar") {
							noButton {
							}
							yesButton()
							{
								TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel)
							}
						}.show()*/

					onClick {
						/*val options: List<String> = listOf("Turno Mañana", "Turno Tarde", "Turno Noche", "Cancelar")
						selector("Seleccione una opción", options) { dialogInterface: DialogInterface, i: Int ->
							when (i)
							{
								0 ->
								{
									toast("Presionamos 0")
									TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel, dataListMorning)
									dialogInterface.dismiss()
									// alertDialogBuilder.setCancelable(true)
								}
								1 ->
								{
									toast("Presionamos 1")
									TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel, dataListAfternoom)
									dialogInterface.dismiss()
								}
								2 ->
								{
									toast("Presionamos 2")
									TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel, dataListNight)
									dialogInterface.dismiss()
								}
								3 ->
								{
									// ui.toast("Presionamos 3")
									isSelected = false
									dialogInterface.dismiss()
								}
							}
						}*/
						showAlert()
						// showAlert(title)
						// showAlert2()
					}
					// }.lparams(dip(250), sp(60))
				}.lparams {
					width = dip(210)
					height = dip(60)
					topMargin = dip(5)
				}
				//--------------------------------------------------------------------------------//

				//--------------------------------------------------------------------------------//
				// recyclerView = recyclerView {
				recyclerView {
					lparams {
						// El único 'width' que permite ocupar el espacio disponible total.
						width = matchParent
						// backgroundColor = Color.WHITE
						topMargin = dip(10)
					}

					val lm = LinearLayoutManager(context)
					layoutManager = lm

					/*val glm = GridLayoutManager(context, 1)
					// glm.isAutoMeasureEnabled = true
					layoutManager = glm*/

					// 2 de 2: hay que pasarle el 'RecyclerViewAdapter' al 'recyclerView'
					adapter = recyclerViewAdapter
				} // Fin del 'recyclerView'.
			}
		}.view
	}
	/*----------------------------------------------------------------------------------------------------*/

	/*----------------------------------------------------------------------------------------------------*/
	override fun onDestroy()
	{
		super.onDestroy()
	}
	/*----------------------------------------------------------------------------------------------------*/

	/*----------------------------------------------------------------------------------------------------*/
	fun getFragmentBackground(): Drawable
	{
		// Asignamos los colores:
		val startColor: Int = Color.parseColor("#dcdcf2")
		val centerColor: Int = Color.parseColor("#8787fc")
		val endColor: Int = Color.parseColor("#5757c1")

		// Colocamos los colores en un 'IntArray':
		val colors: IntArray = intArrayOf(startColor, centerColor, endColor)

		val layer1: GradientDrawable = GradientDrawable()
		// layer1.shape = GradientDrawable.OVAL
		layer1.shape = GradientDrawable.RECTANGLE
		layer1.setSize(33, 33)
		// layer1.setColor(Color.GRAY)
		layer1.colors = colors
		layer1.orientation = GradientDrawable.Orientation.RIGHT_LEFT
		// Un borde en el layout:
		// layer1.setStroke(8, Color.RED)

		/*val layer2 = GradientDrawable()
		layer2.shape = GradientDrawable.OVAL
		layer2.setColor(Color.BLUE)

		val insetLayer = InsetDrawable(layer1, 8, 8, 8, 8)

		val layerDrawable = LayerDrawable(arrayOf(insetLayer, layer2))*/

		// return layerDrawable
		return layer1
	}
	/*----------------------------------------------------------------------------------------------------*/

	// private fun showAlert(messageResource: Int, onYesTapped: () -> Unit, onNoTapped: () -> Unit) {
	private fun showAlert()
	// private fun showAlert(title: TextView)
	{
		val options: List<String> = listOf("Turno Mañana", "Turno Tarde", "Turno Noche", "Cancelar")
		selector("Seleccione una opción", options) { dialogInterface: DialogInterface, i: Int ->
			when (i)
			{
				0 ->
				{
					// toast("Presionamos 0")
					TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel, dataListMorning)
					// title.text = "Turno Mañana"
					dialogInterface.dismiss()
					// alertDialogBuilder.setCancelable(true)
				}
				1 ->
				{
					// toast("Presionamos 1")
					TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel, dataListAfternoom)
					// title.text = "Turno Tarde"
					dialogInterface.dismiss()
				}
				2 ->
				{
					// toast("Presionamos 2")
					TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel, dataListNight)
					// title.text = "Turno Noche"
					dialogInterface.dismiss()
				}
				3 ->
				{
					// ui.toast("Presionamos 3")
					// isSelected = false
					dialogInterface.dismiss()
				}
			}
		}
	}

	private fun showAlert2()
	{
		val options: List<String> = listOf("Turno Mañana", "Turno Tarde", "Turno Noche", "Cancelar")
		alert {
			customView {
				verticalLayout {
					lparams {
						gravity = Gravity.CENTER
					}
					selector("Seleccione una opción", options) { dialogInterface: DialogInterface, i: Int ->
						when (i)
						{
							0 ->
							{
								// toast("Presionamos 0")
								TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel, dataListMorning)
								dialogInterface.dismiss()
								// alertDialogBuilder.setCancelable(true)
							}
							1 ->
							{
								// toast("Presionamos 1")
								TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel, dataListAfternoom)
								dialogInterface.dismiss()
							}
							2 ->
							{
								// toast("Presionamos 2")
								TableQueriesRecycler(this@FragmentHaul.context!!).createRecyclerTable(dbRecyclerViewModel, dataListNight)
								dialogInterface.dismiss()
							}
							3 ->
							{
								// ui.toast("Presionamos 3")
								// isSelected = false
								dialogInterface.dismiss()
							}
						}
					}
				}
			}
		}.show() //4. Showing the alert
	}
}
