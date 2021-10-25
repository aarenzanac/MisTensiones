package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.datos_inicio_activity.*

class DatosInicioActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()

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

        button_registrarse.setOnClickListener {
            if (edit_text_nombre.text.isNotEmpty()){
                if(edit_text_edad.text.isNotEmpty() && edit_text_edad.text.toString().toInt() >= 18 && edit_text_edad.text.toString().toInt() <= 100){
                    if(edit_text_altura.text.isNotEmpty() && edit_text_altura.text.toString().toInt() >= 50 && edit_text_altura.text.toString().toInt() <= 250){
                        if(radio_button_hombre.isChecked || radio_button_mujer.isChecked){
                            database.collection("usuariosRegistrados").document(email).set(
                                    hashMapOf("email" to email,
                                    "nombre" to edit_text_nombre.text.toString(),
                                    "edad" to edit_text_edad.text.toString().toInt(),
                                    "altura" to edit_text_altura.text.toString().toInt()
                                    )
                            )
                            //goPrincipalActivity(email, edit_text_nombre.text.toString(), edit_text_edad.text.toString().toInt(), edit_text_altura.text.toString().toInt())
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

    /*private fun goPrincipalActivity(email: String, nombre: String, edad: Int, altura: Int){
        val pantallaPrincipalIntent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("email", email)
            putExtra("nombre", nombre)
            putExtra("edad", edad)
            putExtra("altura", altura)
        }

        startActivity(pantallaPrincipalIntent)
    }*/
    private fun goPrincipalActivity(email: String) {
        val pantallaPrincipalIntent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("email", email)
        }

        startActivity(pantallaPrincipalIntent)
    }
    private fun mostrarError(){
        Toast.makeText(this, "DEBE COMPLETAR TODOS LOS CAMPOS, REVISE LOS DATOS.", Toast.LENGTH_SHORT).show()
    }


}
