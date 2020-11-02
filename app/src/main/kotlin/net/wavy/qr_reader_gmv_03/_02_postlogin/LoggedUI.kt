package net.wavy.qr_reader_gmv_03._02_postlogin

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import net.wavy.qr_reader_gmv_03.R
import net.wavy.qr_reader_gmv_03._04_room_db.user.TableQueriesUser

class LoggedUI : AnkoComponent<LoggedActivity>
{
	override fun createView(ui: AnkoContext<LoggedActivity>): View = with(ui)
	{
		constraintLayout {
			//--------------------------------------------------------------------------------//
			// Experimentando con 'ConstraintLayout'.
			id = R.id.constraintLayoutLoggedUI
			fitsSystemWindows = true

			lparams(width = matchParent, height = matchParent)
			//--------------------------------------------------------------------------------//

			val toolbarContainer = frameLayout {
				id = R.id.toolbarLayoutLoggedUI

				toolbar()
				{
					id = R.id.toolbarLoggedUI

					lparams(width = matchParent, height = wrapContent)
					// val menuBarColor = ContextCompat.getColor(context, R.color.menuBar)
					backgroundColor = Color.DKGRAY
					// backgroundColor = ContextCompat.getColor(context, R.color.menuBar)

					title = "QR Scanner App"
					setTitleTextColor(Color.LTGRAY)

					val iconMenu: Drawable = ContextCompat.getDrawable(ctx, R.drawable.menu_icon)!!
					overflowIcon = iconMenu
					overflowIcon?.setColorFilter(ContextCompat.getColor(context, R.color.menuIcon), PorterDuff.Mode.SRC_ATOP)

					menu.add(1, R.id.itemFirstLoggedUI, 1, "Scanner")
					menu.add(2, R.id.itemSecondLoggedUI, 2, "Ver recorridos")
					menu.add(3, R.id.itemThirdLoggedUI, 3, "Salir")
					onMenuItemClick {
						when (it!!.itemId)
						{
							R.id.itemFirstLoggedUI  ->
							{
								val fragmentScanner: FragmentScanner = FragmentScanner.newInstance()
								ui.owner.supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, fragmentScanner, "fragmentScanner").addToBackStack(null).commit()
							}
							R.id.itemSecondLoggedUI ->
							{
								val fragmentHaul: FragmentHaul = FragmentHaul.newInstance()
								ui.owner.supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, fragmentHaul).addToBackStack(null).commit()
							}
							R.id.itemThirdLoggedUI  ->
							{
								val dialog: ProgressDialog = context.indeterminateProgressDialog(message = "Aguarde...", title = "Desconectando")
								// dialog.show()
								// Hay que eliminar el usuario que ingresó al salir, para borrar los datos, en particular la cookie JWT.
								// TableQueriesUser(ui.ctx).deleteUser(dialog, "userData") // No llega a borrarlo porque se ejecuta con 'coroutine'.
								// dialog.dismiss()
								// TableQueriesUser(ui.ctx).deleteUserAndGetOut(dialog, "userData")
								TableQueriesUser(ui.ctx).deleteUserAndGetOut(dialog)
								// finishApp()
							}
						}
					}
				}
			}.lparams {
				width = matchParent
				// height = MATCH_CONSTRAINT
			} // Fin del 'FrameLayout' para la 'Toolbar'.

			val fragmentContainer = frameLayout()
			{
				id = R.id.frameLayoutContainer
				// lparams(width = matchParent, height = matchParent)
			}.lparams {
				width = MATCH_CONSTRAINT
				height = MATCH_CONSTRAINT
			} // Fin del 'FrameLayout' para los 'Fragments'.

			// https://github.com/Kotlin/anko/wiki/ConstraintLayout
			applyConstraintSet {

				toolbarContainer {
					connect(
							TOP to TOP of this@constraintLayout,
							RIGHT to RIGHT of this@constraintLayout,
							LEFT to LEFT of this@constraintLayout
					)
				}

				fragmentContainer {
					connect(
							TOP to BOTTOM of toolbarContainer,
							RIGHT to RIGHT of this@constraintLayout,
							LEFT to LEFT of this@constraintLayout,
							BOTTOM to BOTTOM of this@constraintLayout
					)
				}
			}
		}
	}
	//----------------------------------------------------------------------------------------------------//

	//----------------------------------------------------------------------------------------------------//
	fun displayLog(message: String)
	{
		val maxLogSize = 1000
		val stringLength = message.length
		for (i in 0..stringLength / maxLogSize)
		{
			val start = i * maxLogSize
			var end = (i + 1) * maxLogSize
			end = if (end > message.length) message.length else end
			Log.v("TAG", message.substring(start, end))
		}
	}
	//----------------------------------------------------------------------------------------------------//

	//----------------------------------------------------------------------------------------------------//
	fun finishApp()
	{
		android.os.Process.killProcess(android.os.Process.myPid())
	}
	//----------------------------------------------------------------------------------------------------//
}