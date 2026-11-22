package com.tuniptv.app.data

import com.tuniptv.app.model.Channel

/**
 * Parses M3U / M3U8 playlists.
 *
 * Supported:
 * - #EXTM3U header (ignored, may carry url-tvg / x-tvg-url attributes)
 * - #EXTINF lines with attributes tvg-id, tvg-name, tvg-logo, group-title
 *   and the display name after the last comma, followed on the next
 *   non-empty, non-comment line by the stream URL.
 * - Any other # directive is ignored. A URL without a preceding #EXTINF
 *   is ignored as well.
 */
object M3uParser {

    private val ATTR_REGEX = Regex("""([a-zA-Z0-9_-]+)="([^"]*)"""")

    fun parse(text: String): List<Channel> {
        val lines = text.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toList()

        val channels = mutableListOf<Channel>()
        var index = 0
        var pending: PendingInfo? = null

        for (line in lines) {
            when {
                line.startsWith("#EXTM3U") -> {
                    // Playlist header, nothing to do.
                }
                line.startsWith("#EXTINF") -> {
                    pending = parseExtinf(line, index)
                }
                line.startsWith("#") -> {
                    // Any other directive: ignore.
                }
                else -> {
                    val info = pending
                    if (info != null) {
                        channels.add(info.toChannel(line))
                        index++
                        pending = null
                    }
                }
            }
        }
        return channels
    }

    private fun parseExtinf(line: String, number: Int): PendingInfo {
        val attrs = ATTR_REGEX.findAll(line)
            .associate { it.groupValues[1] to it.groupValues[2] }
        val displayName = line.substringAfterLast(",", "").trim()
        return PendingInfo(
            number = number,
            name = attrs["tvg-name"]?.takeIf { it.isNotBlank() } ?: displayName,
            logoUrl = attrs["tvg-logo"],
            group = attrs["group-title"] ?: "Other",
            tvgId = attrs["tvg-id"]
        )
    }

    private data class PendingInfo(
        val number: Int,
        val name: String,
        val logoUrl: String?,
        val group: String,
        val tvgId: String?
    ) {
        fun toChannel(url: String) = Channel(
            id = tvgId?.takeIf { it.isNotBlank() } ?: "ch_$number",
            name = name.ifBlank { "Channel $number" },
            logoUrl = logoUrl?.takeIf { it.isNotBlank() },
            group = group.ifBlank { "Other" },
            streamUrl = url,
            tvgId = tvgId
        )
    }
}
