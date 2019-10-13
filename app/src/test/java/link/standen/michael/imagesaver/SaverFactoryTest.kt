package link.standen.michael.imagesaver

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import link.standen.michael.imagesaver.loader.ImageUrlLoader
import link.standen.michael.imagesaver.util.LoaderFactory
import link.standen.michael.imagesaver.loader.UriLoader
import link.standen.michael.imagesaver.util.UrlHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SaverFactoryTest {

	companion object {
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
	fun createLoader_Null() {
		// Image without uri is null
		assertNull(toClass(LoaderFactory.createLoader(createIntent(mimeImagePng))))
		assertNull(toClass(LoaderFactory.createLoader(createIntent(mimeImageJpg))))
		assertNull(toClass(LoaderFactory.createLoader(createIntent(mimeImageSvg))))
		// Text without extra is null
		assertNull(toClass(LoaderFactory.createLoader(createIntent(mimeTextPlain))))
		// Non image link is null
		assertNull(toClass(LoaderFactory.createLoader(createTextIntent("https://michael.standen.link/image.html"))))
	}

	@Test
	fun createLoader_Uri() {
		// All image types with uri
		assertNotNull(mockUri)
		assertEquals(UriLoader::class, toClass(LoaderFactory.createLoader(createIntent(mimeImagePrefix, mockUri))))
		assertEquals(UriLoader::class, toClass(LoaderFactory.createLoader(createIntent(mimeImagePng, mockUri))))
		assertEquals(UriLoader::class, toClass(LoaderFactory.createLoader(createIntent(mimeImageJpg, mockUri))))
		assertEquals(UriLoader::class, toClass(LoaderFactory.createLoader(createIntent(mimeImageSvg, mockUri))))
	}

	@Test
	fun createLoader_ImageUrl() {
		// Urls ending with image extension use Image Url
		assertEquals(ImageUrlLoader::class, toClass(LoaderFactory.createLoader(createTextIntent("https://michael.standen.link/image.jpg"))))
		assertEquals(ImageUrlLoader::class, toClass(LoaderFactory.createLoader(createTextIntent("https://michael.standen.link/image.jpeg"))))
		assertEquals(ImageUrlLoader::class, toClass(LoaderFactory.createLoader(createTextIntent("https://michael.standen.link/image.png"))))
		// Case insensitive
		assertEquals(ImageUrlLoader::class, toClass(LoaderFactory.createLoader(createTextIntent("https://michael.standen.link/image.JPEG"))))
	}
}
