package com.alexac.mistensiones

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.datos_inicio_activity.*
import kotlinx.android.synthetic.main.registro_activity.*
import kotlinx.android.synthetic.main.registro_activity.button_modificar_datos
import kotlinx.android.synthetic.main.registro_activity.edit_text_email_registro
import kotlinx.android.synthetic.main.registro_activity.edit_text_password_registro
import kotlinx.android.synthetic.main.registro_activity_responsive.*

class RegistroActivity : AppCompatActivity() {
    private val database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_activity_responsive)

        setup()
    }


    private fun setup(){
        button_registro.setOnClickListener {
            if (edit_text_email_registro.text.isNotEmpty() && edit_text_password_registro.text.isNotEmpty()){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edit_text_email_registro.text.toString(), edit_text_password_registro.text.toString()).addOnCompleteListener{
                    val user = Firebase.auth.currentUser

                    if(it.isSuccessful){
                        //Toast.makeText(this, "REGISTRO REALIZADO CON ÉXITO", Toast.LENGTH_SHORT).show()
                        enviarMailRegistro(this, user)
                        database.collection("usuariosRegistrados").document(edit_text_email_registro.text.toString()).set(
                                hashMapOf("email" to edit_text_email_registro.text.toString(),
                                        "nombre" to "Nuevo",
                                        "edad" to "",
                                        "altura" to ""
                                )
                        )
                        goLoginActivity()
                    }else{
                        mostrarError(it.exception.toString())
                    }
                }
            }
        }
    }

    private fun enviarMailRegistro(context: Context, user:  FirebaseUser?){
        val user = Firebase.auth.currentUser
        user!!.sendEmailVerification().addOnCompleteListener(){enviadoCorreo ->
            if(enviadoCorreo.isSuccessful){
                Toast.makeText(context, "CORREO ENVIADO CON ÉXITO. VERIFIQUE SU DIRECCIÓN DE CORREO A TRAVÉS DE SU EMAIL.", Toast.LENGTH_SHORT).show()
            }
        }

    }


    //BOTON PARA ACCEDER A LA PANTALLA DE INICIO
    private fun goDatosInicioActivity(email: String, password: String){
        val datosInicioIntent = Intent(this, DatosInicioActivity::class.java).apply {
            putExtra("email", email)
            putExtra("password", password)
        }

        startActivity(datosInicioIntent)
    }

    private fun goLoginActivity(){
        val datosInicioIntent = Intent(this, LoginActivity::class.java).apply {
        }

        startActivity(datosInicioIntent)
    }
    //FUNCIÓN PARA HACER MAS AMIGABLE EL ERROR DEVUELTO EN EL REGISTRO POR FIREBASE
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
