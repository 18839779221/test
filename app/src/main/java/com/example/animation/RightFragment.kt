package com.example.animation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.test.R
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_right.*

/**
 * @author wanglun
 * @date 2021/11/12
 * @description
 */
class RightFragment: Fragment() {

    private val args: RightFragmentArgs by navArgs()
    private val title by lazy<String> {
        args.title
    }
    private val imageResId by lazy<Int> {
        args.imageResId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            // Scope the transition to a view in the hierarchy so we know it will be added under
            // the bottom app bar but over the elevation scale of the exiting HomeFragment.
            drawingViewId = R.id.nav_host_fragment
            duration = 300L
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(Color.rgb(12,12,12))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_right, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        tvTitle.text = title
        ivPic.setImageResource(imageResId)
    }
}