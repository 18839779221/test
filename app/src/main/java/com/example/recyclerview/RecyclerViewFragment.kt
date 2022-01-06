package com.example.recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_recyclerview.*

/**
 * @author wanglun
 * @date 2021/10/29
 * @description
 */
class RecyclerViewFragment : Fragment() {

    private val contentAdapter = ContentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        rvContent.apply {
            adapter = contentAdapter
            layoutManager = LinearLayoutManager(context)
        }
        contentAdapter.submitList(listOf(User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom"),
            User("1", "bob"),
            User("2", "Tom")))
    }

}