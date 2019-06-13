package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
		setActionBar(findViewById(R.id.toolbar))

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

	/**
	 * Create menu.
	 */
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.main_menu, menu)
		return true
	}

	/**
	 * Handle menu items.
	 */
	override fun onOptionsItemSelected(item: MenuItem): Boolean =
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		when (item.itemId) {
			R.id.action_credits -> {
				startActivity(Intent(this, CreditsActivity::class.java))
				true
			}
			else ->
				super.onOptionsItemSelected(item)
		}
}
