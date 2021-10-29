package com.example.coordinatorlayout.multinested

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_bottom.*

/**
 * @author wanglun
 * @date 2021/09/26
 * @description
 */
class BottomFragment: Fragment() {

    companion object {
        fun newInstance(): BottomFragment{
            val args = Bundle()

            val fragment = BottomFragment()
            fragment.arguments = args
            return fragment
        }
    }


    private val bottomAdapter = FBottomAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
//        rvfTop.apply {
//            adapter = topAdapter
//            layoutManager = LinearLayoutManager(context)
//        }
        rvfBottom.apply {
            adapter = bottomAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }


}