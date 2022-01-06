package com.example.animation

import android.animation.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.core.view.drawToBitmap
import com.example.test.R
import com.example.utils.dp2Px
import kotlinx.android.synthetic.main.activity_animation.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import kotlin.math.tan
import kotlin.random.Random

/**
 * @author wanglun
 * @date 2021/08/05
 * @description
 */
class AnimationActivity : AppCompatActivity() {
    private var popWindow: PopupWindow? = null
    val picW = 300
    val picH = 500
    val bgW = picW * 4
    val bgH = (picH * 2).toInt()
    val bgPixels = IntArray(bgW * bgH)
    val bgAnglePixels = IntArray(bgW * bgH)
    var currLeft = picW-1
    val dx = 10
    var dy = 0
    lateinit var bgBitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)
        initView()
    }

    private fun initView() {
        initImageView()
        tipsView.post {
            showTipPopWindow()
        }
    }

    private fun initImageView() {
//        iv_pic.setImageBitmap(loadBitmapInDensity(R.mipmap.high_quality_pic))
//        iv_pic2.setImageBitmap(loadBitmapInDensity(R.drawable.frozen))
//
//        playScaleAnimationWithObjectAnimation(iv_pic)
//        playScaleAnimationWithObjectAnimation(iv_pic2)

        initBitmapAnim()
    }

    private fun initBitmapAnim() {

        val bitmap =
            Bitmap.createBitmap(loadBitmapInDensity(R.mipmap.high_quality_pic), 0, 0, picW, picH)

        val picPixels = IntArray(picW * picH)
        bitmap.getPixels(picPixels, 0, picW, 0, 0, picW, picH)
        bgBitmap = createBitmap(bgW, bgH)

        bgBitmap.setPixels(picPixels, 0, picW, 0, (bgH * 0.5).toInt(), picW, picH)

        bgBitmap.getPixels(bgPixels, 0, bgW, 0, 0, bgW, bgH)

        iv_pic.setImageBitmap(bgBitmap)
        iv_pic2.setImageBitmap(bitmap)

        animThread.submit(task)

    }

    private fun showTipPopWindow() {
        if (popWindow == null) {
            val popView = ImageView(this).apply {
                setImageDrawable(ColorDrawable(Color.DKGRAY))
                layoutParams = ViewGroup.LayoutParams(dp2Px(context, 160), dp2Px(context, 60))
            }
            popWindow = PopupWindow(
                popView,
                popView.layoutParams.width,
                popView.layoutParams.height,
                true
            ).apply {
                setBackgroundDrawable(ColorDrawable(0x00000000))
                isTouchable = false
                isFocusable = false
                isOutsideTouchable = true
            }
        }
    }

    private fun loadImageInSampleSize() {
        val options = BitmapFactory.Options()
        options.inSampleSize = 8

        val bitmap =
            BitmapFactory.decodeResource(resources, R.mipmap.high_quality_pic, options)
        iv_pic.setImageBitmap(bitmap)

    }

    private fun loadBitmapInDensity(imgResId: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, imgResId, options)
        val srcWidth = options.outWidth
        val srcHeight = options.outHeight

        val newOptions = BitmapFactory.Options()
        newOptions.inScaled = true
        newOptions.inDensity = srcWidth
        newOptions.inTargetDensity = srcWidth / 8

        return BitmapFactory.decodeResource(resources, imgResId, newOptions)
    }

    private fun playAlphaAnimationWithValueAnimation() {
        val anim = ValueAnimator.ofFloat(1f, 0f)
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener {
            iv_pic.alpha = it.animatedValue as Float
        }
        anim.duration = 2000

        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.REVERSE
        anim.start()
    }

    private val animThread = Executors.newSingleThreadExecutor()

    private val task = Runnable {
        var returnFlag = true
        while (returnFlag) {
            returnFlag = false
            Log.e("bitmappixel","1")
            for (i in 0 until bgH) {
                for (j in (currLeft until bgW).reversed()) {
                    if (bgPixels[i * bgW + j] == 0) continue
                    if (j + dx >= bgW) {
                        bgPixels[i * bgW + j] = 0
                        continue
                    }
                    val random = if (i < bgH / 2) {
                        (-10..10).random()
                    } else {
                        (-10..10).random()
                    }
                    val angle = bgAnglePixels[i * bgW + j] + random * 6
                    val radian = Math.toRadians(angle.toDouble())
                    dy = -(dx * tan(radian)).toInt()
                    if (i + dy < 0 || i + dy >= bgH) continue
                    val targetIndex = (j + dx) + (i + dy) * bgW
                    bgPixels[targetIndex] = bgPixels[i * bgW + j]
                    bgAnglePixels[targetIndex] = bgAnglePixels[i * bgW + j]
                    bgPixels[i * bgW + j] = 0
                    bgAnglePixels[i * bgW + j] = 0
                    returnFlag = true
                    Log.e("bitmappixel","2")
                }
            }
            Log.e("bitmappixel","3")
            currLeft = (currLeft - 4).coerceAtLeast(0)
            bgBitmap.setPixels(bgPixels, 0, bgW, 0, 0, bgW, bgH)
            runOnUiThread {
                iv_pic.setImageBitmap(bgBitmap)
            }
            Thread.sleep(40)
        }

    }

    private fun playScaleAnimationWithObjectAnimation(v: View) {
        //动画硬件加速
        //https://github.com/hehonghui/android-tech-frontier/blob/master/issue-30/%E9%80%9A%E8%BF%87%E7%A1%AC%E4%BB%B6%E5%B1%82%E6%8F%90%E9%AB%98Android%E5%8A%A8%E7%94%BB%E7%9A%84%E6%80%A7%E8%83%BD.md
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val holder1 = PropertyValuesHolder.ofFloat("rotationX", 0f, 360f)
        val holder2 = PropertyValuesHolder.ofFloat("rotationY", 0f, 360f)
        val holder3 = PropertyValuesHolder.ofFloat("rotation", 0f, 360f)
        val anim = ObjectAnimator.ofPropertyValuesHolder(v, holder1, holder2, holder3)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                v.setLayerType(View.LAYER_TYPE_NONE, null)
            }
        })
        anim.interpolator = LinearInterpolator()
        anim.duration = 2000
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
        anim.start()
    }

}