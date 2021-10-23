package com.alexac.mistensiones

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        //MANTIENE EL SPLASH SCREEN DURANTE 2 SEGUNDOS
        Thread.sleep(2000)
        setTheme(R.style.Apptheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
    }
}