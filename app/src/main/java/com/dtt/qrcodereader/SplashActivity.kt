package com.dtt.qrcodereader

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        this.enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tvAppTitle = findViewById<TextView>(R.id.tvAppTitle)
        val ivAppIcon = findViewById<ImageView>(R.id.ivAppIcon)

        val fadeInAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha_animation)
        fadeInAnimation.duration = 2000

        tvAppTitle.startAnimation(fadeInAnimation)

        tvAppTitle.postDelayed({
            ivAppIcon.startAnimation(fadeInAnimation)
            ivAppIcon.visibility = android.view.View.VISIBLE
        }, 2000)

        ivAppIcon.postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)

    }
}
