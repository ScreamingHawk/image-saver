package link.standen.michael.imagesaver.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import link.standen.michael.imagesaver.R
import link.standen.michael.imagesaver.util.PreferenceHelper
import link.standen.michael.imagesaver.util.StorageHelper

class SettingsActivity : AppCompatActivity() {

	companion object {
		private const val TAG = "SettingsActivity"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.settings_activity)
		supportFragmentManager
			.beginTransaction()
			.replace(R.id.settings, SettingsFragment())
			.commit()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	/**
	 * Handle back button from toolbar
	 */
	override fun onSupportNavigateUp(): Boolean {
		finish()
		return true
	}

	class SettingsFragment : PreferenceFragmentCompat() {
		override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
			setPreferencesFromResource(R.xml.preferences, rootKey)

			if (context != null) {
				// Bind nomedia
				findPreference<CheckBoxPreference>(PreferenceHelper.NO_MEDIA_KEY)
					?.setOnPreferenceChangeListener { _, newValue ->
						Log.i(TAG, "Setting nomedia to $newValue")
						StorageHelper.updateNoMedia(context!!, newValue == true)
						true
					}
					?: Log.e(TAG, "Unable to bind nomedia")
			}
		}
	}
}
