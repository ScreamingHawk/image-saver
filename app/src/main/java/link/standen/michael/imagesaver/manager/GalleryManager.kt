package link.standen.michael.imagesaver.manager

import link.standen.michael.imagesaver.data.ImageItem

/**
 * Manages a group of images
 */
class GalleryManager(private val links: List<ImageItem>) {

	var currentIndex = 0

	/**
	 * Returns true if there is a next image
	 */
	fun hasNextImage() = (currentIndex + 1) < links.size

	/**
	 * Returns true if there is a previous image
	 */
	fun hasPreviousImage() = currentIndex > 0

	/**
	 * Returns the current image
	 */
	fun getCurrentImage(): ImageItem = links[currentIndex]

	/**
	 * Display the next image
	 */
	fun getNextImage(): ImageItem {
		if (hasNextImage()) {
			currentIndex++
		}
		return links[currentIndex]
	}

	/**
	 * Display the previous image
	 */
	fun getPreviousImage(): ImageItem {
		if (hasPreviousImage()) {
			currentIndex--
		}
		return links[currentIndex]
	}

	/**
	 * Returns true if there is only a single image
	 */
	fun isSingle() = links.size < 2

	/**
	 * Returns a string representing the position in the gallery
	 */
	fun positionString() =
		if (isSingle()) "" else "${currentIndex + 1} / ${links.size}"
}
