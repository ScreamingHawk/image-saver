package link.standen.michael.imagesaver.util

import android.content.Context
import androidx.preference.PreferenceManager
import link.standen.michael.imagesaver.R

object PreferenceHelper {

	private const val FOLDER_NAME_KEY = "folder_name"
	const val NO_MEDIA_KEY = "no_media"
	private const val COMPLETED_TOAST_KEY = "completed_toast"

	/**
	 * Gets a string from the preference manager
	 */
	private fun getString(context: Context, key: String, defaultId: Int): String =
		PreferenceManager(context).sharedPreferences.getString(key, null) ?: context.getString(defaultId)

	/**
	 * Gets a boolean from the preference manager
	 */
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

}
