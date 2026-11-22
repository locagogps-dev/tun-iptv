package com.tuniptv.app.tv

import androidx.leanback.media.PlaybackBaseControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player

/**
 * Bridges ExoPlayer to the Leanback playback framework.
 *
 * Leanback's [PlayerAdapter] only requires [play] and [pause] to be
 * implemented; the state accessors below are overridden so the
 * transport controls (progress bar, duration, seek, fast-forward,
 * rewind) show real values. The base class handles the periodic
 * progress updates through [callback].
 *
 * Note: media3 does not ship a Leanback PlayerAdapter, so this
 * adapter is written by hand (same pattern as the old ExoPlayer 2
 * LeanbackPlayerAdapter sample).
 */
class ExoPlayerAdapter(
    private val player: Player
) : PlayerAdapter() {

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            callback?.onPlayStateChanged(this@ExoPlayerAdapter)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            callback?.onPlayStateChanged(this@ExoPlayerAdapter)
            callback?.onDurationChanged(this@ExoPlayerAdapter)
        }

        override fun onPlayerError(error: PlaybackException) {
            callback?.onError(
                this@ExoPlayerAdapter,
                error.errorCode,
                error.message ?: "Playback error"
            )
        }
    }

    init {
        player.addListener(playerListener)
    }

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun isPlaying(): Boolean = player.isPlaying

    /** -1 when the duration is unknown (live streams). */
    override fun getDuration(): Long =
        player.duration.takeIf { it >= 0 } ?: -1L

    override fun getCurrentPosition(): Long = player.currentPosition.coerceAtLeast(0)

    override fun getBufferedPosition(): Long = player.bufferedPosition.coerceAtLeast(0)

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }

    // The ACTION_* constants are ints in PlaybackBaseControlGlue,
    // so they must be widened to Long explicitly.
    override fun getSupportedActions(): Long =
        PlaybackBaseControlGlue.ACTION_PLAY_PAUSE.toLong() or
            PlaybackBaseControlGlue.ACTION_REWIND.toLong() or
            PlaybackBaseControlGlue.ACTION_FAST_FORWARD.toLong()

    /** Detach the ExoPlayer listener. Call before releasing the player. */
    fun release() {
        player.removeListener(playerListener)
    }
}
