package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import link.standen.michael.imagesaver.R

/**
 * The entry point activity.
 * This activity shows the list of user selected folders.
 */
class MainActivity : Activity() {

	companion object {
		const val TAG = "MainActivity"
		const val PERMISSION_REQUEST_CODE = 2
	}

	override fun onCreate(savedInstanceState: android.os.Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)

		// Check for permissions
		testPermissions()
	}

	/**
	 * Test permissions are granted.
	 */
	private fun testPermissions(){
		val permissions = listOf(
			// All available permissions
			android.Manifest.permission.READ_EXTERNAL_STORAGE,
			android.Manifest.permission.WRITE_EXTERNAL_STORAGE
			// Filter out granted permissions
		).filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }.toTypedArray()
		if (permissions.isNotEmpty()){
			Log.i(TAG, "Requesting permission for " + permissions.reduce { total, next -> "$total, $next" })
			// Request permissions
			requestPermissions(permissions, PERMISSION_REQUEST_CODE)
		}
	}
}
