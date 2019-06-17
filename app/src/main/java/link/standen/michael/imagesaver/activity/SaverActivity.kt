package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import link.standen.michael.imagesaver.R
import link.standen.michael.imagesaver.saver.Saver
import link.standen.michael.imagesaver.saver.SaverFactory
import link.standen.michael.imagesaver.util.IntentHelper

class SaverActivity : Activity() {

	companion object {
		const val TAG = "SaverActivity"
		const val DEFAULT_FILENAME = "image.png"
	}

	private val saverFactory = SaverFactory()
	private var saver: Saver? = null

	private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
		when (item.itemId) {
			R.id.navigation_save -> {
				// Save the image then exit
				item.title = getString(R.string.nav_save_saving)
				Thread {
					if (saver?.save() == true){
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
		findViewById<BottomNavigationView>(R.id.nav_view).setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

		// Create saver in background
		Thread {
			saver = saverFactory.createSaver(this, intent)
			if (saver == null) {
				runOnUiThread {
					findViewById<View>(R.id.image_placeholder).visibility = View.GONE
					findViewById<View>(R.id.no_saver).visibility = View.VISIBLE
					IntentHelper.getIntentText(intent)?.let { textExtra ->
						findViewById<TextView>(R.id.no_saver_link).text = textExtra
					}
				}
			} else {
				val imageView = findViewById<ImageView>(R.id.image)
				saver?.loadImage(imageView, this)
				// Show the image
				runOnUiThread {
					findViewById<View>(R.id.image_placeholder).visibility = View.GONE
					imageView.visibility = View.VISIBLE
				}
			}
		}.start()
	}

}
