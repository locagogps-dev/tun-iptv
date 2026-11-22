package com.tuniptv.app.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists the user's favorites, the last playlist used and an optional
 * custom playlist URL in SharedPreferences.
 */
class FavoritesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** True when [channelId] is starred. */
    fun isFavorite(channelId: String): Boolean =
        prefs.getStringSet(KEY_FAVORITES, emptySet())?.contains(channelId) == true

    /**
     * Toggles the favorite state of [channelId].
     * @return the new favorite state (true = now a favorite).
     */
    fun toggleFavorite(channelId: String): Boolean {
        val current = prefs.getStringSet(KEY_FAVORITES, emptySet())?.toMutableSet()
            ?: mutableSetOf()
        val nowFavorite = if (current.contains(channelId)) {
            current.remove(channelId)
            false
        } else {
            current.add(channelId)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        return nowFavorite
    }

    fun favoriteIds(): Set<String> =
        prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()

    fun lastPlaylistUrl(): String = prefs.getString(KEY_LAST_PLAYLIST_URL, "") ?: ""

    fun setLastPlaylistUrl(url: String) {
        prefs.edit().putString(KEY_LAST_PLAYLIST_URL, url).apply()
    }

    fun customPlaylistUrl(): String = prefs.getString(KEY_CUSTOM_PLAYLIST_URL, "") ?: ""

    fun setCustomPlaylistUrl(url: String) {
        prefs.edit().putString(KEY_CUSTOM_PLAYLIST_URL, url).apply()
    }

    companion object {
        private const val PREFS_NAME = "tun_iptv_prefs"
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_LAST_PLAYLIST_URL = "last_playlist_url"
        private const val KEY_CUSTOM_PLAYLIST_URL = "custom_playlist_url"
    }
}
