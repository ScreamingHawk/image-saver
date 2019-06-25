package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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

	private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
		when (item.itemId) {
			R.id.navigation_save -> {
				if (saverError){
					// Error, ignore
					return@OnNavigationItemSelectedListener true
				}
				// Save the image then exit
				saveClicked = true
				item.title = getString(R.string.nav_save_saving)
				if (imageLoaded) {
					runSaver(item)
				} else {
					Log.d(TAG, "Preparing to save")
				}
				return@OnNavigationItemSelectedListener true
			}
			R.id.navigation_close -> {
				// Exit
				finish()
				return@OnNavigationItemSelectedListener true
			}
		}
		false
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.saver_activity)
		setActionBar(findViewById(R.id.toolbar))
		findViewById<BottomNavigationView>(R.id.nav_view).setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

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
	 * Run the saver
	 */
	private fun runSaver(saveItem: MenuItem? = null) {
		Log.d(TAG, "Running saver")
		val item = saveItem ?: findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_save)
		Thread {
			if (saver?.save() == true) {
				runOnUiThread {
					item.title = getString(R.string.nav_save_success)
				}
				finish()
				Log.d(TAG, "Save successful")
			} else {
				// Failed. Disable button
				runOnUiThread {
					item.title = getString(R.string.nav_save_failed)
					item.isEnabled = false
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
