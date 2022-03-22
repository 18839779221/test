package com.example

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * @author wanglun
 * @date 2022/03/04
 * @description
 */
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }
}