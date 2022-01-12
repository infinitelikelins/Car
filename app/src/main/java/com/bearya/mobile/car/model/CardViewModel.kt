package com.bearya.mobile.car.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearya.mobile.car.ext.setData
import com.vise.baseble.callback.IBleCallback
import com.vise.baseble.common.PropertyType
import com.vise.baseble.core.BluetoothGattChannel
import com.vise.baseble.core.DeviceMirror
import com.vise.baseble.exception.BleException
import com.vise.baseble.model.BluetoothLeDevice
import com.vise.baseble.utils.HexUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class CardViewModel : ViewModel() {

    private val notifyPropertyType: PropertyType = PropertyType.PROPERTY_NOTIFY
    private val writePropertyType: PropertyType = PropertyType.PROPERTY_WRITE
    private var deviceMirror: DeviceMirror? = null
    val responseResult: MutableLiveData<String> by lazy { MutableLiveData("") }

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

    fun bindChannel(mirror: DeviceMirror?) {
        responseResult.setData("null")
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
                                responseResult.setData(response.uppercase())
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

    fun sendCardsToRobot(cards: String) {
        write(HexUtil.decodeHex(cards))
    }

    fun unbindChannel() {
        if (notifyPropertyType == PropertyType.PROPERTY_NOTIFY)
            deviceMirror?.unregisterNotify(false)
        else if (notifyPropertyType == PropertyType.PROPERTY_INDICATE)
            deviceMirror?.unregisterNotify(true)
        deviceMirror?.unbindChannel(notifyChannel)
        deviceMirror?.unbindChannel(writeChannel)
    }

    //外部调用发送数据方法
    private fun write(data: ByteArray?) {
        if (dataInfoQueue != null) {
            dataInfoQueue!!.clear()
            dataInfoQueue = splitPacketFor20Byte(data)
            send()
        }
    }

    //发送队列，提供一种简单的处理方式，实际项目场景需要根据需求优化
    private var dataInfoQueue: Queue<ByteArray?>? = LinkedList()
    private fun send() {
        if (dataInfoQueue != null && dataInfoQueue!!.isNotEmpty()) {
            if (dataInfoQueue!!.peek() != null && deviceMirror != null) {
                deviceMirror?.writeData(dataInfoQueue!!.poll())
            }
        }
        if (dataInfoQueue!!.peek() != null) {
            viewModelScope.launch {
                delay(500)
                send()
            }
        }
    }

    /**
     * 数据分包
     *
     * @param data
     * @return
     */
    private fun splitPacketFor20Byte(data: ByteArray?): Queue<ByteArray?> {
        val dataInfoQueue: Queue<ByteArray?> = LinkedList()
        if (data != null) {
            var index = 0
            do {
                val surplusData = ByteArray(data.size - index)
                var currentData: ByteArray
                System.arraycopy(data, index, surplusData, 0, data.size - index)
                if (surplusData.size <= 20) {
                    currentData = ByteArray(surplusData.size)
                    System.arraycopy(surplusData, 0, currentData, 0, surplusData.size)
                    index += surplusData.size
                } else {
                    currentData = ByteArray(20)
                    System.arraycopy(data, index, currentData, 0, 20)
                    index += 20
                }
                dataInfoQueue.offer(currentData)
            } while (index < data.size)
        }
        return dataInfoQueue
    }

}