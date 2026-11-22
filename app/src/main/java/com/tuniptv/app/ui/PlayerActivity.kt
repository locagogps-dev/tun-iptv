package com.tuniptv.app.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.tuniptv.app.R
import com.tuniptv.app.data.Session

/**
 * Fullscreen ExoPlayer activity (landscape).
 * The whole [Session.channels] list is loaded as the player's playlist,
 * so the built-in controller prev/next buttons and the on-screen
 * prev/next buttons zap between channels. The resize button toggles
 * between fit and zoom.
 */
class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var channelTitle: TextView
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var resizeButton: ImageButton
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        playerView = findViewById(R.id.player_view)
        channelTitle = findViewById(R.id.channel_title)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        resizeButton = findViewById(R.id.resize_button)
        errorText = findViewById(R.id.player_error)

        nextButton.setOnClickListener { player?.seekToNextMediaItem() }
        prevButton.setOnClickListener { player?.seekToPreviousMediaItem() }
        resizeButton.setOnClickListener { toggleResizeMode() }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        val channels = Session.channels
        if (channels.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_channels), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("TUN-IPTV/1.0")
            .setAllowCrossProtocolRedirects(true)
        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(dataSourceFactory)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .also { exo ->
                playerView.player = exo
                exo.addListener(playerListener)
                val items = channels.map { MediaItem.fromUri(it.streamUrl) }
                exo.setMediaItems(
                    items,
                    Session.currentIndex.coerceIn(items.indices),
                    0L
                )
                exo.prepare()
                exo.play()
                updateTitle()
            }
    }

    private fun toggleResizeMode() {
        playerView.resizeMode = if (playerView.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        } else {
            AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
    }

    private fun updateTitle() {
        channelTitle.text = Session.currentChannel?.name ?: ""
    }

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            player?.let {
                Session.currentIndex = it.currentMediaItemIndex
                updateTitle()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            errorText.visibility = View.VISIBLE
            errorText.text = getString(
                R.string.playback_error,
                error.message ?: "Unknown error"
            )
        }
    }

    private fun releasePlayer() {
        player?.removeListener(playerListener)
        player?.release()
        player = null
    }
}
