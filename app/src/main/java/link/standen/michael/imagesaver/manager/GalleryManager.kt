package link.standen.michael.imagesaver.manager

import android.app.Activity
import android.widget.ImageView

/**
 * Manages a group of images
 */
interface GalleryManager {

	/**
	 * Returns true if there is a next image
	 */
	fun hasNextImage(): Boolean

	/**
	 * Returns true if there is a previous image
	 */
	fun hasPreviousImage(): Boolean

	/**
	 * Display the next image
	 */
	fun showNextImage(view: ImageView, activity: Activity)

	/**
	 * Display the previous image
	 */
	fun showPreviousImage(view: ImageView, activity: Activity)
}
