package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.animation.AnimationActivity
import com.example.sharedflow.SFActivity
import com.example.viewgroup.GroupActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        my_view.onClick = {
            //ll_myview.scrollTo(0,ll_myview.scrollY+100)
            Toast.makeText(this, "onClick", Toast.LENGTH_SHORT).show()

        }

        my_view.onScroll = { deltaX: Int, deltaY: Int ->
            var x = deltaX
            var y = deltaY
            if (deltaY < 0) {
                if (ll_myview.scrollY + deltaY < 0) y = -ll_myview.scrollY
            } else {
                if (ll_myview.scrollY + deltaY + 500 >= my_view.height) y =
                    my_view.height - ll_myview.scrollY - 500
            }
            Toast.makeText(this, "x=$x,y=$y", Toast.LENGTH_SHORT).show()
            ll_myview.scrollBy(x, y)
        }
    }

    fun toNetActivity(v: View) {
        val intent = Intent(this,NetActivity::class.java)
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

}