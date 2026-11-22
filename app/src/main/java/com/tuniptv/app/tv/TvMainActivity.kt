package com.tuniptv.app.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.tuniptv.app.R

/**
 * Android TV entry point (LEANBACK_LAUNCHER). Hosts [TvBrowseFragment].
 */
class TvMainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.tv_browse_container, TvBrowseFragment())
                .commitNow()
        }
    }
}
