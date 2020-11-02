
package net.wavy.qr_reader_gmv_03._02_postlogin

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.wavy.qr_reader_gmv_03.R
import net.wavy.qr_reader_gmv_03._04_room_db.user.TableQueriesUser
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.setContentView

class LoggedActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		LoggedUI().setContentView(this)

		if (savedInstanceState == null)
		{
			val fragmentHaul: FragmentHaul = FragmentHaul.newInstance()

			supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, fragmentHaul).addToBackStack(null).commit()
		}
	}

	override fun onResume()
	{
		super.onResume()
	}

	override fun onStop()
	{
		super.onStop()
	}

	override fun onDestroy()
	{
		// super.onDestroy()
		val dialog: ProgressDialog = this.indeterminateProgressDialog(message = "Aguarde...", title = "Desconectando")
		dialog.show()
		TableQueriesUser(this).deleteUser("userData")
		// android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy()
	}
	//----------------------------------------------------------------------------------------------------//

	override fun onBackPressed()
	{
		if (fragmentManager.backStackEntryCount > 0)
		{
			fragmentManager.popBackStack()
		}
		else
		{
			// super.onBackPressed()
			finish()
		}

		/*val fragment = supportFragmentManager.findFragmentByTag("fragmentScanner")
		if ((fragment as? IOnBackPressed)?.onBackPressed()!!.not())
		{
			super.onBackPressed()
		}*/
	}
}