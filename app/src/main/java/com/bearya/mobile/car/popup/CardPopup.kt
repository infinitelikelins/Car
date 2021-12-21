package com.bearya.mobile.car.popup

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearya.mobile.car.R
import com.bearya.mobile.car.adapter.InstructionsAdapter
import com.bearya.mobile.car.repository.CardType
import com.google.android.material.tabs.TabLayout

class CardPopup(context: Context) : AbsBasePopup(context) {

    private val directionInstructions = mutableListOf(
        CardType.FORWARD, CardType.BACKWARD, CardType.LEFT, CardType.RIGHT,
    )

    private val grammarInstructions = mutableListOf(
        CardType.LOOP, CardType.CLOSURE,
    )

    private val eventInstructions = mutableListOf(
        CardType.FIRE_TRUCK, CardType.POLICE_CAR,
        CardType.AMBULANCE, CardType.GOLDEN,
    )

    private val propInstructions = mutableListOf(
        CardType.ARMOR, CardType.PEGASUS, CardType.SWORD,
        CardType.SKIRT, CardType.SHOES, CardType.CARRIAGE,
        CardType.MAP, CardType.KEY, CardType.COMPASS
    )

    private val tabName = mutableListOf(
        "行动" to directionInstructions,
        "语法" to grammarInstructions,
        "道具" to propInstructions,
        "事件" to eventInstructions,
    )

    private val adapter: InstructionsAdapter by lazy { InstructionsAdapter(directionInstructions) }
    var popupWithClick: ((cardType: CardType) -> Unit)? = null

    init {
        setContentView(R.layout.popup_card)

        val cardTab = findViewById<TabLayout>(R.id.card_tab)
        tabName.forEachIndexed { _, item ->
            cardTab.addTab(cardTab.newTab().setText(item.first))
        }
        cardTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                adapter.setNewInstance(tabName[tab?.position ?: 0].second)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        val instructionsRecyclerView = findViewById<RecyclerView>(R.id.instructions_recycler_view)
        instructionsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        instructionsRecyclerView.adapter = adapter.apply {
            setOnItemClickListener { _, _, position ->
                popupWithClick?.invoke(adapter.getItem(position))
                dismiss(true)
            }
        }
    }

}