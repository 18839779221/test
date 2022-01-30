package com.example.animation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
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
        findNavController(R.id.nav_host_fragment).navigate(LeftFragmentDirections.actionGlobalLeftFragment())
    }

}