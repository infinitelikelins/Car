package com.bearya.mobile.car.model

import androidx.lifecycle.ViewModel
import com.vise.baseble.ViseBle
import com.vise.baseble.core.DeviceMirror
import com.vise.baseble.model.BluetoothLeDevice

class ControllerViewModel : ViewModel() {

    private var deviceMirror: DeviceMirror? = null

    fun fetchDevice(device: BluetoothLeDevice?) {
        deviceMirror = ViseBle.getInstance().getDeviceMirror(device)
    }

    override fun onCleared() {
        super.onCleared()
        deviceMirror?.disconnect()
    }

}