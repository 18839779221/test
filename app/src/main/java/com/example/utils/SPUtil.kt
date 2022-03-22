package com.example.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.BaseApplication

/**
 * 保存到本地的配置文件
 *
 *
 */
object SPUtil {

    val commonSP by lazy {
        BaseApplication.context!!.getSharedPreferences("common_sp", Context.MODE_PRIVATE)
    }
    val accountSP by lazy {
        BaseApplication.context!!.getSharedPreferences("account_sp", Context.MODE_PRIVATE)
    }
    /**
     * SharedPreferences常用的10个操作方法
     */
    fun putBoolean(key: String, value: Boolean, sp: SharedPreferences = commonSP) {
        sp.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean, sp: SharedPreferences = commonSP): Boolean {
        return sp.getBoolean(key, defValue)
    }

    fun putString(key: String, value: String?, sp: SharedPreferences = commonSP) {
        sp.edit().putString(key, value).apply()
    }

    fun getString(key: String, defValue: String?, sp: SharedPreferences = commonSP): String? {
        return sp.getString(key, defValue)
    }

    fun putInt(key: String, value: Int, sp: SharedPreferences = commonSP) {
        sp.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defValue: Int, sp: SharedPreferences = commonSP): Int {
        return sp.getInt(key, defValue)
    }

    /**
     * 移除某个key值已经对应的值
     */
    fun remove(key: String, sp: SharedPreferences = commonSP) {
        sp.edit().remove(key).apply()
    }

    /**
     * 清除所有内容
     */
    fun clear(sp: SharedPreferences = commonSP) {
        sp.edit().clear().apply()
    }
}