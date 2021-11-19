package com.bearya.mobile.car.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bearya.mobile.car.databinding.FragmentLottieBinding

class LottieFragment : Fragment() {

    companion object {
        fun newInstance(lottieName: String): LottieFragment =
            LottieFragment().apply { arguments = bundleOf("lottieName" to lottieName) }
    }

    private lateinit var bindView: FragmentLottieBinding
    private var lottieName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lottieName = arguments?.getString("lottieName", "lottie/normal.json")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindView = FragmentLottieBinding.inflate(inflater, container, false)
        return bindView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView.emotionLottie.setAnimation(lottieName)
    }

    override fun onResume() {
        super.onResume()
        bindView.emotionLottie.playAnimation()
    }

    override fun onPause() {
        super.onPause()
        bindView.emotionLottie.cancelAnimation()
    }

}