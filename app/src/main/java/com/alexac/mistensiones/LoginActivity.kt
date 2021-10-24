package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_activity.*

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
        text_view_registrese.setOnClickListener {
            intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        button_acceder.setOnClickListener {
            if (edit_text_email.text.isNotEmpty() && edit_text_password.text.isNotEmpty()){

                FirebaseAuth.getInstance().signInWithEmailAndPassword(edit_text_email.text.toString(), edit_text_password.text.toString()).addOnCompleteListener{

                    if(it.isSuccessful){
                        Toast.makeText(this, "REGISTRO REALIZADO CON ÉXITO", Toast.LENGTH_SHORT).show()
                        goPrincipalActivity(edit_text_email.text.toString(), edit_text_password.text.toString())
                    }else{

                        mostrarError()
                    }
                }

            }
        }

    }

    private fun goPrincipalActivity(email: String, password: String){
        val pantallaPrincipalIntent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("email", email)
            putExtra("password", password)
        }

        startActivity(pantallaPrincipalIntent)
    }

    private fun mostrarError(){
        Toast.makeText(this, "ERROR AL HACER LOGIN, REVISE LOS DATOS", Toast.LENGTH_SHORT).show()
    }

}
