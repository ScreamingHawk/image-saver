package link.standen.michael.imagesaver.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import de.cketti.library.changelog.ChangeLog
import link.standen.michael.imagesaver.R

/**
 * The entry point activity.
 * This activity shows the list of user selected folders.
 */
class MainActivity : Activity() {

	companion object {
		const val TAG = "MainActivity"
		const val PERMISSION_REQUEST_CODE = 2

		const val CHANGE_LOG_CSS = """
				body { padding: 0.8em; }
				h1 { margin-left: 0px; font-size: 1.2em; }
				ul { padding-left: 1.2em; }
				li { margin-left: 0px; }
			"""
	}

	override fun onCreate(savedInstanceState: android.os.Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)
		setActionBar(findViewById(R.id.toolbar))

		// Check for permissions
		testPermissions()

		// Show the change log
		showChangeLog(false)
	}

	/**
	 * Show the change log.
	 * Shows the full change log when nothing is in "What's New" log. Shows "What's New" log otherwise.
	 * @param force Force the change log to be displayed, if false only displayed if new content.
	 */
	private fun showChangeLog(force: Boolean) {
		val cl = ChangeLog(this, CHANGE_LOG_CSS)
		if (force || cl.isFirstRun) {
			if (cl.getChangeLog(false).size == 0){
				// Force the display of the full dialog list.
				cl.fullLogDialog.show()
			} else {
				// Show only the new stuff.
				cl.logDialog.show()
			}
		}
	}

	/**
	 * Test permissions are granted.
	 */
	private fun testPermissions(){
		val permissions = listOf(
			// All available permissions
			android.Manifest.permission.READ_EXTERNAL_STORAGE,
			android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
			android.Manifest.permission.INTERNET
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
			R.id.action_settings -> {
				startActivity(Intent(this, SettingsActivity::class.java))
				true
			}
			R.id.action_credits -> {
				startActivity(Intent(this, CreditsActivity::class.java))
				true
			}
			R.id.action_change_log -> {
				showChangeLog(true)
				true
			}
			else ->
				super.onOptionsItemSelected(item)
		}
}
