package com.tuniptv.app.tv

import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.tuniptv.app.data.Session

/**
 * Leanback video fragment: ExoPlayer wrapped in [ExoPlayerAdapter] and
 * driven by a PlaybackTransportControlGlue, so the TV remote (D-pad)
 * gets the standard Leanback transport controls
 * (play / pause / seek / fast-forward / rewind).
 */
class TvVideoFragment : VideoSupportFragment() {

    private var player: ExoPlayer? = null
    private var adapter: ExoPlayerAdapter? = null
    private var glue: PlaybackTransportControlGlue<ExoPlayerAdapter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channel = Session.currentChannel ?: run {
            requireActivity().finish()
            return
        }

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("TUN-IPTV/1.0")
            .setAllowCrossProtocolRedirects(true)
        val mediaSourceFactory = DefaultMediaSourceFactory(requireContext())
            .setDataSourceFactory(dataSourceFactory)

        val exoPlayer = ExoPlayer.Builder(requireContext())
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        player = exoPlayer

        val playerAdapter = ExoPlayerAdapter(exoPlayer)
        adapter = playerAdapter
        val transportGlue = PlaybackTransportControlGlue(requireContext(), playerAdapter)
        transportGlue.host = VideoSupportFragmentGlueHost(this)
        transportGlue.title = channel.name
        transportGlue.subtitle = channel.group
        transportGlue.isSeekEnabled = true
        glue = transportGlue

        exoPlayer.setMediaItem(MediaItem.fromUri(channel.streamUrl))
        exoPlayer.prepare()
        transportGlue.playWhenPrepared()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        adapter?.release()
        adapter = null
        glue = null
        player?.release()
        player = null
        super.onDestroy()
    }
}
