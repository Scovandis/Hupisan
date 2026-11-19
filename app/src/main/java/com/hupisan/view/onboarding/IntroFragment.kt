package com.hupisan.view.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hupisan.databinding.FragmentIntroBinding


class IntroFragment : Fragment() {
    private var title: String? = null
    private var description: String? = null
    private var lottieAnimation: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null){
            title = requireArguments().getString(ARGS_PARAMS1)
            description = requireArguments().getString(ARGS_PARAMS2)
            lottieAnimation = requireArguments().getInt(ARGS_PARAMS3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentIntroBinding.inflate(inflater,container, false)
        val view = binding.root


        binding.tvTitle.text = title
        binding.tvDesc.text = description
        binding.lottieAnimation.setAnimation(lottieAnimation!!)

        return view
    }

    companion object {
        private const val ARGS_PARAMS1 = "params1"
        private const val ARGS_PARAMS2 = "params2"
        private const val ARGS_PARAMS3 = "params3"

        @JvmStatic
        fun newInstance(
            title: String,
            deskription: String,
            animationLottie: Int
        ) =
            IntroFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGS_PARAMS1, title)
                    putString(ARGS_PARAMS2, deskription)
                    putInt(ARGS_PARAMS3, animationLottie)
                }
            }
    }
}