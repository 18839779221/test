package com.example

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.animation.AnimationActivity
import com.example.animation.FragmentSwitchActivity
import com.example.biometric.BiometricActivity
import com.example.coordinatorlayout.multinested.MultiNestedActivity
import com.example.fragment.FragmentActivity
import com.example.gpu.overdraw.OverDrawActivity
import com.example.launchmode.LaunchModeActivity
import com.example.nestedscroll.NestedScrollActivity
import com.example.recyclerview.RecyclerViewActivity
import com.example.ruler.RulerActivity
import com.example.scroll.ScrollActivity
import com.example.shadow.ShadowActivity
import com.example.sharedflow.SFActivity
import com.example.table.TableActivity
import com.example.test.NetActivity
import com.example.test.R
import com.example.viewgroup.GroupActivity
import com.example.window.MessageQueueWindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun toNetActivity(v: View) {
        val intent = Intent(this, NetActivity::class.java)
        startActivity(intent)
   }

    fun toSFActivity(v: View){
        val intent = Intent(this,SFActivity::class.java)
        startActivity(intent)
    }

    fun toAnimationActivity(v: View){
        val intent = Intent(this,AnimationActivity::class.java)
        startActivity(intent)
    }

    fun toGroupActivity(v: View){
        val intent = Intent(this, GroupActivity::class.java)
        startActivity(intent)
    }

    fun toRulerActivity(v: View){
        val intent = Intent(this, RulerActivity::class.java)
        startActivity(intent)
    }

    fun toScrollActivity(v: View){
        val intent = Intent(this, ScrollActivity::class.java)
        startActivity(intent)
    }

    fun toNestedScrollActivity(v: View){
        val intent = Intent(this, NestedScrollActivity::class.java)
        startActivity(intent)
    }

    fun toFragmentActivity(v: View){
        val intent = Intent(this, FragmentActivity::class.java)
        startActivity(intent)
    }

    fun toShadowActivity(v: View){
        val intent = Intent(this, ShadowActivity::class.java)
        startActivity(intent)
    }

    fun toMultiNestedActivity(v: View){
        val intent = Intent(this, MultiNestedActivity::class.java)
        startActivity(intent)
    }

    fun toRecyclerViewActivity(v: View){
        val intent = Intent(this, RecyclerViewActivity::class.java)
        startActivity(intent)
    }

    fun toFragmentSwitchActivity(v: View){
        val intent = Intent(this, FragmentSwitchActivity::class.java)
        startActivity(intent)
    }

    fun toOverDrawActivity(v: View){
        val intent = Intent(this, OverDrawActivity::class.java)
        startActivity(intent)
    }

    fun showMessageQueueView(v: View) {
        MessageQueueWindowManager.addMessageQueueWindow(this)
    }

    fun toLaunchModeActivity(v: View) {
        val intent = Intent(this, LaunchModeActivity::class.java)
        startActivity(intent)
    }

    fun toTableActivity(v: View) {
        val intent = Intent(this, TableActivity::class.java)
        startActivity(intent)
    }

    fun toBiometricActivity(v: View) {
        val intent = Intent(this, BiometricActivity::class.java)
        startActivity(intent)
    }


}