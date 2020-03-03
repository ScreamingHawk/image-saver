package link.standen.michael.imagesaver.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager
import link.standen.michael.imagesaver.R

object PreferenceHelper {

	private const val FOLDER_NAME_KEY = "folder_name"
	const val NO_MEDIA_KEY = "no_media"
	private const val COMPLETED_TOAST_KEY = "completed_toast"
	private const val RANDOM_NAME_KEY = "random_filename"

	/**
	 * Gets a string from the preference manager
	 */
	@SuppressLint("RestrictedApi")
	private fun getString(context: Context, key: String, defaultId: Int): String =
		PreferenceManager(context).sharedPreferences.getString(key, null) ?: context.getString(defaultId)

	/**
	 * Gets a boolean from the preference manager
	 */
	@SuppressLint("RestrictedApi")
	private fun getBoolean(context: Context, key: String, default: Boolean) =
		PreferenceManager(context).sharedPreferences.getBoolean(key, default)

	/**
	 * Returns the folder name from the settings
	 */
	fun getFolderName(context: Context) = getString(context, FOLDER_NAME_KEY, R.string.setting_folder_name_default)

	/**
	 * Returns the no media file setting
	 */
	fun getNoMedia(context: Context) = getBoolean(context, NO_MEDIA_KEY, false)

	/**
	 * Returns the toast on save completed setting
	 */
	fun getCompletedToast(context: Context) = getBoolean(context, COMPLETED_TOAST_KEY, false)

	/**
	 * Returns the random filename setting
	 */
	fun isRandomFilename(context: Context) = getBoolean(context, RANDOM_NAME_KEY, false)

}
