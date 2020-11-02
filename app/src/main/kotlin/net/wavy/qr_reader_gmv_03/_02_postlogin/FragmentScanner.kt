
package net.wavy.qr_reader_gmv_03._02_postlogin

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.coroutines.*
import net.wavy.qr_reader_gmv_03.R
import net.wavy.qr_reader_gmv_03._04_room_db.recycler.TableQueriesRecycler
import net.wavy.qr_reader_gmv_03._04_room_db.user.TableQueriesUser
import net.wavy.qr_reader_gmv_03._04_room_db.web.DBWebViewModel
import net.wavy.qr_reader_gmv_03._04_room_db.web.RowEntityWeb
import net.wavy.qr_reader_gmv_03._04_room_db.web.TableQueriesWeb
import net.wavy.qr_reader_gmv_03._02_postlogin.graphic.CameraSourcePreview
import net.wavy.qr_reader_gmv_03._02_postlogin.graphic.GraphicOverlay
import net.wavy.qr_reader_gmv_03._03_location_livedata.LocationViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FragmentScanner : Fragment(), LifecycleOwner
{
	// ---------------------------------------------------------------------------------------------------- //
	// Intent request code to handle updating play services if needed.
	private val RC_HANDLE_GMS = 9001
	// Permission request codes need to be < 256.
	private val RC_HANDLE_CAMERA_PERM = 2

	var cameraSource: CameraSource? = null
	val cameraSourcePreview: CameraSourcePreview by lazy { find<CameraSourcePreview>(R.id.preview) as CameraSourcePreview }
	val graphicOverlay: GraphicOverlay by lazy { find<GraphicOverlay>(R.id.graphicOverlay) as GraphicOverlay }
	// ---------------------------------------------------------------------------------------------------- //

	// ---------------------------------------------------------------------------------------------------- //
	// private val locationViewModel: LocationViewModel by lazy { ViewModelProviders.of(this@FragmentScanner.act).get(LocationViewModel::class.java) }
	private val locationViewModel: LocationViewModel by lazy { ViewModelProviders.of(requireActivity()).get(LocationViewModel::class.java) }
	// private val dbWebViewModel: DBWebViewModel by lazy { ViewModelProviders.of(this@FragmentScanner.act).get(DBWebViewModel::class.java) }
	private val dbWebViewModel: DBWebViewModel by lazy { ViewModelProviders.of(requireActivity()).get(DBWebViewModel::class.java) }
	// private val tableQueriesWeb: TableQueriesWeb by lazy { TableQueriesWeb(this@FragmentScanner.act) }
	private val tableQueriesWeb: TableQueriesWeb by lazy { TableQueriesWeb(requireContext()) }
	val treeMap: TreeMap<String, String> = TreeMap<String, String>()
	private var stringDecoded: String = ""
	private var previousStringDecoded: String = ""
	// ---------------------------------------------------------------------------------------------------- //

	// ---------------------------------------------------------------------------------------------------- //
	val buttonSave: Button by lazy { find<Button>(R.id.buttonSave) as Button }
	val buttonSend: Button by lazy { find<Button>(R.id.buttonSend) as Button }
	val textViewLabel: TextView by lazy { find<TextView>(R.id.textViewLabel) as TextView }
	val textViewCounter: TextView by lazy { find<TextView>(R.id.textViewCounter) as TextView }
	// ---------------------------------------------------------------------------------------------------- //

	//----------------------------------------------------------------------------------------------------//
	companion object
	{
		fun newInstance(): FragmentScanner
		{
			return FragmentScanner()
		}
	}
	//----------------------------------------------------------------------------------------------------//

	//----------------------------------------------------------------------------------------------------//
	fun initViewModel()
	{
		// locationViewModel.mediatorLiveData.observe(this@FragmentScanner.activity!!, object : Observer<Location>
		locationViewModel.mediatorLiveData.observe(this, object : Observer<Location>
		{
			override fun onChanged(location: Location?)
			{
				location!!
			}
		})
	}

	fun initObservableRows()
	{
		dbWebViewModel.mediatorLiveData.observe(this@FragmentScanner, object : Observer<List<RowEntityWeb>>
		{
			override fun onChanged(lre: List<RowEntityWeb>?)
			{
				/*val tv: TextView = find<TextView>(R.id.textViewCounter)
				tv.text = lre!!.size.toString()*/
				textViewCounter.text = lre!!.size.toString()

				when (lre.isEmpty())
				{
					true  ->
					{
						/*val buttonSendData: Button = find<Button>(R.id.buttonSend)
						buttonSendData.visibility = View.INVISIBLE*/
						buttonSend.visibility = View.INVISIBLE
						// tv.text = lre.size.toString()
						textViewCounter.text = lre.size.toString()
					}
					false ->
					{
						/*val buttonSendData: Button = find<Button>(R.id.buttonSend)
						buttonSendData.visibility = View.VISIBLE*/
						buttonSend.visibility = View.VISIBLE
						displayLog("Hay un nuevo registro!")
						// tv.text = lre.size.toString()
						textViewCounter.text = lre.size.toString()
					}
				}
			}
		})
	}
	//----------------------------------------------------------------------------------------------------//

	//----------------------------------------------------------------------------------------------------//
	override fun onAttach(context: Context)
	{
		super.onAttach(context)
		initObservableRows()
		initViewModel()
		// TableQueriesUser(this@FragmentScanner.act).setUserNameInTreeMap(treeMap)
		TableQueriesUser(requireActivity()).setUserNameInTreeMap(treeMap)
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val rootView: View = inflater.inflate(R.layout.scanner_layout, container, false)
		return rootView
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		super.onViewCreated(view, savedInstanceState)
		/*
			Check for the camera permission before accessing the camera. If the
			permission is not granted yet, request permission.
		*/
		// val checkPermission: Int = ActivityCompat.checkSelfPermission(this@FragmentScanner.act, Manifest.permission.CAMERA);
		val checkPermission: Int = ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA);
		if (checkPermission == PackageManager.PERMISSION_GRANTED)
		{
			// createCameraSource(autoFocus, useFlash);
			createCameraSource(true, true);
		}
		else
		{
			requestCameraPermission();
		}

		buttonSave.visibility = View.INVISIBLE
		buttonSend.visibility = View.INVISIBLE

		textViewLabel.textSize = 16f
		textViewLabel.text = "datos guardados: ".toUpperCase()

		textViewCounter.textSize = 16f
		textViewCounter.typeface = Typeface.DEFAULT_BOLD

		buttonSave.onClick {
			alert(message = "Guardar datos", title = "Confirmar") {
				noButton {
					// Al presionar el botón 'No', el 'alert' se cierra y el botón 'Guardar' se invisibiliza.
					buttonSave.visibility = View.INVISIBLE
				}
				yesButton()
				{
					createObjectAndInsertInDB(locationViewModel, stringDecoded)
					buttonSave.visibility = View.INVISIBLE
				}
			}.show()
		}

		buttonSend.onClick {
			alert(message = "Enviar datos", title = "Confirmar") {
				noButton {
				}
				yesButton()
				{
					val dialog: ProgressDialog = indeterminateProgressDialog(message = "Aguarde...", title = "Enviando datos")
					tableQueriesWeb.sendDataAndReceiveResponse(dbWebViewModel, dialog, buttonSend)
				}
			}.show()
		}
	}

	// Restarts the camera.
	override fun onResume()
	{
		super.onResume()
		startCameraSource()
	}

	// Stops the camera.
	override fun onPause()
	{
		super.onPause()
		if (cameraSourcePreview != null)
		{
			cameraSourcePreview.stop();
		}
	}

	/*
		Releases the resources associated with the camera source, the associated detectors, and the
		rest of the processing pipeline.
	*/
	override fun onDestroy()
	{
		super.onDestroy()
		if (cameraSourcePreview != null)
		{
			cameraSourcePreview.release()
		}
	}
	//----------------------------------------------------------------------------------------------------//

	//----------------------------------------------------------------------------------------------------//
	@SuppressLint("InlinedApi")
	private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean)
	{
		val funContext: Context = this@FragmentScanner.act

		/*
			A barcode detector is created to track barcodes. An associated multi-processor instance
			is set to receive the barcode detection results, track the barcodes, and maintain
			graphics for each barcode on screen.  The factory is used by the multi-processor to
			create a separate tracker instance for each barcode.
			val barcodeDetector = BarcodeDetector.Builder(funContext).build()
		*/
		val barcodeDetector = BarcodeDetector
				.Builder(funContext)
				.setBarcodeFormats(Barcode.ALL_FORMATS)
				.build()

		// ---------------------------------------------------------------------------------------------------- //
		// Con 'Detector.Processor<Barcode>'.
		val processorBarcode: Detector.Processor<Barcode> = object : Detector.Processor<Barcode>
		{
			override fun release()
			{
			}

			override fun receiveDetections(detections: Detector.Detections<Barcode>?)
			{
				// Parse a detected QR code.
				val sparseArrayBarcode: SparseArray<Barcode> = detections!!.detectedItems

				if (sparseArrayBarcode.size() > 0)
				{
					// Obtenemos el stringDecoded:
					stringDecoded = sparseArrayBarcode.valueAt(0).displayValue.toString()
					displayLog("Detectado: " + stringDecoded)

					if (!(stringDecoded.equals(previousStringDecoded)))
					{
						previousStringDecoded = stringDecoded
						displayLog("Token: " + stringDecoded)

						GlobalScope.launch(Dispatchers.Main)
						{
							val deferredQRString: Deferred<String> = GlobalScope.async(Dispatchers.Default)
							// val deferredQRString: Deferred<String> = bg()
							{
								previousStringDecoded = ""
								stringDecoded
							}
							stringDecoded = deferredQRString.await()

							displayLog("Viendo el resultado: " + stringDecoded)
							buttonSave.visibility = View.VISIBLE
						}
					}
				}
			}
		}
		// ---------------------------------------------------------------------------------------------------- //

		// ---------------------------------------------------------------------------------------------------- //
		// Asignamos el 'Detector.Processor<Barcode>' al 'BarcodeDetector'.
		barcodeDetector.setProcessor(processorBarcode)
		// ---------------------------------------------------------------------------------------------------- //

		if (!barcodeDetector.isOperational)
		{
			/*
				Note: The first time that an app using the barcode or face API is installed on a
				device, GMS will download a native libraries to the device in order to do detection.
				Usually this completes before the app is run for the first time. But if that
				download has not yet completed, then the above call will not detect any barcodes
				and/or faces.

				isOperational() can be used to check if the required native libraries are currently
				available. The detectors will automatically become operational once the library
				downloads complete on device.
			*/
			displayLog("Detector dependencies are not yet available.")

			/*
				Check for low storage.  If there is low storage, the native library will not be
				downloaded, so detection will not become operational.
			*/
			val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)

			// Lo de abajo funciona en 'Activities', tiene que ver con 'BroadcastReceiver'.
			val hasLowStorage = funContext.registerReceiver(null, lowstorageFilter) != null

			if (hasLowStorage)
			{
				toast(R.string.low_storage_error)
				displayLog(getString(R.string.low_storage_error))
			}
		}

		/*
			Creates and starts the camera. Note that this uses a higher resolution in comparison
			to other detection examples to enable the barcode detector to detect small barcodes
			at long distances.
		*/
		var builder: CameraSource.Builder = CameraSource.Builder(funContext, barcodeDetector)
				.setFacing(CameraSource.CAMERA_FACING_BACK)
				.setRequestedPreviewSize(1600, 1024)
				// .setRequestedPreviewSize(800, 600)
				.setRequestedFps(15.0f)

		// Make sure that auto focus is an available option.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			builder = builder.setAutoFocusEnabled(true)
		}

		cameraSource = builder.build()
	}

	/*
		Handles the requesting of the camera permission.  This includes
		showing a "Snackbar" message of why the permission is needed then
		sending the request.
	*/
	fun requestCameraPermission()
	{
		displayLog("Camera permission is not granted. Requesting permission.")

		if ((ActivityCompat.checkSelfPermission(this@FragmentScanner.act, android.Manifest.permission.CAMERA)) != (PackageManager.PERMISSION_GRANTED))
		{
			requestPermissions(arrayOf(Manifest.permission.CAMERA), RC_HANDLE_CAMERA_PERM)
			return;
		}

		val listener: View.OnClickListener = View.OnClickListener() {
			requestPermissions(arrayOf(Manifest.permission.CAMERA), RC_HANDLE_CAMERA_PERM)
		};

		/*Snackbar.make(graphicOverlay, R.string.permission_camera_rationale,
				Snackbar.LENGTH_INDEFINITE)
				.setAction(R.string.ok, listener)
				.show();*/
	}

	/*
		Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
		(e.g., because onResume was called before the camera source was created), this will be called
		again when the camera source is created.
	*/
	@Throws(SecurityException::class)
	private fun startCameraSource()
	{
		// Check that the device has play services available.
		val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@FragmentScanner.act)
		if (code != ConnectionResult.SUCCESS)
		{
			// val dlg = GoogleApiAvailability.getInstance().getErrorDialog(requireActivity(), code, RC_HANDLE_GMS)
			val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this@FragmentScanner.act, code, RC_HANDLE_GMS)
			dlg.show()
		}

		if (cameraSource != null)
		{
			try
			{
				cameraSourcePreview.start(cameraSource!!, graphicOverlay)
			}
			catch (e: IOException)
			{
				displayLog("Unable to start camera source." + e)
				cameraSource!!.release()
				cameraSource = null
			}

		}
	}
	//----------------------------------------------------------------------------------------------------//

	//----------------------------------------------------------------------------------------------------//
	fun createObjectAndInsertInDB(locationModel: LocationViewModel, qrString: String): Unit
	{
		//----------------------------------------------------------------------//
		val treeMapName: String = treeMap.get("name")!!
		displayLog("Treemap userName: " + treeMapName.toString())

		val name: String = when (treeMapName)
		{
			"dflores"  ->
			{
				"Diego Flores"
			}
			"gsarutte" ->
			{
				"Gastón Sarutte"
			}
			"manager"  ->
			{
				"manager"
			}
			// else       -> userName
			else       -> treeMapName
		}
		//----------------------------------------------------------------------//

		//----------------------------------------------------------------------//
		val dateFromLocationManager: Date = Date(locationModel.observableLiveData.value!!.time)
		val date: SimpleDateFormat = SimpleDateFormat("EEEE dd MMMM yyyy - HH:mm:ss")
		val dateFormat: String = date.format(dateFromLocationManager)
		//----------------------------------------------------------------------//

		//----------------------------------------------------------------------//
		val latCoord: String = locationModel.observableLiveData.value!!.latitude.toString()
		val longCoord: String = locationModel.observableLiveData.value!!.longitude.toString()
		//----------------------------------------------------------------------//

		/*
		//----------------------------------------------------------------------//
		if ((name.isNullOrEmpty())
				or (dateFormat.isNullOrEmpty())
				or (qrString.isNullOrEmpty())
				or (dateFormat.isNullOrEmpty())
				or (latCoord.isNullOrEmpty())
				or (longCoord.isNullOrEmpty()))
		{
			toast("Algún elemento está llegando vacío...")
			finishApp()
		}
		//----------------------------------------------------------------------//

		//----------------------------------------------------------------------//
		val rowEntityWeb = RowEntityWeb(
				null,
				// user,
				name,
				dateFormat,
				qrString,
				latCoord,
				longCoord
		)
		//----------------------------------------------------------------------//

		//----------------------------------------------------------------------//
		displayLog("RowEntityWeb modified: " + rowEntityWeb.toString())
		tableQueriesWeb.insertOneInDB(rowEntityWeb, dbWebViewModel)
		// TableQueriesRecycler(this@FragmentScanner.requireContext()).changeState(qrString, true)
		TableQueriesRecycler(requireContext()).changeState(qrString, true)
		//----------------------------------------------------------------------//
		*/

		/*
			'isNullOrEmpty': permite 'whitespaces'
			'isNullOrBlank': no permite 'whitespaces'
		*/
		if ((name.isNullOrEmpty())
				or (dateFormat.isNullOrEmpty())
				or (qrString.isNullOrEmpty())
				or (dateFormat.isNullOrEmpty())
				or (latCoord.isNullOrEmpty())
				or (longCoord.isNullOrEmpty()))
		{
			toast("Algún elemento está llegando vacío...")
			finishApp()
		}
		else
		{
			val rowEntityWeb = RowEntityWeb(
					null,
					// user,
					name,
					dateFormat,
					qrString,
					latCoord,
					longCoord
			)

			displayLog("RowEntityWeb modified: " + rowEntityWeb.toString())
			tableQueriesWeb.insertOneInDB(rowEntityWeb, dbWebViewModel)
			// TableQueriesRecycler(this@FragmentScanner.requireContext()).changeState(qrString, true)
			TableQueriesRecycler(requireContext()).changeState(qrString, true)
		}
	}
	//----------------------------------------------------------------------------------------------------//

	//----------------------------------------------------------------------------------------------------//
	override fun onRequestPermissionsResult(requestCode: Int,
	                                        permissions: Array<String>,
	                                        grantResults: IntArray)
	{
		if (requestCode != RC_HANDLE_CAMERA_PERM)
		{
			super.onRequestPermissionsResult(requestCode, permissions, grantResults)
			return
		}

		if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
		{
			createCameraSource(true, true)
			return
		}

		displayLog("Permission not granted: results len = " + grantResults.size + " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)")

		val listener = DialogInterface.OnClickListener { dialog, id -> finishApp() }

		val builder = AlertDialog.Builder(this@FragmentScanner.act)
		builder.setTitle("Multitracker sample")
				.setMessage(R.string.no_camera_permission)
				.setPositiveButton(R.string.ok, listener)
				.show()
	}
	//----------------------------------------------------------------------------------------------------//

	//----------------------------------------------------------------------------------------------------//
	fun finishApp()
	{
		android.os.Process.killProcess(android.os.Process.myPid())
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
}