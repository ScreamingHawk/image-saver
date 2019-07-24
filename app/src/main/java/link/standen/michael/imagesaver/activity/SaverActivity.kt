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
import link.standen.michael.imagesaver.data.ImageItem
import link.standen.michael.imagesaver.manager.GalleryManager
import link.standen.michael.imagesaver.util.ImageHelper
import link.standen.michael.imagesaver.util.LoaderFactory
import link.standen.michael.imagesaver.util.IntentHelper
import link.standen.michael.imagesaver.util.StorageHelper.STORAGE_PERMISSIONS

class SaverActivity : Activity() {

	companion object {
		const val TAG = "SaverActivity"
		const val REQUEST_CODE_FOLDER_SELECT = 2
	}

	private lateinit var gallery: GalleryManager
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
			val images = LoaderFactory.createLoader(intent)?.listImages(this) ?: listOf()
			if (images.isEmpty()) {
				Log.w(TAG, "Loader load failed")
				noSaver()
			} else {
				Log.d(TAG, "Loader load successful")
				// Initialise gallery
				initialiseGallery(images)
				val imageView = findViewById<ImageView>(R.id.image)
				if (!loadImage(gallery.getCurrentImage(), imageView)){
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
	private fun initialiseGallery(images: List<ImageItem>){
		gallery = GalleryManager(images)
		if (gallery.isSingle()){
			// No need to do all the set up
			return
		}
		// Set up buttons
		val updateVisibility = fun(){
			this@SaverActivity.runOnUiThread {
				findViewById<ImageButton>(R.id.previous).visibility =
					if (gallery.hasPreviousImage()) View.VISIBLE else View.GONE
				findViewById<ImageButton>(R.id.next).visibility =
					if (gallery.hasNextImage()) View.VISIBLE else View.GONE
				// Reset fab icon
				findViewById<FloatingActionButton>(R.id.fab).setImageResource(R.drawable.white_save)
				saveClicked = false
			}
		}
		findViewById<ImageButton>(R.id.previous).setOnClickListener {
			// Show the previous image
			Thread {
				loadImage(gallery.getPreviousImage())
				updateVisibility()
			}.start()
		}
		findViewById<ImageButton>(R.id.next).setOnClickListener {
			// Show the next image
			Thread {
				loadImage(gallery.getNextImage())
				updateVisibility()
			}.start()
		}
		updateVisibility()
		Log.d(TAG, "Loaded gallery")
	}

	/**
	 * Loads the image and updates gallery view items
	 */
	private fun loadImage(item: ImageItem, imageView: ImageView = findViewById(R.id.image)): Boolean {
		runOnUiThread {
			findViewById<TextView>(R.id.top_bar)?.text = item.fname
			findViewById<TextView>(R.id.footnote)?.text = gallery.positionString()
		}
		return ImageHelper.loadImage(item, imageView, this@SaverActivity)
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
				saveResult = ImageHelper.saveImage(gallery.getCurrentImage(), this, folder)
			} catch (e: Exception){
				// Catch everything
				Log.e(TAG, "Saving failed")
				if (e.message != null) {
					Log.e(TAG, e.message)
					Log.e(TAG, Log.getStackTraceString(e))
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
				if (gallery.isSingle()) {
					// Exit on save success if only a single image
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
	 * Set error message
	 */
	private fun noSaver(){
		saverError = true
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
