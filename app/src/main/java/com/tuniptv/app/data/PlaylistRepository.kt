package com.tuniptv.app.data

import com.tuniptv.app.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

/** One playlist the user can pick. */
data class PlaylistSource(val label: String, val url: String)

/** The two default playlists shipped with the app. */
val DEFAULT_PLAYLISTS = listOf(
    PlaylistSource(
        label = "Sports",
        url = "https://raw.githubusercontent.com/locagogps-dev/iptv-playlists/main/popular-sports-iptv.m3u"
    ),
    PlaylistSource(
        label = "Playlist",
        url = "https://raw.githubusercontent.com/locagogps-dev/iptv-playlists/main/playlist.m3u"
    )
)

/**
 * Downloads an M3U playlist over HTTP with OkHttp and parses it.
 * The last successfully loaded list is kept in [channels] as an
 * in-memory cache (also mirrored into [Session] by the UI layer).
 */
class PlaylistRepository(
    private val client: OkHttpClient = defaultClient()
) {

    @Volatile
    var channels: List<Channel> = emptyList()
        private set

    /**
     * Downloads and parses the playlist at [url].
     * @throws IOException on network errors, non-2xx responses or an empty body.
     */
    suspend fun load(url: String): List<Channel> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "TUN-IPTV/1.0")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code}")
            }
            val body = response.body?.string() ?: throw IOException("Empty playlist")
            M3uParser.parse(body).also { channels = it }
        }
    }

    companion object {
        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
