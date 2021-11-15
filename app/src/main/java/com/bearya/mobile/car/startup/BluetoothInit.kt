package com.bearya.mobile.car.startup

import android.content.Context
import androidx.startup.Initializer
import com.vise.baseble.ViseBle

class BluetoothInit : Initializer<Any> {

    override fun create(context: Context) {
        ViseBle.config().apply {
            maxConnectCount = 1
            connectRetryCount = 2
            connectRetryInterval = 1000
            operateTimeout = 3 * 1000
            connectTimeout = 3 * 1000
            scanTimeout = 12 * 1000
        }
        ViseBle.getInstance().init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()

}