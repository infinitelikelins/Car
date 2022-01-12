package com.bearya.mobile.car.activity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bearya.mobile.car.R
import com.bearya.mobile.car.adapter.CardAdapter
import com.bearya.mobile.car.databinding.ActivityCardBinding
import com.bearya.mobile.car.entity.Card
import com.bearya.mobile.car.model.CardViewModel
import com.bearya.mobile.car.popup.CardPopup
import com.bearya.mobile.car.popup.ReplacePopup
import com.bearya.mobile.car.popup.StepPopup
import com.bearya.mobile.car.repository.CardType
import com.bearya.mobile.car.repository.stepResource
import com.vise.baseble.ViseBle
import com.vise.baseble.core.DeviceMirror
import com.vise.baseble.model.BluetoothLeDevice
import kotlinx.coroutines.*

class CardActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context, device: BluetoothLeDevice?) {
            context.startActivity(
                Intent(context, CardActivity::class.java).putExtra("device", device)
            )
        }
    }

    private val localBroadcastManagerReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "ACTION_FINISH") {
                    finish()
                }
            }
        }
    }

    private val viewModel: CardViewModel by viewModels()
    private var device: BluetoothLeDevice? = null
    private var deviceMirror: DeviceMirror? = null
    private var animator: Animator? = null
    private lateinit var bindView: ActivityCardBinding
    private val cardAdapter: CardAdapter by lazy { CardAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindView = ActivityCardBinding.inflate(layoutInflater)
        setContentView(bindView.root)

        device = intent.getParcelableExtra("device")
        deviceMirror = ViseBle.getInstance().getDeviceMirror(device)

        bindView.back.setOnClickListener { finish() }
        bindView.cardRun.setOnClickListener { cardRun() }
        bindView.sceneSync.setOnClickListener { EmotionActivity.start(this, device) }
        bindView.cards.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val footer = layoutInflater.inflate(R.layout.item_card_add, bindView.cards, false).apply {
            setOnClickListener {
                showCardPop()
            }
        }
        animator = AnimatorInflater.loadAnimator(this, R.animator.card_add_animator)
            .apply { setTarget(footer) }

        cardAdapter.addFooterView(footer, 0, LinearLayoutManager.HORIZONTAL)
        cardAdapter.footerViewAsFlow = true
        cardAdapter.isAnimationFirstOnly = false
        cardAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.parent_action -> replaceCardPopup(position, cardAdapter.getItem(position))
                R.id.step_bg -> showStepPopup(position)
            }
        }
        cardAdapter.setOnItemChildLongClickListener { _, _, position ->
            cardAdapter.removeAt(position).let { false }
        }
        bindView.cards.adapter = cardAdapter

        LocalBroadcastManager.getInstance(this).registerReceiver(
            localBroadcastManagerReceiver,
            IntentFilter("ACTION_FINISH")
        )

        viewModel.responseResult.observe(this) {
            if (it == "E0020201") {
                lifecycleScope.cancel()
                Toast.makeText(this, "指令发送完成", Toast.LENGTH_SHORT).show()
                EmotionActivity.start(this, device)
            } else if (it == "E0020202") {
                lifecycleScope.cancel()
                Toast.makeText(this, "指令校验失败,发送失败,请重新执行.", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        animator?.start()
        viewModel.bindChannel(deviceMirror)
    }

    override fun onPause() {
        super.onPause()
        animator?.cancel()
        viewModel.unbindChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastManagerReceiver)
    }

    private fun checkCard(): Boolean {
        val data = cardAdapter.data
        if (data.size <= 0) {
            Toast.makeText(this, "指令不能空集", Toast.LENGTH_LONG).show()
            return false
        }
        var loopCloseable = 0
        var errorIndex = -1
        var actionIndex = -1
        for (i in data.indices) {
            val cardParentAction = data[i]
            if (cardParentAction.cardType === CardType.LOOP) {
                loopCloseable++
                actionIndex = i
                if (loopCloseable >= 2) {
                    errorIndex = i
                    break
                }
            } else if (cardParentAction.cardType === CardType.CLOSURE) {
                loopCloseable--
                actionIndex = i
                if (loopCloseable < 0) {
                    errorIndex = i
                    break
                }
            }
        }
        if (loopCloseable != 0 && errorIndex > -1) {
            Toast.makeText(this, "第${errorIndex + 1}张卡片错误,请检查", Toast.LENGTH_LONG).show()
            return false
        } else if (loopCloseable == 1) {
            Toast.makeText(this, "第${actionIndex + 1}张卡片错误,请检查", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun cardRun() {
        if (checkCard()) {
            buildCards().apply { viewModel.sendCardsToRobot(this) }
            Toast.makeText(this@CardActivity, "指令正在发送中", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                delay(5000)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CardActivity, "发送超时", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun buildCards() = buildString {
        var itemChildCount = 0
        var totalCount = CardType.START.card.toInt(16)
        append(CardType.FLAG.card) // 集合标志标识
        cardAdapter.data.forEach {
            itemChildCount += if (it.step > 1) 1 else 0
        } // 集合附带参数标识
        append((cardAdapter.data.size + itemChildCount + 2).toString(16)
            .let { if (it.length % 2 == 0) it else "0$it" }) // 集合校验数量额
        append(CardType.START.card) // 集合开始标识
        cardAdapter.data.forEach { item ->
            append(item.cardType.card) // 集合指令标识
            totalCount += item.cardType.card.toInt(16)
            if ((item.cardType == CardType.FORWARD || item.cardType == CardType.CLOSURE) && item.step > 1) {
                val stepResource = stepResource(item.step)
                append("$stepResource")
                totalCount += stepResource?.toInt(16) ?: 0
            }
        }
        append(CardType.END.card) // 集合结尾标识
        totalCount += CardType.END.card.toInt(16)
        val total = totalCount.toString(16).let { if (it.length % 2 == 0) it else "0$it" }
        append(total) // 集合校验数据总和额
    }

    private fun showCardPop(position: Int = cardAdapter.itemCount - 1, update: Boolean = false) {
        CardPopup(this).apply {
            popupWithClick = { cardType ->
                if (update) {
                    cardAdapter.setData(position, Card(cardType))
                } else {
                    cardAdapter.addData(position, Card(cardType))
                    (cardAdapter.data.size - position - 1).takeIf { it > 0 }?.run {
                        cardAdapter.notifyItemRangeChanged(position + 1, this)
                    }
                }
                bindView.cards.smoothScrollToPosition(position + 1)
            }
        }.showPopupWindow()
    }

    private fun replaceCardPopup(position: Int, card: Card) {
        ReplacePopup(this, card.cardType).apply {
            popupWithClick = { cardType ->
                when (cardType) {
                    CardType.INSERT_LEFT -> showCardPop(position, false)
                    CardType.INSERT_RIGHT -> showCardPop(position + 1, false)
                    else -> showCardPop(position, true)
                }
            }
        }.showPopupWindow()
    }

    private fun showStepPopup(position: Int) {
        StepPopup(this).apply {
            popupWithClick = {
                cardAdapter.getItem(position).step = stepResource(it)
                cardAdapter.notifyItemChanged(position)
            }
        }.showPopupWindow()
    }

}