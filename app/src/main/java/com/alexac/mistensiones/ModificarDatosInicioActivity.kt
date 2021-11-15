package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.funciones_varias.CargarPreferenciasCompartidas
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.datos_inicio_activity.*

class ModificarDatosInicioActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificar_datos_inicio_activity_responsive)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        textViewEmail.setText(email)
        if (email != null) {
            setup(email)
        }
    }


    private fun setup(email: String) {
        //EN FUNCION DEL USUARIO LOGEADO, MUESTRO SU SEXO Y SU NOMBRE QUE SON LAS VARIABLES QUE NO DEJO MODIFICAR
        val sexo = ""
        database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener{
            edit_text_nombre.setText(it.get("nombre") as String?)}
        database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener{
            if(it.get("sexo") == ("Hombre")){
                radio_button_hombre.isChecked = true
            }else{
                radio_button_mujer.isChecked = true
            }
        }


        button_modificar_datos.setOnClickListener {
            if(edit_text_edad.text.isNotEmpty() && edit_text_edad.text.toString().toInt() >= 18 && edit_text_edad.text.toString().toInt() <= 100){
                if(edit_text_altura.text.isNotEmpty() && edit_text_altura.text.toString().toInt() >= 50 && edit_text_altura.text.toString().toInt() <= 250){
                    modificarDatosUsuario(email)
                }else{
                    Toast.makeText(this, "LA ALTURA DEBE ESTAR COMPRENDIDA ENTRE 50 Y 250 CM. REVISELA POR FAVOR.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "LA EDAD DEBE ESTAR COMPRENDIDA ENTRE 18 Y 100 AÑOS. REVISELA, POR FAVOR.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //FUNCION PARA MODIFICAR LOS DATOS PERSONALES DEL USUARIO
    private fun modificarDatosUsuario(email: String){
        var sexo = ""
        if(radio_button_hombre.isChecked){
            sexo = "Hombre"
        }else{
            sexo = "Mujer"
        }
        database.collection("usuariosRegistrados").document(email).set(
                hashMapOf("email" to email,
                        "nombre" to edit_text_nombre.text.toString(),
                        "edad" to edit_text_edad.text.toString().toInt(),
                        "altura" to edit_text_altura.text.toString().toInt(),
                        "sexo" to sexo
                )
        )
        CargarPreferenciasCompartidas.preferenciasCompartidas.guardarPreferenciaAltura(edit_text_altura.text.toString().toInt())
        goPrincipalActivity(email)
    }

    //FUNCION PARA ACCEDER A LA ACTIVIDAD PRINCIPAL
    private fun goPrincipalActivity(email: String) {
        val pantallaPrincipalIntent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(pantallaPrincipalIntent)
    }
}
