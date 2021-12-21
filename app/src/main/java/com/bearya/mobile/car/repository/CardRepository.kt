package com.bearya.mobile.car.repository

import com.bearya.mobile.car.R

enum class CardType(val card: String) {
    FLAG("F0"),
    START("F7"),
    END("F6"),

    FORWARD("E8"),
    BACKWARD("E5"),
    LEFT("E7"),
    RIGHT("E8"),
    LOOP("D7"),
    CLOSURE("D8"),

    PARAM1("-3"),
    PARAM2("C9"),
    PARAM3("CA"),
    PARAM4("CB"),
    PARAM5("CC"),

    FIRE_TRUCK("EB"), // 消防车
    POLICE_CAR("E9"), // 警车
    AMBULANCE("EA"), // 救护车

    GOLDEN("EC"), // 金币

    MAP("DC"), // 藏宝图
    COMPASS("DD"), // 指南针
    KEY("DE"), // 钥匙

    ARMOR("E1"), // 铠甲
    SWORD("E0"), // 宝剑
    PEGASUS("DF"), // 飞马

    SKIRT("E3"), // 舞裙
    SHOES("E4"), // 舞鞋
    CARRIAGE("E2"), // 马车

    INSERT_LEFT("-1"), // 左边插入卡片
    INSERT_RIGHT("-2"), // 右边插入卡片
}

fun cardResource(card: CardType): Int = when (card) {
    CardType.FORWARD -> R.drawable.ic_arrow_upward_black_24dp
    CardType.BACKWARD -> R.drawable.ic_arrow_downward_black_24dp
    CardType.LEFT -> R.drawable.ic_left
    CardType.RIGHT -> R.drawable.ic_right
    CardType.LOOP -> R.drawable.ic_loop_2
    CardType.CLOSURE -> R.drawable.ic_closure_2
    CardType.PARAM1 -> R.drawable.ic_plus_1
    CardType.PARAM2 -> R.drawable.ic_plus_2
    CardType.PARAM3 -> R.drawable.ic_plus_3
    CardType.PARAM4 -> R.drawable.ic_plus_4
    CardType.PARAM5 -> R.drawable.ic_plus_5
    CardType.FIRE_TRUCK -> R.drawable.ic_fire_truck
    CardType.POLICE_CAR -> R.drawable.ic_police_car
    CardType.AMBULANCE -> R.drawable.ic_ambulance
    CardType.GOLDEN -> R.drawable.ic_golden
    CardType.MAP -> R.drawable.ic_map_square
    CardType.COMPASS -> R.drawable.ic_compass_square
    CardType.KEY -> R.drawable.ic_key_square
    CardType.SKIRT -> R.drawable.ic_skirt_square
    CardType.SHOES -> R.drawable.ic_shoes_square
    CardType.CARRIAGE -> R.drawable.ic_carriage_square
    CardType.PEGASUS -> R.drawable.ic_pegasus_square
    CardType.SWORD -> R.drawable.ic_sword_square
    CardType.ARMOR -> R.drawable.ic_armor_square
    CardType.INSERT_LEFT -> R.drawable.ic_card_add
    CardType.INSERT_RIGHT -> R.drawable.ic_card_add
    else -> 0
}

fun stepResource(step: Int) = when (step) {
    2 -> CardType.PARAM2.card
    3 -> CardType.PARAM3.card
    4 -> CardType.PARAM4.card
    5 -> CardType.PARAM5.card
    else -> null
}

fun stepResource(cardType: CardType) = when (cardType) {
    CardType.PARAM2 -> 2
    CardType.PARAM3 -> 3
    CardType.PARAM4 -> 4
    CardType.PARAM5 -> 5
    else -> 1
}

fun cardSelector(card: CardType): Int = when (card) {
    CardType.FORWARD -> R.drawable.selector_upward
    CardType.BACKWARD -> R.drawable.selector_downward
    CardType.LEFT -> R.drawable.selector_left
    CardType.RIGHT -> R.drawable.selector_right
    CardType.LOOP -> R.drawable.selector_loop
    CardType.CLOSURE -> R.drawable.selector_closure
    CardType.PARAM1 -> R.drawable.selector_plus_1
    CardType.PARAM2 -> R.drawable.selector_plus_2
    CardType.PARAM3 -> R.drawable.selector_plus_3
    CardType.PARAM4 -> R.drawable.selector_plus_4
    CardType.PARAM5 -> R.drawable.selector_plus_5
    CardType.FIRE_TRUCK -> R.drawable.selector_fire_truck
    CardType.POLICE_CAR -> R.drawable.selector_police_car
    CardType.AMBULANCE -> R.drawable.selector_ambulance
    CardType.GOLDEN -> R.drawable.selector_golden
    CardType.MAP -> R.drawable.selector_map_square
    CardType.COMPASS -> R.drawable.selector_compass_square
    CardType.KEY -> R.drawable.selector_key_square
    CardType.SKIRT -> R.drawable.selector_skirt_square
    CardType.SHOES -> R.drawable.selector_shoes_square
    CardType.CARRIAGE -> R.drawable.selector_carriage_square
    CardType.PEGASUS -> R.drawable.selector_pegasus_square
    CardType.SWORD -> R.drawable.selector_sword_square
    CardType.ARMOR -> R.drawable.selector_armor_square
    CardType.INSERT_LEFT -> R.drawable.ic_card_add
    CardType.INSERT_RIGHT -> R.drawable.ic_card_add
    else -> 0
}