package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.widget.ImageView

/**
 * Interface for saver classes
 */
interface SaverStrategy {

	/**
	 * Load the image into the ImageView
	 * @param activity Includes Activity for runOnUiThread
 	 */
	fun loadImage(view: ImageView, activity: Activity): Boolean

	/**
	 * Saves the file
	 */
	fun save(): Boolean
}
