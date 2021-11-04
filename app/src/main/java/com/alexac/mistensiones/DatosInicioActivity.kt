package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.datos_inicio_activity.*

class DatosInicioActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()
    private val fechaActual = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.datos_inicio_activity)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        textViewEmail.setText(email)
        if (email != null) {
            setup(email)
        }

    }

    private fun setup(email: String) {

        button_modificar_datos.setOnClickListener {
            if (edit_text_nombre.text.isNotEmpty()){
                if(edit_text_edad.text.isNotEmpty() && edit_text_edad.text.toString().toInt() >= 18 && edit_text_edad.text.toString().toInt() <= 100){
                    if(edit_text_altura.text.isNotEmpty() && edit_text_altura.text.toString().toInt() >= 50 && edit_text_altura.text.toString().toInt() <= 250){
                        if(radio_button_hombre.isChecked || radio_button_mujer.isChecked){
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
                            database.collection(email).document(fechaActual.toString()).set(
                                    hashMapOf("fecha" to "",
                                    "hora" to "",
                                    "sistolica" to 0.0,
                                    "diastolica" to 0.0,
                                    "oxigenacion" to 0,
                                    "peso" to 0.0,
                                    "glucemia" to 0.0,
                                    "observaciones" to "",
                                    "timestamp" to 0
                                )
                            )
                            goPrincipalActivity(email)
                        }else{
                            Toast.makeText(this, "SELECCIONE UN GÉNERO, POR FAVOR.", Toast.LENGTH_SHORT).show()

                        }
                    }else{
                        Toast.makeText(this, "LA ALTURA DEBE ESTAR COMPRENDIDA ENTRE 50 Y 250 CM. REVISELA POR FAVOR.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "LA EDAD DEBE ESTAR COMPRENDIDA ENTRE 18 Y 100 AÑOS. REVISELA, POR FAVOR.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "DEBE INTRODUCIR UN NOMBRE, POR FAVOR.", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun goPrincipalActivity(email: String) {
        val pantallaPrincipalIntent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("email", email)
        }

        startActivity(pantallaPrincipalIntent)
    }
}
