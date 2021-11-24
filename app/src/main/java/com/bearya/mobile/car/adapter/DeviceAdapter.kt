package com.bearya.mobile.car.adapter

import androidx.recyclerview.widget.DiffUtil
import com.bearya.mobile.car.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.vise.baseble.model.BluetoothLeDevice

class DeviceAdapter :
    BaseQuickAdapter<BluetoothLeDevice?, BaseViewHolder>(R.layout.item_bluetooth_device) {

    init {
        setDiffCallback(object : DiffUtil.ItemCallback<BluetoothLeDevice?>() {
            override fun areItemsTheSame(
                oldItem: BluetoothLeDevice,
                newItem: BluetoothLeDevice
            ): Boolean = oldItem.address == newItem.address

            override fun areContentsTheSame(
                oldItem: BluetoothLeDevice,
                newItem: BluetoothLeDevice
            ): Boolean = oldItem.name == newItem.name
        })
    }

    override fun convert(holder: BaseViewHolder, item: BluetoothLeDevice?) {
        holder.setText(R.id.device_address, item?.address)
    }

}