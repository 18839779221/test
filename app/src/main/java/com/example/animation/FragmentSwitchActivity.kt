package com.example.animation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.test.R

/**
 * @author wanglun
 * @date 2021/11/12
 * @description
 */
class FragmentSwitchActivity: AppCompatActivity() {

    private var leftFragment = LeftFragment()
    private var rightFragment = RightFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_switch)
    }


    fun openLeftFragment(v: View?) {
        supportFragmentManager.commit {
            if (leftFragment.isAdded) {
                if (leftFragment.isHidden) {
                    hide(rightFragment)
                    show(leftFragment)
                }
            } else {
                add(R.id.fragmentContainer, leftFragment)
            }
        }
    }

    fun openRightFragment(v: View?) {
        supportFragmentManager.commit {
            if (rightFragment.isAdded) {
                if (rightFragment.isHidden) {
                    hide(leftFragment)
                    show(rightFragment)
                }
            } else {
                add(R.id.fragmentContainer, rightFragment)
            }
        }
    }

}