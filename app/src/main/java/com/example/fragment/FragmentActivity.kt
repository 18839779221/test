package com.example.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.test.R
import kotlinx.android.synthetic.main.activity_fragment.*

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
            add(ColorFragment(0, Color.BLUE))
            add(ColorFragment(1, Color.YELLOW))
            add(ColorFragment(2, Color.RED))
            add(ColorFragment(3, Color.GRAY))
            add(ColorFragment(4, Color.GREEN))

        }

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
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