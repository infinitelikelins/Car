package com.bearya.mobile.car.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bearya.mobile.car.R
import com.bearya.mobile.car.databinding.ActivitySpeedBinding
import com.bearya.mobile.car.model.SpeedViewModel
import com.kaopiz.kprogresshud.KProgressHUD
import com.vise.baseble.ViseBle
import com.vise.baseble.model.BluetoothLeDevice
import kotlinx.coroutines.delay

class SpeedActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var bindView: ActivitySpeedBinding
    private val viewModel: SpeedViewModel by viewModels()
    private var device: BluetoothLeDevice? = null

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
                Intent(context, SpeedActivity::class.java).putExtra("device", device)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView = ActivitySpeedBinding.inflate(layoutInflater)
        setContentView(bindView.root)

        device = intent.getParcelableExtra("device")
        val deviceMirror = ViseBle.getInstance().getDeviceMirror(device)
        viewModel.bindChannel(deviceMirror)

        bindView.leftAdd.setOnClickListener(this)
        bindView.leftMinus.setOnClickListener(this)
        bindView.rightAdd.setOnClickListener(this)
        bindView.rightMinus.setOnClickListener(this)
        bindView.saveSpeed.setOnClickListener(this)
        bindView.fetchSpeed.setOnClickListener(this)
        bindView.resetSpeed.setOnClickListener(this)
        bindView.back.setOnClickListener(this)

        viewModel.leftSpeed.observe(this) {
            bindView.leftSpeed.text = "$it"
        }
        viewModel.rightSpeed.observe(this) {
            bindView.rightSpeed.text = "$it"
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            localBroadcastManagerReceiver,
            IntentFilter("ACTION_FINISH")
        )

        lifecycleScope.launchWhenResumed {
            val hud = KProgressHUD.create(this@SpeedActivity)
                .setLabel("正在获取当前速度中...").setCancellable(false).show()
            delay(1500)
            viewModel.fetchCurrentSpeed()
            hud.dismiss()
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.left_add -> viewModel.leftAdd()
            R.id.left_minus -> viewModel.leftMinus()
            R.id.right_add -> viewModel.rightAdd()
            R.id.right_minus -> viewModel.rightMinus()
            R.id.save_speed -> viewModel.saveSpeed(true)
            R.id.fetch_speed -> viewModel.fetchCurrentSpeed()
            R.id.reset_speed -> viewModel.resetSpeed()
            R.id.back -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastManagerReceiver)
    }

}