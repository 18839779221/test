package com.example.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_color.*

/**
 * @author wanglun
 * @date 2021/09/03
 * @description
 */
class ColorFragment(
    private val index: Int,
    private val color: Int,
) : Fragment() {

    private val savedStateViewModel: SavedStateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_color, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("lifecycleLog", "onViewCreated,index=$index")

        ivColor.setImageDrawable(ColorDrawable(color))
        tvIndex.text = "$index"
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.e("lifecycleLog", "onHiddenChanged($hidden),index=$index")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("lifecycleLog", "onCreate,index=$index")
        Log.e("savestate", "onCreate,"+savedStateViewModel.getRefreshKey()+"  $index")
    }

    override fun onResume() {
        super.onResume()
        Log.e("lifecycleLog", "onResume,index=$index,${toString()}")
    }

    override fun onStart() {
        super.onStart()
        Log.e("lifecycleLog", "onStart,index=$index")
    }

    override fun onPause() {
        super.onPause()
        Log.e("lifecycleLog", "onPause,index=$index")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("lifecycleLog", "onDestroy,index=$index")
        Log.e("savestate", "onDestroy,"+savedStateViewModel.getRefreshKey()+"  $index")
    }

    class SavedStateViewModel(private val savedStateHandle: SavedStateHandle): ViewModel(){
        private val refreshKey = "refreshKey"
        private val initValue = toString()

        init {
            savedStateHandle.set(refreshKey, initValue)
        }

        fun getRefreshKey(): String {
            var value = savedStateHandle.get<String>(refreshKey)
            if (value == null){
                savedStateHandle.set(refreshKey, initValue)
                value = initValue
            }
            return value
        }

    }
}