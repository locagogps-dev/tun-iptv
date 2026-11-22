package com.tuniptv.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

/**
 * Forces the Premium Dark theme on every device, phone and TV.
 */
class TunIptvApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}
