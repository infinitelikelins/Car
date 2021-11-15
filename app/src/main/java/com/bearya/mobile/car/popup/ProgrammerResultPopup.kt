package com.bearya.mobile.car.popup

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import com.bearya.mobile.car.R

class ProgrammerResultPopup(context: Context?, result: Triple<String, String, String>) :
    AbsBasePopup(context) {

    init {
        setContentView(R.layout.popup_result_programmer)
        findViewById<AppCompatTextView>(R.id.step_count).text = result.first
        findViewById<AppCompatTextView>(R.id.prop_count).text = result.second
        findViewById<AppCompatTextView>(R.id.known_count).text = result.third
        setOutSideDismiss(false)
        isOutSideTouchable = false
    }

}