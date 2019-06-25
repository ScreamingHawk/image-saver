package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.widget.ImageView

/**
 * An abstract class that calls on an imageUrlSaver
 */
abstract class EnhancedImageUrlSaver(): SaverStrategy {

	protected var imageUrlSaver: ImageUrlSaver? = null

	/**
	 * Use the imageUrlSaver to load the image
	 */
	override fun loadImage(view: ImageView, activity: Activity): Boolean = imageUrlSaver?.loadImage(view, activity) ?: false

	/**
	 * Use the imageUrlSaver to load the image
	 */
	override fun save(): Boolean = imageUrlSaver?.save() ?: false

}
