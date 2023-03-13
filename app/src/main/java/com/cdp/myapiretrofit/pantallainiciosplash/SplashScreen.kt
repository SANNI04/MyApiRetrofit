package com.cdp.myapiretrofit.pantallainiciosplash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.cdp.myapiretrofit.Login
import com.cdp.myapiretrofit.R


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val intent = Intent(this@SplashScreen, Login::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}