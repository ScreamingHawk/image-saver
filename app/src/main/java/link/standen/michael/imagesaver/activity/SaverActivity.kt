package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.saver_activity.view.*
import link.standen.michael.imagesaver.R
import link.standen.michael.imagesaver.saver.SaverStrategy
import link.standen.michael.imagesaver.saver.SaverFactory
import link.standen.michael.imagesaver.util.IntentHelper

class SaverActivity : Activity() {

	companion object {
		const val TAG = "SaverActivity"
		const val DEFAULT_FILENAME = "image.png"
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

		// Display link
		IntentHelper.getIntentText(intent)?.let { textExtra ->
			findViewById<TextView>(R.id.intent_link1).text = textExtra
			findViewById<TextView>(R.id.intent_link2).text = textExtra
		}

		// Create saver in background
		Thread {
			saver = saverFactory.createSaver(this, intent)
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
			runSaver(fab)
		} else {
			Log.d(TAG, "Preparing to save")
		}
	}

	/**
	 * Run the saver
	 */
	private fun runSaver(fab: FloatingActionButton = findViewById(R.id.fab)) {
		Log.d(TAG, "Running saver")
		Thread {
			if (saver?.save() == true) {
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

}
