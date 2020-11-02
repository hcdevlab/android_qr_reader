
package net.wavy.qr_reader_gmv_03._02_postlogin

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import androidx.gridlayout.widget.GridLayout
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.wavy.qr_reader_gmv_03.R
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView


class RecyclerItemUI : AnkoComponent<ViewGroup>
{
	override fun createView(ui: AnkoContext<ViewGroup>): View
	{
		return with(ui) {
			getViewLayout(ui)
			// getCardViewGridLayout(ui)
		}
	}

	fun getViewLayout(ui: AnkoContext<ViewGroup>): View
	{
		val layoutView = ui.linearLayout {

			lparams {
				width = matchParent
				// width = RecyclerView.LayoutParams.MATCH_PARENT
				height = dip(70)
				topMargin = dip(5)
				weightSum = 5f
				background = borderRadius()
				// background = layoutBorderRadius()
			}

			//------------------------------------------------------------//
			// 'Layout' 1 de 2
			linearLayout {
				lparams {
					// orientation = LinearLayoutManager.HORIZONTAL
					// width = matchParent
					width = dip(0)
					// height = dip(70)
					// height = wrapContent

					weight = 1f
					gravity = Gravity.CENTER // Combinación 1
					// gravity = LinearLayoutManager.HORIZONTAL.plus(LinearLayoutManager.VERTICAL)
					// gravity = Gravity.CENTER_VERTICAL.plus(Gravity.CENTER_HORIZONTAL)
					// setGravity(Gravity.CENTER)
					// setGravity(Gravity.CENTER_HORIZONTAL.plus(Gravity.CENTER_VERTICAL))
				}/*.apply {
					this@apply.gravity = Gravity.CENTER_HORIZONTAL.plus(Gravity.CENTER_VERTICAL)
				}*/

				val textViewId = textView {
					id = R.id.recyclerItemNumber
					textSize = 16f
					textColor = Color.WHITE
				}.lparams {
					// padding = dip(5)
					// gravity = Gravity.CENTER
					// this.gravity = Gravity.CENTER
					// leftPadding = dip(5)
					// layout_centerInParent
					// textAlignment = View.TEXT_ALIGNMENT_CENTER
					setGravity(Gravity.CENTER) // Combinación 1
				}/*.apply {
					this@apply.gravity = Gravity.CENTER_HORIZONTAL.plus(Gravity.CENTER_VERTICAL)
				}*/
			}
			//------------------------------------------------------------//

			//------------------------------------------------------------//
			// 'Layout' 2 de 2
			linearLayout {

				lparams {
					// width = matchParent
					width = dip(0)
					// height = dip(70)
					// height = wrapContent

					gravity = Gravity.CENTER
					// gravity = LinearLayoutManager.HORIZONTAL
					weight = 4f
				}

				val textViewName = textView {
					id = R.id.recyclerItemName
					textSize = 16f
					textColor = Color.WHITE
				}.lparams {
					// padding = dip(5)
				}

			}
			//------------------------------------------------------------//
			// }
		}
		return layoutView
	}

	@SuppressLint("WrongConstant")
	fun getCardViewGridLayout(ui: AnkoContext<ViewGroup>): View
	{
		lateinit var textViewId: TextView
		lateinit var textViewName: TextView
		// lateinit var textViewState: TextView

		val cv = ui.cardView() {
			cardElevation = dip(3).toFloat()
			// Agrega un espacio entre las hileras.
			useCompatPadding = true
			// lparams(width = matchParent, height = wrapContent)

			lparams {
				width = matchParent
				// height = dip(70)
				height = wrapContent
				// backgroundColor = Color.WHITE
				// background = R.drawable.cardview_border
				// backgroundResource = R.drawable.cardview_border
				// Modifica el color del fondo, el que se ve entre los 'cardView'.
				backgroundColor = Color.parseColor("#f3f3f3")
				radius = dip(8).toFloat()
				gravity = Gravity.CENTER
				// gravity = LinearLayoutManager.HORIZONTAL
			}

			// https://stackoverflow.com/questions/10347846/how-to-make-a-gridlayout-fit-screen-size
			val size: Point = Point()
			ui.ctx.windowManager.defaultDisplay.getSize(size)

			/*val display = measuredWidth
			val k = measuredHeight*/

			// Obtenemos el 'width' del dispositivo:
			val screenWidth: Int = size.x
			// Obtenemos el 30% del 'width'.
			val screenWidthThird: Int = (screenWidth * 0.33).toInt()
			// Obtenemos el 'height' del dispositivo:
			// val screenHeight: Int = size.y

			// Lo de abajo es muy relativo, la forma más clara de exponerlo (hasta ahora) es la siguiente.
			/*
				Como hay una sola fila (asignada en el 'GridLayout', más abajo) determinada mediante 'rowCount = 1',
				el 'GridLayout.Spec' es igual a '0' (desde donde empieza a contar).
			*/
			val firstLayoutRow: GridLayout.Spec = GridLayout.spec(0)
			/*
				Como hay tres columnas (asignadas en el 'GridLayout', más abajo) determinadas mediante 'columnCount = 3',
				el 'GridLayout.Spec' es igual a '0' (desde donde empieza a contar), y se extiende por '2' (el 'size').
				Igualmente hay que asignar un 'width' al 'RelativeLayout' además de estas especificaciones.
			*/
			val firstLayoutColumn: GridLayout.Spec = GridLayout.spec(0, 2)
			val secondLayoutRow: GridLayout.Spec = GridLayout.spec(0)
			val secondLayoutColumn: GridLayout.Spec = GridLayout.spec(2, 1)

			// val firstLayout: GridLayout.LayoutParams = GridLayout.LayoutParams(firstLayoutRow, firstLayoutColumn)
			val firstLayout: GridLayout.LayoutParams = GridLayout.LayoutParams(firstLayoutRow, secondLayoutColumn)
			// val secondLayout: GridLayout.LayoutParams = GridLayout.LayoutParams(secondLayoutRow, secondLayoutColumn)

			gridLayout {
				lparams {
					// Ver bien con qué 'width' funciona, por ahora lo hace con 'matchParent'.
					width = matchParent
					// Puede necesitar especificar el 'width' calculando el tamaño del dispositivo 'programáticamente'.
					// width = screenWidth
					// height = dip(150)
					height = wrapContent
					orientation = GridLayout.HORIZONTAL
					rowCount = 1
					columnCount = 3
					setGravity(Gravity.CENTER)
				}

				// El primer 'layout', contiene los dos 'TextView'.
				relativeLayout {

					textViewId = textView {
						id = R.id.recyclerItemNumber
						textSize = 16f
						textColor = Color.RED
					}.lparams {
						padding = dip(10)
						width = screenWidthThird
					}

					textViewName = textView {
						id = R.id.recyclerItemName
						textSize = 12f
						textColor = Color.BLACK
					}.lparams {
						padding = dip(10)
						rightOf(textViewId)
						width = screenWidthThird
					}

				}.lparams {
					// orientation = LinearLayout.VERTICAL
					// width = screenWidthThird * 3
					width = matchParent
					height = wrapContent
					layoutParams = firstLayout
					// Modifica el color del contenido del 'cardView'.
					// backgroundColor = Color.parseColor("#ffffff")
				} // Fin del 'layout' para los 'TextView'.
			}
		}
		return cv
	}

	fun borderRadius(): GradientDrawable
	{
		val shape = GradientDrawable()
		shape.shape = GradientDrawable.RECTANGLE
		shape.cornerRadii = floatArrayOf(15f, 15f, 15f, 15f, 0f, 0f, 0f, 0f)
		// shape.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 8f, 8f, 8f, 8f)
		return shape
	}

	fun layoutBorderRadius() = GradientDrawable().apply {
		shape = GradientDrawable.RECTANGLE
		cornerRadius = 8f
	}
}