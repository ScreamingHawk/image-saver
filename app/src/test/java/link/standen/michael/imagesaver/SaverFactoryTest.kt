package link.standen.michael.imagesaver

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import com.nhaarman.mockito_kotlin.isA
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.notNull
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import link.standen.michael.imagesaver.saver.ImageUrlSaver
import link.standen.michael.imagesaver.saver.SaverFactory
import link.standen.michael.imagesaver.saver.UriSaver
import link.standen.michael.imagesaver.util.UrlHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SaverFactoryTest {

	companion object {
		private val context = Activity()
		private var mockUri: Uri = mock()

		private const val mimeImagePrefix = "image/"
		private const val mimeImagePng = "image/png"
		private const val mimeImageJpg = "image/jpeg"
		private const val mimeImageSvg = "image/svg+xml"

		private const val mimeTextPlain = "text/plain"
	}

	@Before
	fun createMocks(){
		// Mock log
		mockkStatic(Log::class)
		every { Log.v(any(), any()) } returns 0
		every { Log.d(any(), any()) } returns 0
		every { Log.i(any(), any()) } returns 0
		every { Log.e(any(), any()) } returns 0
		every { Log.e(any(), any(), any()) } returns 0
		// Every non null URL is valid
		mockkStatic(URLUtil::class)
		every { URLUtil.isValidUrl(ofType(String::class)) } returns true
		// No redirects
		mockkObject(UrlHelper::class)
		//FIXME every { UrlHelper.resolveRedirects(ofType(String::class)) } returnsArgument 0
	}

	/**
	 * Create a URL intent.
	 */
	private fun createTextIntent(textExtra: String) = createIntent(mimeTextPlain, null, textExtra)

	/**
	 * Create an intent with the given values.
	 */
	private fun createIntent(type: String, uri: Uri? = null, textExtra: String? = null): Intent{
		val intent: Intent = mock()
		Mockito.`when`(intent.action).thenReturn(Intent.ACTION_SEND)
		Mockito.`when`(intent.type).thenReturn(type)
		Mockito.`when`(intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)).thenReturn(uri)
		Mockito.`when`(intent.getStringExtra(Intent.EXTRA_TEXT)).thenReturn(textExtra)
		return intent
	}

	/**
	 * Returns the kotlin class of an object
	 */
	private fun toClass(c: Any?) = c?.javaClass?.kotlin

	@Test
	fun createSaver_Null() {
		// Image without uri is null
		assertNull(toClass(SaverFactory().createSaver(context, createIntent(mimeImagePng))))
		assertNull(toClass(SaverFactory().createSaver(context, createIntent(mimeImageJpg))))
		assertNull(toClass(SaverFactory().createSaver(context, createIntent(mimeImageSvg))))
		// Text without extra is null
		assertNull(toClass(SaverFactory().createSaver(context, createIntent(mimeTextPlain))))
		// Non image link is null
		assertNull(toClass(SaverFactory().createSaver(context, createTextIntent("https://michael.standen.link/image.html"))))
	}

	@Test
	fun createSaver_Uri() {
		// All image types with uri
		assertNotNull(mockUri)
		assertEquals(UriSaver::class, toClass(SaverFactory().createSaver(context, createIntent(mimeImagePrefix, mockUri))))
		assertEquals(UriSaver::class, toClass(SaverFactory().createSaver(context, createIntent(mimeImagePng, mockUri))))
		assertEquals(UriSaver::class, toClass(SaverFactory().createSaver(context, createIntent(mimeImageJpg, mockUri))))
		assertEquals(UriSaver::class, toClass(SaverFactory().createSaver(context, createIntent(mimeImageSvg, mockUri))))
	}

	@Test
	fun createSaver_ImageUrl() {
		// Urls ending with image extension use Image Url
		assertEquals(ImageUrlSaver::class, toClass(SaverFactory().createSaver(context, createTextIntent("https://michael.standen.link/image.jpg"))))
		assertEquals(ImageUrlSaver::class, toClass(SaverFactory().createSaver(context, createTextIntent("https://michael.standen.link/image.jpeg"))))
		assertEquals(ImageUrlSaver::class, toClass(SaverFactory().createSaver(context, createTextIntent("https://michael.standen.link/image.png"))))
	}
}
