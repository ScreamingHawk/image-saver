package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import link.standen.michael.imagesaver.R
import link.standen.michael.imagesaver.manager.GalleryManager
import link.standen.michael.imagesaver.saver.SaverStrategy
import link.standen.michael.imagesaver.util.SaverFactory
import link.standen.michael.imagesaver.util.IntentHelper
import link.standen.michael.imagesaver.util.StorageHelper.STORAGE_PERMISSIONS

class SaverActivity : Activity() {

	companion object {
		const val TAG = "SaverActivity"
		const val DEFAULT_FILENAME = "image.png"
		const val REQUEST_CODE_FOLDER_SELECT = 2
	}

	private var saver: SaverStrategy? = null
	private var gallery: GalleryManager? = null
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
			saver = SaverFactory.createSaver(intent)
			if (saver == null) {
				Log.w(TAG, "Saver load failed")
				noSaver()
			} else {
				Log.d(TAG, "Saver load successful")
				// Initialise gallery
				initialiseGallery()
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
	 * Initialise the gallery buttons
	 */
	private fun initialiseGallery(){
		gallery = saver?.getGalleryManager()
		if (gallery != null){
			// Set up buttons
			val updateVisibility = fun(){
				this@SaverActivity.runOnUiThread {
					findViewById<ImageButton>(R.id.previous).visibility =
						if (gallery?.hasPreviousImage() == true) View.VISIBLE else View.GONE
					findViewById<ImageButton>(R.id.next).visibility =
						if (gallery?.hasNextImage() == true) View.VISIBLE else View.GONE
					// Reset fab icon
					findViewById<FloatingActionButton>(R.id.fab).setImageResource(R.drawable.white_save)
				}
			}
			findViewById<ImageButton>(R.id.previous).setOnClickListener {
				// Show the previous image
				Thread {
					gallery?.showPreviousImage(findViewById(R.id.image), this@SaverActivity)
					updateVisibility()
				}.start()
			}
			findViewById<ImageButton>(R.id.next).setOnClickListener {
				// Show the next image
				Thread {
					gallery?.showNextImage(findViewById(R.id.image), this@SaverActivity)
					updateVisibility()
				}.start()
			}
			updateVisibility()
			Log.d(TAG, "Loaded gallery")
		} else {
			Log.d(TAG, "No gallery to load")
		}
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
			var saveResult = false
			try {
				saveResult = (if (folder != null) saver?.save(this, folder) else saver?.save(this)) ?: false
			} catch (e: Exception){
				// Catch everything
				Log.e(TAG, "Saving failed")
				if (e.message != null) {
					Log.e(TAG, e.message)
					runOnUiThread {
						fab.setImageResource(R.drawable.white_error)
					}
					showError(e.message as String, fab)
					return@Thread
				}
			}
			if (saveResult) {
				Log.d(TAG, "Save successful")
				runOnUiThread {
					fab.setImageResource(R.drawable.white_ok)
				}
				if (gallery == null) {
					// Exit on save success if not in a gallery
					finish()
				}
			} else {
				// Failed
				Log.e(TAG, "Unable to save")
				showError(fab=fab)
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
	 * Surface the error message
	 */
	private fun showError(message: String? = null, fab: FloatingActionButton? = null){
		runOnUiThread {
			// Show sorry error
			findViewById<TextView>(R.id.sorry).text = getString(R.string.sorry_error)
			findViewById<TextView>(R.id.saver_error).text = getString(R.string.sorry_error_subtext)
			if (message != null) {
				// Show error message
				findViewById<TextView>(R.id.saver_error_message).text = message
			}
			// Block fab
			fab?.setImageResource(R.drawable.white_error)
			// Update visibility
			findViewById<View>(R.id.image).visibility = View.GONE
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
				contentResolver.takePersistableUriPermission(uri, STORAGE_PERMISSIONS)
				runSaver(folder=uri)
				contentResolver.releasePersistableUriPermission(uri, STORAGE_PERMISSIONS)
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data)
		}
	}

}
