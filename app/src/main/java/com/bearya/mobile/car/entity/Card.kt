package com.bearya.mobile.car.entity

import com.bearya.mobile.car.repository.CardType

data class Card(val cardType: CardType, var step: Int = 1)
