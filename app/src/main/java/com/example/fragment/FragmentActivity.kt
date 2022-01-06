package com.example.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.recyclerview.RecyclerViewFragment
import com.example.test.R
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlin.math.abs

/**
 * @author wanglun
 * @date 2021/09/03
 * @description
 */
class FragmentActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        initViewPager()
    }

    private val savedStateViewModel: SavedStateViewModel by viewModels()

    private val adapter = Adapter(this)

    private fun initViewPager() {
        viewPager.adapter = adapter
        viewPager.currentItem = savedStateViewModel.getIndex()
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                savedStateViewModel.setIndex(position)
            }
        })
    }

    private inner class Adapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        private val fragments = mutableListOf<Fragment>().apply {
            add(RecyclerViewFragment())
            add(ColorFragment(2, Color.RED))
            add(ColorFragment(3, Color.GRAY))
            add(ColorFragment(4, Color.GREEN))
            add(ColorFragment(1, Color.YELLOW))
        }

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    private var downX = 0f
    private var downY = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var xYRatio = 100f

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return super.dispatchTouchEvent(ev)
        val currX = ev.x
        val currY = ev.y
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = currX
                downY = currY
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs((currX - downX)/(currY - downY)) > xYRatio) {
                    ev.offsetLocation(0f, lastY - currY)
                } else {
                    ev.offsetLocation(lastX - currX, 0f)
                }
            }
        }
        lastX = currX
        lastY = currY
        return super.dispatchTouchEvent(ev)
    }

    class SavedStateViewModel(private val savedStateHandle: SavedStateHandle): ViewModel(){
        private val indexKey = "indexKey"
        private val initValue = 0

        init {
            savedStateHandle.set(indexKey, initValue)
        }

        fun getIndex(): Int {
            var value = savedStateHandle.get<Int>(indexKey)
            if (value == null){
                savedStateHandle.set(indexKey, initValue)
                value = initValue
            }
            return value
        }

        fun setIndex(index: Int){
            savedStateHandle.set(indexKey, index)
        }

    }
}