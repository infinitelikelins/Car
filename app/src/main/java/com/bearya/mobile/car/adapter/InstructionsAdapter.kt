package com.bearya.mobile.car.adapter

import com.bearya.mobile.car.R
import com.bearya.mobile.car.repository.CardType
import com.bearya.mobile.car.repository.cardSelector
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class InstructionsAdapter(instructions: MutableList<CardType>) :
    BaseQuickAdapter<CardType, BaseViewHolder>(R.layout.item_instructions, instructions) {

    override fun convert(holder: BaseViewHolder, item: CardType) {
        holder.setImageResource(R.id.instructions, cardSelector(item))
    }

}