package com.example.animation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.test.R
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.fragment_left.*

/**
 * @author wanglun
 * @date 2021/11/12
 * @description
 */
class LeftFragment: Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_left, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }

        card1.setOnClickListener {
            onCardItemClick(it, iv_card1, tv_card1_title, R.drawable.frozen, tv_card1_title.text.toString())
        }
        card2.setOnClickListener {
            onCardItemClick(it,iv_card2, tv_card2_title, R.drawable.view1, tv_card2_title.text.toString())
        }
        card3.setOnClickListener {
            onCardItemClick(it,iv_card3, tv_card3_title, R.drawable.view2, tv_card3_title.text.toString())
        }
        card4.setOnClickListener {
            onCardItemClick(it,iv_card4, tv_card4_title, R.drawable.view3, tv_card4_title.text.toString())
        }
        card5.setOnClickListener {
            onCardItemClick(it,iv_card5, tv_card5_title, R.drawable.view4, tv_card5_title.text.toString())
        }

    }

    private fun onCardItemClick(view: View,iv: ImageView, tv: TextView, imageResId: Int, title: String) {
        val extra = FragmentNavigatorExtras(
            view to getString(R.string.trans_card_detail),
//            iv to getString(R.string.trans_imageview),
//            tv to getString(R.string.trans_textview)
        )
        findNavController().navigate(LeftFragmentDirections.leftFragmentToRightFragment(title, imageResId), extra)
    }
}