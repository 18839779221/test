package com.example.audiorecord

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.android.synthetic.main.activity_audio_record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.buffer
import okio.source
import java.io.File
import java.io.FileOutputStream

/**
 * @author wanglun
 * @date 2022/08/04
 * @description
 */
class AudioRecordActivity : AppCompatActivity() {

    private val bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

    private val audioRecord: AudioRecord by lazy {
        AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
    }

    private var audioTrack: AudioTrack? = null

    private val savePath by lazy {
        "$externalCacheDir/audio"
    }

    private var currentFile = File("")

    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_record)
        prepare()
        btnRecord.setOnClickListener {
            if (isRecording) return@setOnClickListener
            audioRecord.startRecording()
            isRecording = true
            currentFile = File(savePath, "${System.currentTimeMillis()}.pcm")
            tvFilePath.text = currentFile.absolutePath
            lifecycleScope.launch(Dispatchers.IO) {
                if (!currentFile.exists()) {
                    currentFile.createNewFile()
                }
                val os = FileOutputStream(currentFile)
                kotlin.runCatching {
                    while (isRecording) {
                        val buffer = ByteArray(bufferSize)
                        val read = audioRecord.read(buffer, 0, bufferSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            os.write(buffer)
                        }
                    }
                }
                os.close()
            }
        }
        btnStop.setOnClickListener {
            if (!isRecording) return@setOnClickListener
            isRecording = false
            audioRecord.stop()
            Toast.makeText(this, "录制完成，请在对应路径查看输出文件", Toast.LENGTH_SHORT).show()
        }
        btnPlay.setOnClickListener {
            if (isRecording) {
                Toast.makeText(this, "正在录制音频，请先结束录制", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            play()
        }
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
    }

    private fun prepare() {
        val dir = File(savePath)
        if (!dir.exists()) {
            dir.mkdir()
        }
    }

    private fun play() {
        btnPlay.isEnabled = false
        audioTrack?.stop()
        audioTrack?.release()
        val len = currentFile.length().toInt()
        if (len == 0) return
        val source = currentFile.source()
        val buffer = source.buffer()
        val audioData: ShortArray = buffer.readByteArray().asList()
            .chunked(2)
            .map { (l, h) -> (l.toInt() + h shl 8).toShort() }
            .toShortArray()
        audioTrack = AudioTrack.Builder()
            .setAudioFormat(AudioFormat.Builder()
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(44100).build())
            .setBufferSizeInBytes(len)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
            .apply {
                play()
                write(audioData, 0, len)
            }


        btnPlay.isEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.any { it == PackageManager.PERMISSION_DENIED }) {
            Toast.makeText(this, "请确保已授予正确的权限", Toast.LENGTH_SHORT).show()
        }
    }
}