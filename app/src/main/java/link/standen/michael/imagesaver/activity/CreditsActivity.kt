package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView

import link.standen.michael.imagesaver.R
import java.util.Locale
import android.util.Log
import android.view.View

/**
 * Credits activity.
 */
class CreditsActivity : Activity() {

	private val defaultLocale = Locale("en").language

	companion object {
		private const val TAG = "CreditsActivity"
	}

	@Suppress("SpellCheckingInspection")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.credits_activity)

		// Version
		try {
			val versionName = packageManager.getPackageInfo(packageName, 0).versionName
			findViewById<TextView>(R.id.credits_version).text = resources.getString(R.string.credits_version, versionName)
		} catch (e: PackageManager.NameNotFoundException) {
			Log.e(TAG, "Unable to get package version", e)
		}

		// Linkify
		findViewById<TextView>(R.id.credits_creator).movementMethod = LinkMovementMethod.getInstance()
		findViewById<TextView>(R.id.credits_content1).movementMethod = LinkMovementMethod.getInstance()
		findViewById<TextView>(R.id.credits_content2).movementMethod = LinkMovementMethod.getInstance()
		findViewById<TextView>(R.id.credits_content3).movementMethod = LinkMovementMethod.getInstance()
		if (getCurrentLocale().language == defaultLocale){
			// English, hide the translator info
			findViewById<TextView>(R.id.credits_content_translator).visibility = View.GONE
		} else {
			findViewById<TextView>(R.id.credits_content_translator).movementMethod = LinkMovementMethod.getInstance()
		}
	}

	/**
	 * A version safe way to get the currently applied locale.
	 */
	private fun getCurrentLocale(): Locale =
		this.resources.configuration.locales.get(0)
}
