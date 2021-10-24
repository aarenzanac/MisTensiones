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
        button_registrarse.setOnClickListener {
            if (edit_text_email_registro.text.isNotEmpty() && edit_text_password_registro.text.isNotEmpty()){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edit_text_email_registro.text.toString(), edit_text_password_registro.text.toString()).addOnCompleteListener{

                    if(it.isSuccessful){
                        Toast.makeText(this, "REGISTRO REALIZADO CON ÉXITO", Toast.LENGTH_SHORT).show()
                        goDatosInicioActivity(edit_text_email_registro.text.toString(), edit_text_password_registro.text.toString())
                    }else{

                        mostrarError()
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

    private fun mostrarError(){
        Toast.makeText(this, "ERROR EN EL REGISTRO, REVISE LOS DATOS", Toast.LENGTH_SHORT).show()
    }

}
