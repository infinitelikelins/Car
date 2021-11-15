package com.bearya.mobile.car.repository

import com.bearya.mobile.car.R

enum class ImageType {
    Lottie, Frame
}

fun frameRepository(params: String): Pair<ImageType, Int>? = when (params) {
    "e0010106" -> Pair(ImageType.Frame, R.array.spider_fail)
    "e0010107" -> Pair(ImageType.Frame, R.array.spider_success)
    "e0010108" -> Pair(ImageType.Frame, R.array.crocodile_fail)
    "e0010109" -> Pair(ImageType.Frame, R.array.crocodile_success)
    "e001010A", "e001010a" -> Pair(ImageType.Frame, R.array.zombie_fail)
    "e001010B", "e001010b" -> Pair(ImageType.Frame, R.array.zombie_success)
    "e001010C", "e001010c" -> Pair(ImageType.Frame, R.array.cat_fail)
    "e001010D", "e001010d" -> Pair(ImageType.Frame, R.array.cat_success)
    "e001010E", "e001010e" -> Pair(ImageType.Frame, R.array.monster_fail)
    "e001010F", "e001010f" -> Pair(ImageType.Frame, R.array.monster_success)
    "e0010110" -> Pair(ImageType.Frame, R.array.witch_fail)
    "e0010111" -> Pair(ImageType.Frame, R.array.witch_success)
    "e0010112" -> Pair(ImageType.Frame, R.array.tree_fail)
    "e0010113" -> Pair(ImageType.Frame, R.array.tree_success)
    "e0010114" -> Pair(ImageType.Frame, R.array.flower_fail)
    "e0010115" -> Pair(ImageType.Frame, R.array.flower_success)
    "e0010116" -> Pair(ImageType.Frame, R.array.volcano)
    "e0010117" -> Pair(ImageType.Frame, R.array.treasure)
    "e0010118" -> Pair(ImageType.Frame, R.array.compass)
    "e0010119" -> Pair(ImageType.Frame, R.array.key)
    "e001011A", "e001011a" -> Pair(ImageType.Frame, R.array.skirt)
    "e001011B", "e001011b" -> Pair(ImageType.Frame, R.array.carriage)
    "e001011C", "e001011c" -> Pair(ImageType.Frame, R.array.shoes)
    "e001011D", "e001011d" -> Pair(ImageType.Frame, R.array.armor)
    "e001011E", "e001011e" -> Pair(ImageType.Frame, R.array.sword)
    "e001011F", "e001011f" -> Pair(ImageType.Frame, R.array.pegasus)
    "e0010120" -> Pair(ImageType.Frame, R.array.gold_coin)
    "e0010121" -> Pair(ImageType.Frame, R.array.lost_load)
    "e0010122" -> Pair(ImageType.Frame, R.array.fire)
    "e0010123" -> Pair(ImageType.Frame, R.array.guards)
    "e0010124" -> Pair(ImageType.Frame, R.array.dance_end)
    "e0010125" -> Pair(ImageType.Frame, R.array.seafloor_end)
    "e0010126" -> Pair(ImageType.Frame, R.array.fair_end)
    "e0010128" -> Pair(
        ImageType.Frame, listOf(
            R.array.game_success_home_1,
            R.array.game_success_home_2,
            R.array.game_success_home_3,
            R.array.game_success_school_1,
            R.array.game_success_school_2,
            R.array.game_success_school_3,
            R.array.game_success_zoo_1,
            R.array.game_success_zoo_2,
            R.array.game_success_zoo_3,
            R.array.game_success_park_1,
            R.array.game_success_park_2,
            R.array.game_success_park_3,
            R.array.game_success_library_1,
            R.array.game_success_library_2,
            R.array.game_success_library_3,
        )[(0 until 15).random()]
    )
    else -> null
}

fun emotionRepository(params: String): Pair<ImageType, String>? = when (params) {
    "e0010101" -> Pair(ImageType.Lottie, "lottie/normal.json")
    "e0010102" -> Pair(ImageType.Lottie, "lottie/sh.json")
    "e0010103" -> Pair(ImageType.Lottie, "lottie/hg.json")
    "e0010104" -> Pair(ImageType.Lottie, "lottie/axy.json")
    "e0010105" -> Pair(ImageType.Lottie, "lottie/pain.json")
    "e0010127" -> Pair(ImageType.Lottie, "lottie/ja.json")
    else -> null
}