package link.standen.michael.imagesaver.loader

import android.content.Context
import link.standen.michael.imagesaver.data.ImageItem

/**
 * Interface for loading images
 */
interface LoaderStrategy {

	/**
	 * Load the image into the ImageView
 	 */
	fun listImages(context: Context): List<ImageItem>

}
