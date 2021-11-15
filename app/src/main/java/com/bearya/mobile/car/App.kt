package com.bearya.mobile.car

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(this, "b8e7263382", true)
    }

}