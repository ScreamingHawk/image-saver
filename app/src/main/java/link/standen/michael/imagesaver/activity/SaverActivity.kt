package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import link.standen.michael.imagesaver.R
import link.standen.michael.imagesaver.saver.SaverStrategy
import link.standen.michael.imagesaver.saver.SaverFactory
import link.standen.michael.imagesaver.util.IntentHelper

class SaverActivity : Activity() {

	companion object {
		const val TAG = "SaverActivity"
		const val DEFAULT_FILENAME = "image.png"
		const val REQUEST_CODE_FOLDER_SELECT = 2
	}

	private val saverFactory = SaverFactory()
	private var saver: SaverStrategy? = null
	private var imageLoaded = false
	private var saveClicked = false
	private var saverError = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.saver_activity)
		findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { fab -> saveClicked(fab as FloatingActionButton) }
		findViewById<FloatingActionButton>(R.id.fab2).setOnClickListener { selectLocation() }

		// Display link
		IntentHelper.getIntentText(intent)?.let { textExtra ->
			findViewById<TextView>(R.id.intent_link1).text = textExtra
			findViewById<TextView>(R.id.intent_link2).text = textExtra
		}

		// Create saver in background
		Thread {
			saver = saverFactory.createSaver(intent)
			if (saver == null) {
				Log.w(TAG, "Saver load failed")
				noSaver()
			} else {
				Log.d(TAG, "Saver load successful")
				val imageView = findViewById<ImageView>(R.id.image)
				if (saver?.loadImage(imageView, this) != true){
					// Image loading failed
					Log.w(TAG, "Image load failed")
					noSaver()
				} else {
					Log.d(TAG, "Image load successful")
					// Show the image
					runOnUiThread {
						findViewById<View>(R.id.image_placeholder).visibility = View.GONE
						imageView.visibility = View.VISIBLE
					}
					imageLoaded = true
					if (saveClicked) {
						runSaver()
					}
				}
			}
		}.start()
	}

	/**
	 * Save button clicked
	 */
	private fun saveClicked(fab: FloatingActionButton) {
		if (saverError){
			// Error, ignore
		}
		// Save the image then exit
		saveClicked = true
		fab.isEnabled = false
		fab.setImageResource(R.drawable.white_loading)
		if (imageLoaded) {
			runSaver(fab=fab)
		} else {
			Log.d(TAG, "Preparing to save")
		}
	}

	/**
	 * Run the saver
	 */
	private fun runSaver(fab: FloatingActionButton = findViewById(R.id.fab), folder: Uri? = null) {
		Log.d(TAG, "Running saver")
		Thread {
			val saveResult = if (folder != null) saver?.save(this, folder) else saver?.save(this)
			if (saveResult == true) {
				runOnUiThread {
					fab.setImageResource(R.drawable.white_ok)
				}
				finish()
				Log.d(TAG, "Save successful")
			} else {
				// Failed
				runOnUiThread {
					fab.setImageResource(R.drawable.white_error)
				}
				Log.e(TAG, "Unable to save")
			}
		}.start()
	}

	/**
	 * Remove saver, set error message
	 */
	private fun noSaver(){
		saverError = true
		saver = null
		runOnUiThread {
			findViewById<View>(R.id.image_placeholder).visibility = View.GONE
			findViewById<View>(R.id.no_saver).visibility = View.VISIBLE
		}
	}

	/**
	 * Select save folder location
	 */
	private fun selectLocation(){
		startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), REQUEST_CODE_FOLDER_SELECT)
	}

	/**
	 * Handle activity result
	 */
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == REQUEST_CODE_FOLDER_SELECT && resultCode == RESULT_OK) {
			data?.data?.let { uri ->
				// Save it into the selected folder
				Log.i(TAG, "Save location: $uri")
				// Get perms
				val perms = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
				contentResolver.takePersistableUriPermission(uri, perms)
				runSaver(folder=uri)
				contentResolver.releasePersistableUriPermission(uri, perms)
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data)
		}
	}

}
