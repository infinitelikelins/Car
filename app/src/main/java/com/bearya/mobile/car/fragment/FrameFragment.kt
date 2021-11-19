package com.bearya.mobile.car.fragment

import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bearya.mobile.car.databinding.FragmentFrameBinding

class FrameFragment : Fragment() {

    companion object {
        fun newInstance(drawableResArrays: Int): FrameFragment =
            FrameFragment().apply { arguments = bundleOf("drawableResArrays" to drawableResArrays) }
    }

    private lateinit var bindView: FragmentFrameBinding
    private var frameRes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        frameRes = arguments?.getInt("drawableResArrays", 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindView = FragmentFrameBinding.inflate(inflater, container, false)
        return bindView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (frameRes != 0) {
            val frame = getData(frameRes)
            bindView.frameAnimator.mBitmapResourceIds = frame
            bindView.frameAnimator.mIsRepeat = false
            bindView.frameAnimator.mGapTime = 25
        }
    }

    override fun onResume() {
        super.onResume()
        bindView.frameAnimator.start()
    }

    override fun onStop() {
        super.onStop()
        bindView.frameAnimator.stop()
    }

    /**
     * 从xml中读取帧数组
     */
    private fun getData(resId: Int): IntArray {
        val array: TypedArray = resources.obtainTypedArray(resId)
        val len = array.length()
        val intArray = IntArray(array.length())
        for (i in 0 until len) {
            intArray[i] = array.getResourceId(i, 0)
        }
        array.recycle()
        return intArray
    }

}