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
                        Toast.makeText(this, "ACCESO REALIZADO CON ÉXITO", Toast.LENGTH_SHORT).show()
                        goPrincipalActivity(edit_text_email.text.toString(), edit_text_password.text.toString())
                    }else{
                        mostrarError(it.exception.toString())
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

    private fun mostrarError(exception: String){
        when(exception) {
            "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted." ->
                Toast.makeText(this, "Correo electrónico incorrecto, Reviselo, por favor.",Toast.LENGTH_SHORT).show()
            "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account." ->
                Toast.makeText(this, "El usuario introducido ya está registrado.", Toast.LENGTH_SHORT).show()
            "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password." ->
                Toast.makeText(this, "Contraseña incorrecta.",Toast.LENGTH_SHORT).show()
            "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted." ->
                Toast.makeText(this, "Usuario no registrado.",Toast.LENGTH_SHORT).show()
            "com.google.firebase.FirebaseTooManyRequestsException: We have blocked all requests from this device due to unusual activity. Try again later. [ Access to this account has been temporarily " +
                    "disabled due to many failed login attempts. You can immediately restore it by resetting your password or you can try again later. ]" ->
                Toast.makeText(this, "Usuario bloqueado. Contacte con el administrador.",Toast.LENGTH_SHORT).show()
        }
        //Toast.makeText(this, exception, Toast.LENGTH_SHORT).show()
    }

}
