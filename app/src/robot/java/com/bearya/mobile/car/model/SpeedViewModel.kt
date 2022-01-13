package com.bearya.mobile.car.model

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bearya.mobile.car.ext.setData
import com.vise.baseble.callback.IBleCallback
import com.vise.baseble.common.PropertyType
import com.vise.baseble.core.BluetoothGattChannel
import com.vise.baseble.core.DeviceMirror
import com.vise.baseble.exception.BleException
import com.vise.baseble.model.BluetoothLeDevice
import com.vise.baseble.utils.HexUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SpeedViewModel(application: Application) : AndroidViewModel(application) {

    private val leftSpeedMax = 100
    private val leftSpeedMin = 1
    private val rightSpeedMax = 100
    private val rightSpeedMin = 1

    private val notifyPropertyType: PropertyType = PropertyType.PROPERTY_NOTIFY
    private val writePropertyType: PropertyType = PropertyType.PROPERTY_WRITE
    private var deviceMirror: DeviceMirror? = null

    private val notifyChannel: BluetoothGattChannel by lazy {
        BluetoothGattChannel.Builder()
            .setBluetoothGatt(deviceMirror?.bluetoothGatt)
            .setPropertyType(notifyPropertyType)
            .setServiceUUID(UUID.fromString("000000ff-0000-1000-8000-00805f9b34fb"))
            .setCharacteristicUUID(UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"))
            .setDescriptorUUID(null)
            .builder()
    }

    private val writeChannel: BluetoothGattChannel by lazy {
        BluetoothGattChannel.Builder()
            .setBluetoothGatt(deviceMirror?.bluetoothGatt)
            .setPropertyType(writePropertyType)
            .setServiceUUID(UUID.fromString("000000ff-0000-1000-8000-00805f9b34fb"))
            .setCharacteristicUUID(UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"))
            .setDescriptorUUID(null)
            .builder()
    }

    val leftSpeed: MutableLiveData<Int> by lazy { MutableLiveData<Int>(leftSpeedMin) }
    val rightSpeed: MutableLiveData<Int> by lazy { MutableLiveData<Int>(rightSpeedMin) }

    fun leftAdd() {
        leftSpeed.value = when (leftSpeed.value) {
            in Int.MIN_VALUE until leftSpeedMin -> Toast.makeText(
                getApplication(),
                "当前速度已经最低，无法继续减速",
                Toast.LENGTH_SHORT
            ).show().let { leftSpeedMin }
            in leftSpeedMin until leftSpeedMax -> leftSpeed.value?.plus(1)
                ?.also { saveSpeed(false) }
            in leftSpeedMax..Int.MAX_VALUE -> Toast.makeText(
                getApplication(),
                "当前速度已经最高，无法继续加速",
                Toast.LENGTH_SHORT
            ).show().let { leftSpeedMax }
            else -> leftSpeedMin
        }
    }

    fun leftMinus() {
        leftSpeed.value = when (leftSpeed.value) {
            in Int.MIN_VALUE..leftSpeedMin -> Toast.makeText(
                getApplication(),
                "当前速度已经最低，无法继续减速",
                Toast.LENGTH_SHORT
            ).show().let { leftSpeedMin }
            in (leftSpeedMin + 1)..leftSpeedMax -> leftSpeed.value?.minus(1)
                ?.also { saveSpeed(false) }
            in (leftSpeedMax + 1)..Int.MAX_VALUE -> Toast.makeText(
                getApplication(),
                "当前速度已经最高，无法继续加速",
                Toast.LENGTH_SHORT
            ).show().let { leftSpeedMax }
            else -> leftSpeedMax
        }
    }

    fun rightAdd() {
        rightSpeed.value = when (rightSpeed.value) {
            in Int.MIN_VALUE until rightSpeedMin -> Toast.makeText(
                getApplication(),
                "当前速度已经最低，无法继续减速",
                Toast.LENGTH_SHORT
            ).show().let { rightSpeedMin }
            in rightSpeedMin until rightSpeedMax -> rightSpeed.value?.plus(1)
                ?.also { saveSpeed(false) }
            in rightSpeedMax..Int.MAX_VALUE -> Toast.makeText(
                getApplication(),
                "当前速度已经最高，无法继续加速",
                Toast.LENGTH_SHORT
            ).show().let { rightSpeedMax }
            else -> rightSpeedMin
        }
    }

    fun rightMinus() {
        rightSpeed.value = when (rightSpeed.value) {
            in Int.MIN_VALUE..rightSpeedMin -> Toast.makeText(
                getApplication(),
                "当前速度已经最低，无法继续减速",
                Toast.LENGTH_SHORT
            ).show().let { rightSpeedMin }
            in (rightSpeedMin + 1)..rightSpeedMax -> rightSpeed.value?.minus(1)
                ?.also { saveSpeed(false) }
            in (rightSpeedMax + 1)..Int.MAX_VALUE -> Toast.makeText(
                getApplication(),
                "当前速度已经最高，无法继续加速",
                Toast.LENGTH_SHORT
            ).show().let { rightSpeedMax }
            else -> rightSpeedMax
        }
    }

    fun bindChannel(mirror: DeviceMirror?) {
        deviceMirror = mirror
        deviceMirror?.bindChannel(object : IBleCallback {
            override fun onSuccess(
                byteArray: ByteArray?,
                bluetoothGattChannel: BluetoothGattChannel?,
                bluetoothLeDevice: BluetoothLeDevice?
            ) {
                if (bluetoothGattChannel?.propertyType == PropertyType.PROPERTY_NOTIFY ||
                    bluetoothGattChannel?.propertyType == PropertyType.PROPERTY_INDICATE
                )
                    mirror?.setNotifyListener(
                        bluetoothGattChannel.gattInfoKey,
                        object : IBleCallback {
                            override fun onSuccess(
                                data: ByteArray?,
                                bluetoothGattChannel: BluetoothGattChannel?,
                                bluetoothLeDevice: BluetoothLeDevice?
                            ) {
                                val response = HexUtil.encodeHexStr(data)
                                if (response.uppercase().startsWith("D001")) {
                                    val leftSpeedResponse = response.substring(4, 6)
                                    val rightSpeedResponse = response.substring(6, 8)
                                    leftSpeed.setData(Integer.parseInt(leftSpeedResponse, 16))
                                    rightSpeed.setData(Integer.parseInt(rightSpeedResponse, 16))
                                }
                            }

                            override fun onFailure(exception: BleException?) {

                            }
                        })
            }

            override fun onFailure(exception: BleException?) {

            }
        }, notifyChannel)
        if (notifyPropertyType == PropertyType.PROPERTY_NOTIFY)
            deviceMirror?.registerNotify(false)
        else if (notifyPropertyType == PropertyType.PROPERTY_INDICATE)
            deviceMirror?.registerNotify(true)

        deviceMirror?.bindChannel(object : IBleCallback {
            override fun onSuccess(
                byteArray: ByteArray?,
                bluetoothGattChannel: BluetoothGattChannel?,
                bluetoothLeDevice: BluetoothLeDevice?
            ) {

            }

            override fun onFailure(exception: BleException?) {

            }
        }, writeChannel)

    }

    fun saveSpeed(isSave: Boolean) {
        val saveFlag = if (isSave) "02" else "01" // 02 : 保存调整 ; 01 : 发送调整
        val leftSpeedSave = Integer.toHexString(leftSpeed.value ?: 200)
        val rightSpeedSave = Integer.toHexString(rightSpeed.value ?: 200)
        val saveMessage = HexUtil.decodeHex("F3${saveFlag}${leftSpeedSave}${rightSpeedSave}")
        viewModelScope.launch(Dispatchers.IO) {
            deviceMirror?.writeData(saveMessage)
        }
        if (isSave) Toast.makeText(getApplication(), "速度保存成功", Toast.LENGTH_SHORT).show()
    }

    fun fetchCurrentSpeed() {
        viewModelScope.launch(Dispatchers.IO) {
            deviceMirror?.writeData(HexUtil.decodeHex("F3030101"))
        }
    }

    fun resetSpeed() {
        leftSpeed.value = 200
        rightSpeed.value = 200
        viewModelScope.launch(Dispatchers.IO) {
            deviceMirror?.writeData(HexUtil.decodeHex("F302C8C8"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (notifyPropertyType == PropertyType.PROPERTY_NOTIFY)
            deviceMirror?.unregisterNotify(false)
        else if (notifyPropertyType == PropertyType.PROPERTY_INDICATE)
            deviceMirror?.unregisterNotify(true)
        deviceMirror?.unbindChannel(notifyChannel)
        deviceMirror?.unbindChannel(writeChannel)
    }

}