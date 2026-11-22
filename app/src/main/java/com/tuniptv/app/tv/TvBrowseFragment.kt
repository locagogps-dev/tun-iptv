package com.tuniptv.app.tv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.lifecycleScope
import com.tuniptv.app.R
import com.tuniptv.app.data.DEFAULT_PLAYLISTS
import com.tuniptv.app.data.PlaylistRepository
import com.tuniptv.app.data.Session
import com.tuniptv.app.model.Channel
import kotlinx.coroutines.launch

/**
 * Leanback home screen: one row per category (group-title) with
 * channel cards (logo + name). D-pad navigable. Clicking a card
 * opens [TvPlayerActivity].
 */
class TvBrowseFragment : BrowseSupportFragment() {

    private val repository = PlaylistRepository()
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        badgeDrawable = requireContext().getDrawable(R.drawable.banner)
        adapter = rowsAdapter

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Channel) openPlayer(item)
        }

        loadPlaylists()
    }

    private fun loadPlaylists() {
        progressBarManager.show()
        lifecycleScope.launch {
            val all = mutableListOf<Channel>()
            for (source in DEFAULT_PLAYLISTS) {
                try {
                    all += repository.load(source.url)
                } catch (e: Exception) {
                    // One playlist failing must not block the other one.
                }
            }
            if (all.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.playlist_load_error,
                    Toast.LENGTH_LONG
                ).show()
            }
            buildRows(all)
        }
    }

    private fun buildRows(channels: List<Channel>) {
        progressBarManager.hide()
        Session.channels = channels
        rowsAdapter.clear()
        val groups = channels.groupBy { it.group }.toSortedMap()
        var headerId = 0L
        for ((group, groupChannels) in groups) {
            val cardAdapter = ArrayObjectAdapter(TvCardPresenter())
            cardAdapter.addAll(0, groupChannels)
            rowsAdapter.add(ListRow(HeaderItem(headerId++, group), cardAdapter))
        }
    }

    private fun openPlayer(channel: Channel) {
        Session.currentIndex = Session.channels.indexOf(channel).coerceAtLeast(0)
        startActivity(Intent(requireContext(), TvPlayerActivity::class.java))
    }
}
