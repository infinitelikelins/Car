package com.bearya.mobile.car.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.bearya.mobile.car.repository.ImageType
import com.bearya.mobile.car.repository.emotionRepository
import com.bearya.mobile.car.repository.frameRepository
import com.vise.baseble.callback.IBleCallback
import com.vise.baseble.common.PropertyType
import com.vise.baseble.core.BluetoothGattChannel
import com.vise.baseble.core.DeviceMirror
import com.vise.baseble.exception.BleException
import com.vise.baseble.model.BluetoothLeDevice
import com.vise.baseble.utils.HexUtil
import java.util.*

class EmotionViewModel : ViewModel() {

    private val propertyType: PropertyType = PropertyType.PROPERTY_NOTIFY

    private var deviceMirror: DeviceMirror? = null

    private val channel: BluetoothGattChannel by lazy {
        BluetoothGattChannel.Builder()
            .setBluetoothGatt(deviceMirror?.bluetoothGatt)
            .setPropertyType(propertyType)
            .setServiceUUID(UUID.fromString("000000ff-0000-1000-8000-00805f9b34fb"))
            .setCharacteristicUUID(UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"))
            .setDescriptorUUID(null)
            .builder()
    }

    private val emotionId: MutableLiveData<String> by lazy { MutableLiveData<String>("e0010101") }

    val emotion: LiveData<Pair<ImageType, Any>?> = emotionId.map {
        emotionRepository(it) ?: frameRepository(it)
    }

    private val fairyResult: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    val fairy: LiveData<Pair<String, String>> = fairyResult.map {
        val stepCount = it.substring(8, 10).toInt(16).toString()
        val propCount = it.substring(10, 12).toInt(16).toString()
        Pair(stepCount, propCount)
    }

    private val programmerResult: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    val programmer: LiveData<Triple<String, String, String>> = programmerResult.map {
        val stepCount = it.substring(8, 10).toInt(16).toString()
        val coinCount = it.substring(10, 12).toInt(16).toString()
        val knownCount = it.substring(12, 14).toInt(16).toString()
        Triple(stepCount, coinCount, knownCount)
    }

    private fun updateEmotion(emotion: String?) {
        if (emotion != null && emotion.isNotBlank()) {
            emotionId.postValue(emotion)
        }
    }

    private fun updateProgrammerResult(result: String?) {
        if (result != null && result.isNotBlank() && result.length == 14) {
            programmerResult.postValue(result)
        }
    }

    private fun updateFairyResult(result: String?) {
        if (result != null && result.isNotBlank() && result.length == 12) {
            fairyResult.postValue(result)
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
                                val encodeEmotionId = HexUtil.encodeHexStr(data)
                                when {
                                    encodeEmotionId.length == 8 ->
                                        updateEmotion(encodeEmotionId)
                                    encodeEmotionId.startsWith("e0010201") ->
                                        updateProgrammerResult(encodeEmotionId)
                                    encodeEmotionId.startsWith("e0010202") ->
                                        updateFairyResult(encodeEmotionId)
                                }
                            }

                            override fun onFailure(exception: BleException?) {

                            }
                        })
            }

            override fun onFailure(exception: BleException?) {

            }
        }, channel)
        if (propertyType == PropertyType.PROPERTY_NOTIFY)
            deviceMirror?.registerNotify(false)
        else if (propertyType == PropertyType.PROPERTY_INDICATE)
            deviceMirror?.registerNotify(true)
    }

    fun unbindChannel() {
        if (propertyType == PropertyType.PROPERTY_NOTIFY)
            deviceMirror?.unregisterNotify(false)
        else if (propertyType == PropertyType.PROPERTY_INDICATE)
            deviceMirror?.unregisterNotify(true)
        deviceMirror?.unbindChannel(channel)
    }

}