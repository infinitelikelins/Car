package com.bearya.mobile.car.popup

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import com.bearya.mobile.car.R

class FairyResultPopup(context: Context?, result: Pair<String, String>) :
    AbsBasePopup(context) {

    init {
        setContentView(R.layout.popup_result_fairy)
        findViewById<AppCompatTextView>(R.id.step_count).text = result.first
        findViewById<AppCompatTextView>(R.id.prop_count).text = result.second
        setOutSideDismiss(false)
        isOutSideTouchable = false
    }

}