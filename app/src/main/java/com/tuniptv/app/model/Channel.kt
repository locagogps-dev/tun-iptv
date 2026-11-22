package com.tuniptv.app.model

/**
 * One TV channel parsed from an M3U playlist.
 *
 * @param id        stable id: tvg-id when present, otherwise "ch_<number>"
 * @param name      display name (tvg-name, or the text after the comma in #EXTINF)
 * @param logoUrl   tvg-logo URL, may be null
 * @param group     group-title, defaults to "Other"
 * @param streamUrl the stream URL on the line following #EXTINF
 * @param tvgId     raw tvg-id value, may be null
 */
data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val group: String,
    val streamUrl: String,
    val tvgId: String?
)
