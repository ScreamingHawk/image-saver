package link.standen.michael.imagesaver.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import link.standen.michael.imagesaver.R

class SaverActivity : AppCompatActivity() {

	private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
		when (item.itemId) {
			R.id.navigation_save -> {
				//TODO Save the image
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

		if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
			handleSendImage(intent) // Handle single image being sent
		} else {
			//TODO Handle not image
		}
	}

	/**
	 * Handles images received from share
	 */
	private fun handleSendImage(intent: Intent) {
		(intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
			findViewById<ImageView>(R.id.image).setImageURI(it)
		}
	}

}
