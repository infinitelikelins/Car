package com.bearya.mobile.car.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bearya.mobile.car.R
import com.bearya.mobile.car.databinding.ActivityControllerBinding
import com.bearya.mobile.car.model.ControllerViewModel
import com.vise.baseble.model.BluetoothLeDevice

class ControllerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var bindView: ActivityControllerBinding
    private var device: BluetoothLeDevice? = null
    private val viewModel :ControllerViewModel by viewModels()

    private val localBroadcastManagerReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "ACTION_FINISH") {
                    finish()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context, device: BluetoothLeDevice?) {
            context.startActivity(
                Intent(context, ControllerActivity::class.java).putExtra("device", device)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView = ActivityControllerBinding.inflate(layoutInflater)
        setContentView(bindView.root)

        device = intent.getParcelableExtra("device")
        viewModel.fetchDevice(device)

        bindView.emotion.setOnClickListener(this)
        bindView.speed.setOnClickListener(this)
        bindView.back.setOnClickListener(this)

        LocalBroadcastManager.getInstance(this).registerReceiver(
                localBroadcastManagerReceiver,
                IntentFilter("ACTION_FINISH")
        )

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.emotion -> EmotionActivity.start(this, device)
            R.id.speed -> SpeedActivity.start(this, device)
            R.id.back -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastManagerReceiver)
    }

}