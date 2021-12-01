package com.bearya.mobile.car.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bearya.mobile.car.R
import com.bearya.mobile.car.databinding.ActivityEmotionBinding
import com.bearya.mobile.car.fragment.FrameFragment
import com.bearya.mobile.car.fragment.LottieFragment
import com.bearya.mobile.car.model.EmotionViewModel
import com.bearya.mobile.car.popup.AbsBasePopup
import com.bearya.mobile.car.popup.FairyResultPopup
import com.bearya.mobile.car.popup.ProgrammerResultPopup
import com.bearya.mobile.car.repository.ImageType
import com.vise.baseble.ViseBle
import com.vise.baseble.model.BluetoothLeDevice

class EmotionActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context, device: BluetoothLeDevice?) {
            context.startActivity(
                Intent(context, EmotionActivity::class.java).putExtra("device", device)
            )
        }
    }

    private lateinit var bindView: ActivityEmotionBinding

    private val emotionViewModel: EmotionViewModel by viewModels()

    private var basePopup: AbsBasePopup? = null

    private val localBroadcastManagerReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "ACTION_FINISH") {
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView = ActivityEmotionBinding.inflate(layoutInflater)
        setContentView(bindView.root)

        val device = intent.getParcelableExtra<BluetoothLeDevice?>("device")
        val deviceMirror = ViseBle.getInstance().getDeviceMirror(device)
        emotionViewModel.bindChannel(deviceMirror)

        // 过程的帧动画和表情动画交替
        emotionViewModel.emotion.observe(this) {
            basePopup?.dismiss()
            basePopup = null
            when (it?.first) {
                ImageType.Lottie -> LottieFragment.newInstance(it.second as String)
                ImageType.Frame -> FrameFragment.newInstance(it.second as Int)
                else -> null
            }?.also { fragment ->
                supportFragmentManager.commit {
                    replace(R.id.fragment_container, fragment)
                }
            }
        }

        // 童话世界的结果弹窗
        emotionViewModel.fairy.observe(this) {
            basePopup = FairyResultPopup(this, it)
            basePopup?.showPopupWindow()
        }

        // 跳跳镇的结果弹窗
        emotionViewModel.programmer.observe(this) {
            basePopup = ProgrammerResultPopup(this, it)
            basePopup?.showPopupWindow()
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                localBroadcastManagerReceiver,
                IntentFilter("ACTION_FINISH")
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastManagerReceiver)
    }

}