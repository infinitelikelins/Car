package com.bearya.mobile.car.popup

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.bearya.mobile.car.R
import com.bearya.mobile.car.repository.CardType
import com.bearya.mobile.car.repository.cardResource

class ReplacePopup(context: Context,private val cardType: CardType) : AbsBasePopup(context),
    View.OnClickListener {

    var popupWithClick: ((cardType: CardType) -> Unit)? = null

    init {

        setContentView(R.layout.popup_replace)

        val originalAction = findViewById<AppCompatImageView>(R.id.original_action)
        val insertLeft = findViewById<AppCompatImageView>(R.id.insert_left)
        val insertRight = findViewById<AppCompatImageView>(R.id.insert_right)

        originalAction?.setImageResource(cardResource(cardType))
        originalAction?.setOnClickListener(this)
        insertLeft?.setOnClickListener(this)
        insertRight?.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.insert_left -> popupWithClick?.invoke(CardType.INSERT_LEFT)
            R.id.insert_right -> popupWithClick?.invoke(CardType.INSERT_RIGHT)
            R.id.original_action -> popupWithClick?.invoke(cardType)
        }
        dismiss(true)
    }

}