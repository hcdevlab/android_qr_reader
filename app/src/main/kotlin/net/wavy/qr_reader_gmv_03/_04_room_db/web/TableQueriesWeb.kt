package net.wavy.qr_reader_gmv_03._04_room_db.web

import android.app.ProgressDialog
import androidx.room.Room
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONException
import java.io.*
import java.net.MalformedURLException
import android.widget.Button
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.wavy.qr_reader_gmv_03._04_room_db.RoomDataBase
import net.wavy.qr_reader_gmv_03._04_room_db.user.TableQueriesUser
import java.util.*


class TableQueriesWeb(val externalContext: Context)
{
	private val treeMap: TreeMap<String, String> = TreeMap<String, String>()

	init
	{
		TableQueriesUser(externalContext).setCookieInTreeMap(treeMap)
		// Lo de abajo no sirve porque no llega a cargar lo anterior antes de lo siguiente.
		// val jwtCookie = treeMap.get("cookie")
		// val jwtCookie = treeMap.get("jwt_cookie")
		// displayLog("Cookie from DB Web: " + jwtCookie)
	}

	fun provideAppDataBase(): RoomDataBase
	{
		val db: RoomDataBase = Room.databaseBuilder(externalContext, RoomDataBase::class.java, "roomDataBase").build()
		return db
	}

	fun insertOneInDB(rowEntityWeb: RowEntityWeb, dbWebViewModel: DBWebViewModel): Unit
	{
		// launch(UI)
		// Lanza la 'coroutine' en el 'main thread'.
		GlobalScope.launch(Dispatchers.Main)
		{
			val query: Deferred<Long> = bg()
			{
				provideAppDataBase().getDaoWeb().insertOne(rowEntityWeb)
			}

			query.await()

			dbWebViewModel.refresh() // Activar.
			setObservableLiveData(dbWebViewModel)
		}
	}

	fun sendDataAndReceiveResponse(dbWebViewModel: DBWebViewModel, dialog: ProgressDialog, button: Button): Unit
	{
		// var data: List<RowEntityWeb> = listOf()

		// launch(UI)
		// Lanza la 'coroutine' en el 'main thread'.
		GlobalScope.launch(Dispatchers.Main)
		{
			dialog.show()

			val dataDeferred: Deferred<List<RowEntityWeb>> = bg()
			{
				provideAppDataBase().getDaoWeb().fetchAllRows()
			}

			val data: List<RowEntityWeb> = dataDeferred.await()
			displayLog("Antes de enviar: " + data.toString())

			val responseDeferred: Deferred<String> = bg()
			{
				serverRequestInsertAndGetResponse(data)
			}

			val response: String = responseDeferred.await()

			dialog.dismiss()

			displayLog("Respuesta del servidor: " + response)
			if (response.equals("success"))
			{
				// async(CommonPool) {
				GlobalScope.launch(Dispatchers.Default) {
					// provideAppDataBase().clearAllTables()
					provideAppDataBase().getDaoWeb().deleteAll(dbWebViewModel.observableLiveData.value!!)
				}.join()

				dbWebViewModel.refresh()
				setObservableLiveData(dbWebViewModel)

				button.visibility = View.INVISIBLE
				Toast.makeText(externalContext, "Datos insertados!", Toast.LENGTH_LONG).show();
			}
			else
			{
				button.visibility = View.VISIBLE
				Toast.makeText(externalContext, "Problemas de conexión!", Toast.LENGTH_LONG).show();
			}
		}
	}

	fun serverRequestInsertAndGetResponse(data: List<RowEntityWeb>): String
	{
		var response: String = ""
		try
		{
			val jsonArray: JSONArray = JSONArray()

			for (rowEntityWeb: RowEntityWeb in data)
			{
				val jsonObject: JSONObject = JSONObject()
				jsonObject.put("userName", rowEntityWeb.userName)
				jsonObject.put("date", rowEntityWeb.date)
				jsonObject.put("qrCode", rowEntityWeb.qrMachineCode)
				jsonObject.put("latCoordinate", rowEntityWeb.latCoordinate)
				jsonObject.put("longCoordinate", rowEntityWeb.longCoordinate)

				jsonArray.put(jsonObject)
			}
			// Para 'Spring'.
			// val url: URL = URL("https://www.wavyway.net/mobile/insert/")
			val url: URL = URL("https://d2ba9ddc3dc2.ngrok.io/mobile/insert/")
			// Usando 'Vert.x', 'Vertx_Kotlin_DSL_01'.
			// val url: URL = URL("http://192.168.0.11:8080/private/insert_remote/")
			val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			connection.requestMethod = "POST"
			connection.setRequestProperty("Content-Type", "application/json");
			// connection.setRequestProperty("Content-Type", "text/plain");
			connection.doInput = true
			connection.doOutput = true
			// Para enviar una 'Cookie', la obtenemos de 'SharedPreferences'.
			/*val cookie: String = externalContext.getSharedPreferences("dataSet", 0).getString("cookie", "")
			connection.setRequestProperty("Cookie", cookie)*/
			// Obtenemos la 'Cookie' del 'TreeMap', que se carga desde 'TableQueriesUser' en la función 'init' de esta clase.
			// connection.setRequestProperty("Cookie", treeMap.get("cookie"))
			val cookie: String = treeMap.get("jwt_cookie")!!
			displayLog("Ver la cookie a enviar: $cookie")
			/*
				El nombre de la 'cookie' (la 'key', no el 'value') tiene que estar coordinado con el
				usado en 'ServiceAuthorizationHandler' (servidor, en este caso el ejemplo usado es
				'Vertx_Kotlin_DSL_02').
			*/
			/*
				Usa la línea siguiente a la de abajo, el nombre de la 'request property' NO es la 'key'.
				El nombre de la 'key' que usa el ejemplo 'Vertx_Kotlin_DSL_02' es "jwt_cookie", y eso
				queda implícito en el 'string' obtenido del 'TreeMap'.
			*/
			// connection.setRequestProperty("jwt_cookie", treeMap.get("jwt_cookie")) // Nombre de la 'request property' incorrecto.
			connection.setRequestProperty("cookie", cookie) // Nombre de la 'request property' correcto.
			// connection.setRequestProperty("jwt_cookie", cookie)
			displayLog("Datos de la request: " + connection.requestProperties.values)

			val os: OutputStream = connection.outputStream
			val writer: BufferedWriter = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
			val dataToSend: String = jsonArray.toString()
			displayLog("Datos JSON toString: " + dataToSend)

			writer.write(dataToSend)

			writer.flush();
			writer.close();
			os.close();
			connection.connect();

			val code: Int = connection.responseCode
			displayLog("Response: " + code)
			if (code == HttpURLConnection.HTTP_OK)
			{
				response = "success"
			}
			else
			{
				response = "fail"
			}

			connection.disconnect()
		}
		catch (malformedUrlException: MalformedURLException)
		{
			Log.d("MalformedURLException:", malformedUrlException.message);
		}
		catch (ioeException: IOException)
		{
			Log.d("IOException: ", ioeException.message)
		}
		catch (jsonException: JSONException)
		{
			Log.d("JSONException:", jsonException.message)
		}
		return response
	}

	fun setObservableLiveData(dbWebViewModel: DBWebViewModel)
	{
		// launch(UI)
		GlobalScope.launch(Dispatchers.Main)
		// async(UI)
		{
			val dataDeferred: Deferred<List<RowEntityWeb>> = bg()
			{
				provideAppDataBase().getDaoWeb().fetchAllRows()
			}

			dbWebViewModel.mediatorLiveData.postValue(dataDeferred.await())
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
			Log.v("TAG Table Web", message.substring(start, end))
		}
	}
}
