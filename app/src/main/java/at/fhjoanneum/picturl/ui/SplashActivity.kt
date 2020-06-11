package at.fhjoanneum.picturl.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import at.fhjoanneum.picturl.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }, 300)
    }
}