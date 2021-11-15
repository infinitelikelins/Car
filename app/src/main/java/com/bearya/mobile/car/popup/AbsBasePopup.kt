package com.bearya.mobile.car.popup

import android.content.Context
import android.view.animation.Animation
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AlphaConfig
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.ScaleConfig

abstract class AbsBasePopup(context: Context?) : BasePopupWindow(context) {

    override fun onCreateShowAnimation(): Animation =
        AnimationHelper.asAnimation()
            .withScale(ScaleConfig.CENTER.duration(100L))
            .withAlpha(AlphaConfig.IN.duration(100L))
            .toShow()

    override fun onCreateDismissAnimation(): Animation =
        AnimationHelper.asAnimation()
            .withScale(ScaleConfig.CENTER.duration(100L))
            .withAlpha(AlphaConfig.OUT.duration(100L))
            .toDismiss()

}