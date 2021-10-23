package com.alexac.mistensiones

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        //MANTIENE EL SPLASH SCREEN DURANTE 2 SEGUNDOS
        Thread.sleep(2000)
        setTheme(R.style.Apptheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        setup()

    }

    private fun setup() {

    }

}
