package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.net.Uri
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
	 * Saves the file to the supplied folder
	 */
	fun save(context: Context, folder: Uri? = null): Boolean
}
