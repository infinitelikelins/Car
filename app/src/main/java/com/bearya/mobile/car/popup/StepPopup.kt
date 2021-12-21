package com.bearya.mobile.car.popup

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearya.mobile.car.R
import com.bearya.mobile.car.adapter.InstructionsAdapter
import com.bearya.mobile.car.repository.CardType
import com.google.android.material.tabs.TabLayout

class StepPopup(context: Context) : AbsBasePopup(context) {

    private val paramInstructions = mutableListOf(
        CardType.PARAM1, CardType.PARAM2, CardType.PARAM3, CardType.PARAM4, CardType.PARAM5,
    )

    private val adapter: InstructionsAdapter by lazy { InstructionsAdapter(paramInstructions) }
    var popupWithClick: ((cardType: CardType) -> Unit)? = null

    private val tabName = mutableListOf(
        "参数" to paramInstructions,
    )

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