package com.example.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author wanglun
 * @date 2022/05/05
 * @description
 */
class CoroutineActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startCoroutine()
    }

    private fun startCoroutine() {
        // funTest协程体
        val funTest: suspend CoroutineScope.() -> Unit = {
            println("funTest")
            suspendFun1()
            suspendFun2()
        }
        GlobalScope.launch(Dispatchers.Default, block = funTest)
    }

    // 挂起函数
    suspend fun suspendFun1() {
        println("suspendFun1")
    }
    // 挂起函数
    suspend fun suspendFun2() {
        println("suspendFun2")
    }
}