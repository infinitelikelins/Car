package com.bearya.mobile.car.adapter

import com.bearya.mobile.car.R
import com.bearya.mobile.car.entity.Card
import com.bearya.mobile.car.repository.CardType
import com.bearya.mobile.car.repository.cardResource
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class CardAdapter : BaseQuickAdapter<Card, BaseViewHolder>(R.layout.item_card) {

    init {
        addChildClickViewIds(R.id.parent_action, R.id.step_bg)
        addChildLongClickViewIds(R.id.parent_action)
    }

    override fun convert(holder: BaseViewHolder, item: Card) {
        holder.setText(R.id.position, "${holder.bindingAdapterPosition + 1}")
        holder.setImageResource(R.id.parent_action, cardResource(item.cardType))
        holder.setVisible(R.id.step_bg, item.cardType == CardType.FORWARD || item.cardType == CardType.CLOSURE)
        holder.setVisible(R.id.step, item.cardType == CardType.FORWARD || item.cardType == CardType.CLOSURE)
        holder.setText(R.id.step, "${item.step}")
    }

}