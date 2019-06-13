package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.widget.ImageView
import link.standen.michael.imagesaver.R
import link.standen.michael.imagesaver.saver.Saver
import link.standen.michael.imagesaver.saver.SaverFactory

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
				if (saver?.save() == true){
					item.title = getString(R.string.nav_save_success)
					finish()
					Log.d(TAG, "Save successful")
				} else {
					// Failed. Disable button
					item.title = getString(R.string.nav_save_failed)
					item.isEnabled = false
					Log.e(TAG, "Unable to save")
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
		findViewById<BottomNavigationView>(R.id.nav_view).setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

		updateImage(intent)

		// Create saver in background
		Thread {
			saver = saverFactory.createSaver(this, intent)
		}.start()
	}

	/**
	 * Handles images received from share
	 */
	private fun updateImage(intent: Intent) {
		if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
			(intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
				findViewById<ImageView>(R.id.image).setImageURI(it)
			}
		} else {
			//TODO Handle not image
		}
	}

}
