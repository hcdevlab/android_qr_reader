
package net.wavy.qr_reader_gmv_03._01_prelogin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import kotlinx.coroutines.*
import net.wavy.qr_reader_gmv_03.R
import net.wavy.qr_reader_gmv_03._02_postlogin.LoggedActivity
import net.wavy.qr_reader_gmv_03._04_room_db.user.TableQueriesUser
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.*

class MainUI : AnkoComponent<MainActivity>
{
	override fun createView(ui: AnkoContext<MainActivity>): View = with(ui)
	{
		constraintLayout {
			id = R.id.constraintLayoutMainUI
			fitsSystemWindows = true

			lparams(width = matchParent, height = matchParent)

			frameLayout {
				id = R.id.toolbarLayoutMainUI

				lparams {
					width = matchParent
					height = wrapContent
				}

				toolbar()
				{
					id = R.id.toolbarMainUI

					lparams(width = matchParent, height = wrapContent)
					// val menuBarColor = ContextCompat.getColor(context, R.color.menuBar)
					backgroundColor = Color.DKGRAY
					// backgroundColor = ContextCompat.getColor(context, R.color.menuBar)

					title = "QR Scanner App"
					setTitleTextColor(Color.LTGRAY)

					val iconMenu: Drawable = ContextCompat.getDrawable(ctx, R.drawable.menu_icon)!!
					overflowIcon = iconMenu
					overflowIcon?.setColorFilter(ContextCompat.getColor(context, R.color.menuIcon), PorterDuff.Mode.SRC_ATOP)

					menu.add(1, R.id.itemMainUIExit, 1, "Salir")
					onMenuItemClick {
						when (it!!.itemId)
						{
							R.id.itemMainUIExit ->
							{
								finishApp()
							}
						}
					}
				}
			}

			verticalLayout {
				// constraintLayout {
				// fitsSystemWindows = true

				lparams {
					// startToEnd = R.id.toolbar
					width = matchParent
					height = matchParent

					/*width = MATCH_CONSTRAINT
					height = MATCH_CONSTRAINT*/
				}

				gravity = Gravity.CENTER_HORIZONTAL.plus(Gravity.CENTER_VERTICAL)

				checkIsLocationSystemEnabled(ctx)

				verticalLayout {
					lparams {
						width = wrapContent
						height = wrapContent
					}

					val img: ImageView = imageView(R.drawable.ic_account)
					{
						Gravity.CENTER_HORIZONTAL
					}
				}

				val editTextUser = editText() {
					hint = "Usuario"
					gravity = Gravity.CENTER
					inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
					typeface = Typeface.SANS_SERIF
					background = ContextCompat.getDrawable(context, R.drawable.text_box)
				}
				.lparams {
					width = dip(250)
					topMargin = dip(5)
				}

				val editTextPassword = editText() {
					hint = "Contraseña"
					gravity = Gravity.CENTER
					inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
					typeface = Typeface.SANS_SERIF
					background = ContextCompat.getDrawable(context, R.drawable.text_box)
				}.lparams {
					width = dip(250)
					topMargin = dip(15)
				}

				button("Enviar datos") {
					gravity = Gravity.CENTER
					// background = ColorDrawable(Color.parseColor("#F8F2F2"))
					background = ContextCompat.getDrawable(context, R.drawable.button_style)

					onClick() {
						owner.UI {
							if (checkLocationEnabled(owner))
							{
								// sendDataAndReceiveResponse(owner.ctx, editTextUser, editTextPassword)
								sendDataAndReceiveResponse(owner, editTextUser, editTextPassword)
							}
							else
							{
								finishApp()
							}
						}
					}
					// }.lparams(dip(250), sp(60))
				}.lparams {
					width = dip(250)
					height = dip(50)
					topMargin = dip(15)
				}
			}
		}
	}

	// 1 de 3
	fun sendDataAndReceiveResponse(context: Context, userEditText: EditText, passwordEditText: EditText): Unit
	{
		if ((userEditText.text.isNullOrEmpty()) || (passwordEditText.text.isNullOrEmpty()))
		{
			context.toast("Complete todos los datos.")
			userEditText.setText("")
			userEditText.error = "Usuario"
			passwordEditText.setText("")
			passwordEditText.error = "Contraseña"
			userEditText.requestFocus()
		}
		else
		{
			// val dialog: ProgressDialog = context.indeterminateProgressDialog(message = "Aguarde...", title = "Enviando datos")
			val dialog: ProgressDialog = context.indeterminateProgressDialog(message = "Aguarde...", title = "Enviando datos")
			val user = userEditText.text.toString()
			val password = passwordEditText.text.toString()
			// launch(UI)
			GlobalScope.launch(Dispatchers.Main)
			{
				dialog.show()
				// val loginResponseDeferred: Deferred<String> = bg()
				val loginResponseDeferred: Deferred<String> = async(Dispatchers.Default)
				{
					sendCredentials(context, user, password)
				}

				val response: String = loginResponseDeferred.await()
				displayLog("La response desde la fun asíncrona: " + response)
				dialog.dismiss()
				when (response)
				{
					"unavailable"  ->
					{
						context.toast("Servicio no disponible." + "\n" + "Inténtelo más tarde.")
					}
					"unauthorized" ->
					{
						userEditText.setText("")
						passwordEditText.setText("")
						userEditText.requestFocus()
						context.toast("Acceso denegado." + "\n" + "Revise los datos ingresados.")
					}
					"fail"         ->
					{
						userEditText.setText("")
						passwordEditText.setText("")
						userEditText.requestFocus()
						context.toast("Acceso denegado." + "\n" + "Revise los datos ingresados.")
					}
					"success"      ->
					{
						context.startActivity<LoggedActivity>()
					}
				}
			}
		}
	}

	// 2 de 3
	fun sendCredentials(context: Context, userName: String, password: String): String
	{
		var response: String = ""
		val jsonObject: JSONObject = JSONObject()
		jsonObject.put("username", userName)
		jsonObject.put("password", password)

		val credentialsToSend: String = jsonObject.toString()
		try
		{
			// Para 'Spring'.
			// val url: URL = URL("https://www.wavyway.net/mobile/secpath")
			val url: URL = URL("https://d2ba9ddc3dc2.ngrok.io/mobile/secpath")
			// Para 'Vert.x'.
			// val url: URL = URL("https://e502eaa2.ngrok.io/path/login/")
			val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			connection.requestMethod = "POST"
			connection.setRequestProperty("Content-Type", "application/json");
			connection.doInput = true
			connection.doOutput = true

			val os: OutputStream = connection.outputStream
			val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
			displayLog("Datos JSON toString: " + credentialsToSend)
			writer.write(credentialsToSend)

			writer.flush();
			writer.close();
			os.close();
			connection.connect();

			response = getResponseWithCredentials(context, connection, userName)
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

	// 3 de 3
	fun getResponseWithCredentials(context: Context, connection: HttpURLConnection, userName: String): String
	{
		var response: String = ""
		val code: Int? = connection.responseCode
		displayLog("Response: " + code)
		if (code != null)
		{
			when (code)
			{
				HttpURLConnection.HTTP_UNAVAILABLE  ->
				{
					// Unavailable - 503
					response = "unavailable"
				}
				HttpURLConnection.HTTP_UNAUTHORIZED ->
				{
					displayLog("Problemas de autorización!")
					// Unauthorized - 401
					response = "unauthorized"
				}
				HttpURLConnection.HTTP_OK           ->
				{
					val COOKIES_HEADER: String = "Set-Cookie";

					val headerFields: Map<String, List<String>> = connection.headerFields;
					displayLog("Los headers: " + headerFields.values.toString())

					val cookiesHeader: List<String>? = headerFields.get(COOKIES_HEADER)!!
					displayLog("Cookies en el header: " + cookiesHeader.toString());

					// -------------------------------------------------------------------------------------------------------------- //
					// Para 'Spring'.
					if (cookiesHeader != null)
					{
						for (authorizationCookie: String in cookiesHeader)
						{
							//--------------------------------------------------------------------------------//
							// COOKIE_MANAGER.getCookieStore().add(null, HttpCookie.parse(authorizationCookie).get(0));
							val cookieReceived: HttpCookie = HttpCookie.parse(authorizationCookie).get(0);
							displayLog("Cookie name: " + cookieReceived.name) // El 'name' es 'Authorization'.
							displayLog("Cookie value: " + cookieReceived.value) // El 'value' es: 'Bearer eyJhbGciOiJIUzUxMiJ9...'.

							TableQueriesUser(context).createUser("userData", userName, cookieReceived.toString())
							//--------------------------------------------------------------------------------//
						}
					}
					// -------------------------------------------------------------------------------------------------------------- //

					// -------------------------------------------------------------------------------------------------------------- //
					// Para 'Vert.x'.
					// if (cookiesHeader != null)
					/*if (!cookiesHeader.isNullOrEmpty())
					{
						for (authorizationCookie: String in cookiesHeader)
						{*/
							//--------------------------------------------------------------------------------//
							// COOKIE_MANAGER.getCookieStore().add(null, HttpCookie.parse(authorizationCookie).get(0));
							/*val cookieReceived: HttpCookie = HttpCookie.parse(authorizationCookie).get(0);
							displayLog("Cookie name: " + cookieReceived.name) // El 'name' es 'Authorization'.
							displayLog("Cookie value: " + cookieReceived.value) // El 'value' es: 'Bearer eyJhbGciOiJIUzUxMiJ9...'.
							displayLog("Cookie complete: " + cookieReceived.toString()) // El 'value' es: 'Bearer eyJhbGciOiJIUzUxMiJ9...'.

							// TableQueriesUser(context).createUser("userData", userName, cookieReceived.toString())
							if (cookieReceived.name == "jwt_cookie")
							{
								displayLog("Lo buscado: ${cookieReceived.value}")
								TableQueriesUser(context).createUser("userData", userName, cookieReceived.toString())
							}
							/*val firstCookieName = cookieReceived.name.get(0)
							val firstCookieValue = cookieReceived.value.get(0)
							displayLog("Reciente: $firstCookieName, $firstCookieValue")*/
							// TableQueriesUser(context).createUser("userData", userName, cookieReceived.toString())
							// TableQueriesUser(context).createUser("userData", userName, cookieReceived.value.toString())
							//--------------------------------------------------------------------------------//
						}
					}*/
					response = "success"
				}
				else                                ->
				{
					response = "fail"
				}
			}
		}
		return response
	}

	// COMPROBAR QUE EL GPS ESTÉ ACTIVO
	// 1 de 3
	private fun checkLocationEnabled(context: Context): Boolean
	{
		val booleanResult: Boolean
		val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) != true)
		{
			booleanResult = false
		}
		else
		{
			booleanResult = true
		}
		return booleanResult
	}

	// 2 de 3
	private fun checkIsLocationSystemEnabled(context: Context): Unit
	{
		if (!checkLocationEnabled(context))
		{
			showAlert(context);
		}
	}

	// 3 de 3
	private fun showAlert(context: Context): Unit
	{
		val alertMessage: String =
				"""El Sistema de Geolocalización está desactivado.
				|Por favor actívelo para que la aplicación pueda funcionar correctamente.""".trimMargin()
		val dialog = context.alert(message = alertMessage, title = "GPS Desactivado") {
			yesButton {
				val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
				context.startActivity(intent)
			}
			noButton {
				finishApp()
			}
		}.build()
		dialog.setCancelable(false)
		dialog.setCanceledOnTouchOutside(false)
		return dialog.show()
	}

	fun finishApp()
	{
		android.os.Process.killProcess(android.os.Process.myPid())
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
			Log.v("TAG", message.substring(start, end))
		}
	}
}
