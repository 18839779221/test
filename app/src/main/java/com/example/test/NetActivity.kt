package com.example.test

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_net.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okio.buffer
import okio.sink
import okio.source
import java.io.File

/**
 * @author wanglun
 * @date 2021/07/21
 * @description
 */
class NetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net)
        initView()
    }

    private fun initView() {
        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        )
        ActivityCompat.requestPermissions(this, permissions, 222)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startOperateFile()
    }

    private fun startOperateFile() {
        Thread {
            val file =
                File(Environment.getExternalStoragePublicDirectory("").absolutePath + "/a.txt")
            if (!file.exists()) {
                file.createNewFile()
            }
            writeFile(file)
            val content = readFileContent(file) ?: return@Thread
            runOnUiThread {
                tv_file.text = content
            }
        }.start()
        GlobalScope.launch {

        }
    }


    private fun writeFile(file: File) {
        file.sink(false)
            .buffer()
            .writeUtf8("write string by utf-8.\n")
            .writeInt(1234)
            .close()
    }

    private fun readFileContent(file: File): String? {
        return file.source()
            .buffer()
            .readUtf8()

    }


}