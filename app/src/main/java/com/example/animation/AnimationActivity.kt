package com.example.animation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
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
import com.example.test.R
import com.example.utils.dp2Px
import kotlinx.android.synthetic.main.activity_animation.*

/**
 * @author wanglun
 * @date 2021/08/05
 * @description
 */
class AnimationActivity : AppCompatActivity() {
    private var popWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)
        initView()
    }

    private fun initView() {
        initImageView()
        playScaleAnimationWithObjectAnimation()
        tipsView.post {
            showTipPopWindow()
        }
    }

    private fun initImageView() {
        loadImageInDensity()
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

        popWindow?.let {
            if (!isFinishing && !it.isShowing) {
                it.showAsDropDown(tipsView, dp2Px(this, -110),10)
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

    private fun loadImageInDensity() {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.mipmap.high_quality_pic, options)
        val srcWidth = options.outWidth
        val srcHeight = options.outHeight

        val newOptions = BitmapFactory.Options()
        newOptions.inScaled = true
        newOptions.inDensity = srcWidth
        newOptions.inTargetDensity = srcWidth / 8

        val bitmap =
            BitmapFactory.decodeResource(resources, R.mipmap.high_quality_pic, newOptions)
        iv_pic.setImageBitmap(bitmap)
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

    private fun playScaleAnimationWithObjectAnimation() {
        val holder1 = PropertyValuesHolder.ofFloat("rotationX", 0f, 360f)
        val holder2 = PropertyValuesHolder.ofFloat("rotationY", 0f, 360f)
        val holder3 = PropertyValuesHolder.ofFloat("rotation", 0f, 360f)
        val anim = ObjectAnimator.ofPropertyValuesHolder(iv_pic, holder1, holder2, holder3)
        anim.interpolator = LinearInterpolator()
        anim.duration = 2000
//        anim.repeatCount = ValueAnimator.INFINITE
//        anim.repeatMode = ValueAnimator.RESTART
        anim.start()
    }

}