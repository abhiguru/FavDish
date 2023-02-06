package `in`.tutorial.favdish

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import `in`.tutorial.favdish.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    var binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        binding?.tvAppName?.animation = splashAnimation
        splashAnimation.setAnimationListener(object:AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }, 700)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }
}