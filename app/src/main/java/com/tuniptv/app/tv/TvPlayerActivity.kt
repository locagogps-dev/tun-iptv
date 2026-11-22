package com.tuniptv.app.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.tuniptv.app.R

/**
 * Android TV player host. The actual playback happens in
 * [TvVideoFragment], a Leanback VideoSupportFragment driven by
 * ExoPlayer through a PlaybackTransportControlGlue.
 */
class TvPlayerActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_player)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.tv_player_container, TvVideoFragment())
                .commitNow()
        }
    }
}
