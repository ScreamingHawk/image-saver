package link.standen.michael.imagesaver.manager

import android.app.Activity
import android.util.Log
import android.widget.ProgressBar
import link.standen.michael.imagesaver.R

/**
 * Manages the status of the progress bar.
 */
class ProgressManager(private val activity: Activity, private val total: Int) {

	companion object {
		private const val TAG = "ProgressManager"
	}

	private val bar = activity.findViewById<ProgressBar>(R.id.progress_bar)
	private val hasTotal = total > 0

	init {
		if (hasTotal) {
			Log.d(TAG, "Starting progress with total: $total")
			activity.runOnUiThread {
				bar.isIndeterminate = false
				bar.max = total
			}
		}
	}

	/**
	 * Update the progress bar.
	 */
	fun updateProgress(progress: Int){
		if (hasTotal) {
			activity.runOnUiThread {
				if (progress >= total) {
					bar.progress = total
				} else {
					bar.progress = progress
				}
			}
		}
	}

	/**
	 * Progress is at 100%
	 */
	fun completed(){
		Log.d(TAG, "Progress completed")
		activity.runOnUiThread {
			bar.max = 1
			bar.progress = 1
			bar.isIndeterminate = false
		}
	}
}
