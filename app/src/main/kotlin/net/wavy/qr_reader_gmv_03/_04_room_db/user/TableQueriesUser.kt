package net.wavy.qr_reader_gmv_03._04_room_db.user

import android.app.ProgressDialog
import androidx.room.Room
import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import net.wavy.qr_reader_gmv_03._04_room_db.RoomDataBase
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.coroutines.experimental.bg
import java.net.HttpURLConnection
import java.util.*

class TableQueriesUser(val externalContext: Context)
{
	fun provideAppDataBase(): RoomDataBase
	{
		val db: RoomDataBase = Room.databaseBuilder(externalContext, RoomDataBase::class.java, "roomDataBase").build()
		// val db: RoomDataBase = Room.databaseBuilder(externalContext, RoomDataBase::class.java, "roomDataBase").allowMainThreadQueries().build()
		return db
	}

	fun createUser(keyString: String, name: String, cookieString: String)
	{
		GlobalScope.launch(Dispatchers.Default)
		{
			val query = GlobalScope.launch(Dispatchers.Default)
			{
				val user = RowEntityUser(null, keyString, name, cookieString)
				displayLog("Create user: " + user.toString())
				provideAppDataBase().getDaoUser().insertUser(user)
			}.join()
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //
	/*
		Se usa para obtener el nombre del 'user' que ingresó en la 'app'.
		Como no se pueden hacer consultas directas a la base con 'Room', para sacarlo lo colocamos en un 'TreeMap'.
		Solamente es utilizado en 'FragmentScanner'- 'onAttach'.
	*/
	fun setUserNameInTreeMap(treeMap: TreeMap<String, String>)
	{
		GlobalScope.launch(Dispatchers.Main)
		{
			// ------------------------------------------------------------------------------------------ //
			val query = GlobalScope.launch(Dispatchers.Default)
			{
				// Obtenemos todos los campos del 'user' guardados en la tabla; el mismo se determina en 'MainUI'- 'getResponseWithCredentials'.
				val userData: RowEntityUser = provideAppDataBase().getDaoUser().fetchEntityByKey("userData")
				displayLog("Set name: " + userData.toString())
				treeMap.put("name", userData.userName)
			}.join()
			// ------------------------------------------------------------------------------------------ //

			// ------------------------------------------------------------------------------------------ //
			// Usando 'async' desde una 'fun'.
			// treeMap.put("name", getUserNameAsync().await())

			// Usando 'Channel' desde una 'fun'.
			// treeMap.put("name", getUserNameChannel().receive())
			// ------------------------------------------------------------------------------------------ //
		}
	}

	/*
		Se usa para obtener el nombre del 'user' que ingresó en la 'app'.
		Como no se pueden hacer consultas directas a la base con 'Room', para sacarlo lo colocamos en un 'TreeMap'.
		Solamente es utilizado en 'TableQueriesWeb'- 'init'.
	*/
	fun setCookieInTreeMap(treeMap: TreeMap<String, String>)
	{
		GlobalScope.launch(Dispatchers.Main)
		{
			// ------------------------------------------------------------------------------------------ //
			// Usando 'launch'.
			val query = GlobalScope.launch(Dispatchers.Default)
			{
				val userData: RowEntityUser = provideAppDataBase().getDaoUser().fetchEntityByKey("userData")
				treeMap.put("jwt_cookie", userData.jwtCookie)
			}.join()
			// ------------------------------------------------------------------------------------------ //

			// ------------------------------------------------------------------------------------------ //
			// Usando 'async'.
			// val dataDeferred: Deferred<RowEntityUser> = bg()
			/*val dataDeferred: Deferred<RowEntityUser> = GlobalScope.async(Dispatchers.Default)
			{
				provideAppDataBase().getDaoUser().fetchEntityByKey("userData")
			}
			val rowEntityUser: RowEntityUser = dataDeferred.await()
			treeMap.put("cookie", rowEntityUser.jwtCookie)*/
			// ------------------------------------------------------------------------------------------ //

			// ------------------------------------------------------------------------------------------ //
			// Usando 'async' desde una 'fun'.
			// treeMap.put("cookie", getJWTCookieAsync().await())

			// Usando 'Channel' desde una 'fun'.
			// treeMap.put("cookie", getJWTCookieChannel().receive())
			// treeMap.put("jwt_cookie", getJWTCookieChannel().receive())
			// ------------------------------------------------------------------------------------------ //
		}
	}
	// -------------------------------------------------------------------------------------------------------------- //

	// -------------------------------------------------------------------------------------------------------------- //
	/*
		Para invocar la 'fun' de abajo usar:
		val userName: Deferred<String> = getUserNameAsync()
		GlobalScope.launch(Dispatchers.Default)
		{
			userName.join()
		}
	*/
	/*
	fun getUserNameAsync(): Deferred<String> = GlobalScope.async {
		return@async provideAppDataBase().getDaoUser().fetchEntityByKey("userData").userName
	}

	fun getJWTCookieAsync(): Deferred<String> = GlobalScope.async {
		return@async provideAppDataBase().getDaoUser().fetchEntityByKey("userData").jwtCookie
	}
	// -------------------------------------------------------------------------------------------------------------- //

	// -------------------------------------------------------------------------------------------------------------- //
	// Con 'produce' 'Channel'.
	// fun getUserNameChannel(): ReceiveChannel<String> = CoroutineScope(GlobalScope.coroutineContext).produce {
	fun getUserNameChannel(): ReceiveChannel<String> = GlobalScope.produce {
		val name = provideAppDataBase().getDaoUser().fetchEntityByKey("userData").userName
		send(name)
	}

	// fun getJWTCookieChannel(): ReceiveChannel<String> = CoroutineScope(GlobalScope.coroutineContext).produce {
	fun getJWTCookieChannel(): ReceiveChannel<String> = GlobalScope.produce {
		val jwtCookie = provideAppDataBase().getDaoUser().fetchEntityByKey("userData").jwtCookie
		send(jwtCookie)
	}*/
	// -------------------------------------------------------------------------------------------------------------- //

	// -------------------------------------------------------------------------------------------------------------- //
	// NO FUNCIONAN. Con 'actor'.
	/*fun getUserNameActor(treeMap: TreeMap<String, String>): SendChannel<String> = GlobalScope.actor {
		val name: String = provideAppDataBase().getDaoUser().fetchEntityByKey("userData").userName
		treeMap.put("name", name)
	}

	fun getJWTCookieActor(treeMap: TreeMap<String, String>): SendChannel<String> = GlobalScope.actor {
		val jwtCookie: String = provideAppDataBase().getDaoUser().fetchEntityByKey("userData").jwtCookie
		treeMap.put("cookie", jwtCookie)
	}*/
	// -------------------------------------------------------------------------------------------------------------- //

	// -------------------------------------------------------------------------------------------------------------- //
	fun deleteUser(keyString: String)
	{
		GlobalScope.launch(Dispatchers.Default)
		{
			val query = GlobalScope.launch(Dispatchers.Default)
			{
				provideAppDataBase().getDaoUser().deleteEntityByKey(keyString)
				// provideAppDataBase().getDaoUser().deleteUser()
			}.join()
			android.os.Process.killProcess(android.os.Process.myPid())
		}
	}

	// fun deleteUserAndGetOut(dialog: ProgressDialog, keyString: String)
	fun deleteUserAndGetOut(dialog: ProgressDialog)
	{
		dialog.show()
		val bgTask = GlobalScope.launch(Dispatchers.Default)
		{
			// dialog.show()
			GlobalScope.launch(Dispatchers.Default)
			{
				// provideAppDataBase().getDaoUser().deleteEntityByKey(keyString)
				provideAppDataBase().getDaoUser().deleteUser()
			}.join().let {
				displayLog("From let 1" + this) // Receiver.
				displayLog("From let 2" + it) // Argument.
				// Cerramos el 'dialog'.
				dialog.dismiss()
				// Detenemos la aplicación:
				android.os.Process.killProcess(android.os.Process.myPid())
			}
			/*// Cerramos el 'dialog'.
			dialog.dismiss()
			// Detenemos la aplicación:
			android.os.Process.killProcess(android.os.Process.myPid())*/
		}
	}
	// -------------------------------------------------------------------------------------------------------------- //
	// -------------------------------------------------------------------------------------------------------------- //

	fun displayLog(message: String)
	{
		val maxLogSize = 1000
		val stringLength = message.length
		for (i in 0..stringLength / maxLogSize)
		{
			val start = i * maxLogSize
			var end = (i + 1) * maxLogSize
			end = if (end > message.length) message.length else end
			Log.v("TAG Table User", message.substring(start, end))
		}
	}
}