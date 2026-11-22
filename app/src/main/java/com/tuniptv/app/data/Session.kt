package com.tuniptv.app.data

import com.tuniptv.app.model.Channel

/**
 * Holds the currently loaded playlist in memory so the player activities
 * can navigate to the next / previous channel without re-downloading.
 * The list is replaced every time a playlist is (re)loaded.
 */
object Session {

    @Volatile
    var channels: List<Channel> = emptyList()

    @Volatile
    var currentIndex: Int = 0

    val currentChannel: Channel?
        get() = channels.getOrNull(currentIndex)
}
