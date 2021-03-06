package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.funciones_varias.CargarPreferenciasCompartidas.Companion.preferenciasCompartidas
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login_activity.button_acceder
import kotlinx.android.synthetic.main.login_activity.edit_text_email
import kotlinx.android.synthetic.main.login_activity.edit_text_password
import kotlinx.android.synthetic.main.login_activity.login_layout
import kotlinx.android.synthetic.main.login_activity.text_view_registrese
import kotlinx.android.synthetic.main.login_activity_responsive.*


class LoginActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()
    private var nombreLogueado = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        //MANTIENE EL SPLASH SCREEN DURANTE 2 SEGUNDOS
        Thread.sleep(2000)
        setTheme(R.style.Apptheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_responsive)
        login_layout.visibility = View.VISIBLE

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen", bundle)


        setup()
        comprobarSesion()
    }


    private fun setup() {

        text_view_registrese.setOnClickListener {
            intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        button_acceder.setOnClickListener {

            if (edit_text_email.text.isNotEmpty() && edit_text_password.text.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(edit_text_email.text.toString(), edit_text_password.text.toString()).addOnCompleteListener {
                    val user = Firebase.auth.currentUser
                    if (it.isSuccessful) {
                        if (user!!.isEmailVerified) {

                            Toast.makeText(this, "ACCESO REALIZADO CON ÉXITO", Toast.LENGTH_SHORT).show()
                            database.collection("usuariosRegistrados").document(edit_text_email.text.toString()).get().addOnSuccessListener{
                                nombreLogueado = (it.get("nombre") as String)
                                //Toast.makeText(this, nombreLogueado + " pruebaNombre", Toast.LENGTH_SHORT).show()
                                if(nombreLogueado == "Nuevo"){
                                    goDatosInicioActivity(edit_text_email.text.toString())
                                    preferenciasCompartidas.guardarPreferenciaEmail(edit_text_email.text.toString())
                                    preferenciasCompartidas.guardarPreferenciaPwd(edit_text_password.text.toString())
                                }else{
                                    preferenciasCompartidas.guardarPreferenciaEmail(edit_text_email.text.toString())
                                    preferenciasCompartidas.guardarPreferenciaPwd(edit_text_password.text.toString())
                                    goPrincipalActivity(edit_text_email.text.toString(), edit_text_password.text.toString())
                                }
                            }

                            //preferenciasCompartidas.guardarPreferenciaEmail(edit_text_email.text.toString())
                            //preferenciasCompartidas.guardarPreferenciaPwd(edit_text_password.text.toString())
                            //goPrincipalActivity(edit_text_email.text.toString(), edit_text_password.text.toString())
                        } else {
                            Toast.makeText(this, "ASEGÚRESE DE VERIFICAR SU MAIL ANTES DE ENTRAR.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        mostrarError(it.exception.toString())
                    }
                }
            } else {
                Toast.makeText(this, "COMPLETE TODOS LOS CAMPOS, POR FAVOR.",Toast.LENGTH_SHORT).show()
            }
        }

        text_view_olvido_contraseña.setOnClickListener {
            if(edit_text_email.text.isNotEmpty()){
                enviarMailRestablecimiento(edit_text_email.text.toString())
            }else{
                Toast.makeText(this, "INDIQUE UN CORREO EN EL CAMPO DE EMAIL, POR FAVOR.",Toast.LENGTH_SHORT).show()
            }
        }
    }


    //FUNCION PARA ACCEDER A LA ACTIVIDAD PRINCIPAL EN CASO DE EXISTIR USUARIO REGISTRADO
    private fun goPrincipalActivity(email: String, password: String){
        val pantallaPrincipalIntent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("email", email)
        }

        startActivity(pantallaPrincipalIntent)
    }

    private fun goDatosInicioActivity(email: String){
        val pantallaDatosInicioIntent = Intent(this, DatosInicioActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(pantallaDatosInicioIntent)
    }


    //FUNCION PARA HACER MAS AMIGABLE EL ERROR DEVUELTO EN EL LOGIN POR FIREBASE
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
    }

    private fun comprobarSesion(){
        val email: String? = preferenciasCompartidas.recuperarPrefenenciaEmail()
        val pwd: String? = preferenciasCompartidas.recuperarPrefenenciaPwd()
        if(!email.equals("")  && !pwd.equals("")){
            login_layout.visibility = View.INVISIBLE
            goPrincipalActivity(email!!, pwd!!)
        }
    }

    private fun enviarMailRestablecimiento(email: String){
        val user = FirebaseAuth.getInstance()
        user.sendPasswordResetEmail(email).addOnCompleteListener { tarea ->
            if(tarea.isSuccessful){
                Toast.makeText(this, "CORREO DE RESTABLECIMIENTO ENVIADO CON ÉXITO.",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
