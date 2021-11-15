package com.bearya.mobile.car.adapter

import android.text.style.AbsoluteSizeSpan
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
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
        holder.setText(R.id.device, buildSpannedString {
            bold {
                inSpans(AbsoluteSizeSpan(64)) {
                    append(item?.name)
                }
            }
            append("\n\n")
            inSpans(AbsoluteSizeSpan(40)) {
                append(item?.address)
            }
        })
    }

}