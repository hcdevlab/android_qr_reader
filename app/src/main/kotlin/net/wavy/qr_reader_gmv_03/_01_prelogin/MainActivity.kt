
package net.wavy.qr_reader_gmv_03._01_prelogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.setContentView

class MainActivity : AppCompatActivity()
{

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		MainUI().setContentView(this)
	}

	//----------------------------------------------------------------------------------------------------//
	override fun onStop()
	{
		super.onStop()
	}

	override fun onDestroy()
	{
		super.onDestroy()
		// android.os.Process.killProcess(android.os.Process.myPid());
	}
	//----------------------------------------------------------------------------------------------------//
}
