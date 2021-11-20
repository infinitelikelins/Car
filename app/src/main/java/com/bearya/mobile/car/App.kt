package com.bearya.mobile.car

import android.app.Application
import com.kelin.apkUpdater.ApkUpdater

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ApkUpdater.init(this,"${BuildConfig.APPLICATION_ID}.fileProvider")
    }

}