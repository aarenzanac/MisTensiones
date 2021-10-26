package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.registro_activity.*

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_activity)

        setup()
    }

    private fun setup(){
        button_modificar_datos.setOnClickListener {
            if (edit_text_email_registro.text.isNotEmpty() && edit_text_password_registro.text.isNotEmpty()){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edit_text_email_registro.text.toString(), edit_text_password_registro.text.toString()).addOnCompleteListener{

                    if(it.isSuccessful){
                        Toast.makeText(this, "REGISTRO REALIZADO CON ÉXITO", Toast.LENGTH_SHORT).show()
                        goDatosInicioActivity(edit_text_email_registro.text.toString(), edit_text_password_registro.text.toString())
                    }else{
                        mostrarError(it.exception.toString())
                    }
                }
            }
        }

    }

    private fun goDatosInicioActivity(email: String, password: String){
        val datosInicioIntent = Intent(this, DatosInicioActivity::class.java).apply {
            putExtra("email", email)
            putExtra("password", password)
        }

        startActivity(datosInicioIntent)
    }

    private fun mostrarError(exception: String){
        when(exception) {
            "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted." ->
            Toast.makeText(this, "Correo electrónico incorrecto, Reviselo, por favor.",Toast.LENGTH_SHORT).show()
            "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account." ->
            Toast.makeText(this, "El usuario introducido ya está registrado.", Toast.LENGTH_SHORT).show()
            "com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]" ->
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
        }
    }

}
