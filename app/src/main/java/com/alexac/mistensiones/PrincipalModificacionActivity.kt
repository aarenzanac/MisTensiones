package com.alexac.mistensiones

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.principal_activity.*
import kotlinx.android.synthetic.main.principal_activity.edit_text_diastolica
import kotlinx.android.synthetic.main.principal_activity.edit_text_oxigenacion
import kotlinx.android.synthetic.main.principal_activity.edit_text_peso
import kotlinx.android.synthetic.main.principal_activity.edit_text_sistolica
import kotlinx.android.synthetic.main.principal_activity.textViewNombreLogueado
import kotlinx.android.synthetic.main.principal_modificacion_activity.*

class PrincipalModificacionActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal_modificacion_activity)
        editTextDate.setInputType(InputType.TYPE_NULL);

        val bundle = intent.extras
        val email = bundle?.getString("email")

        if (email != null) {
            database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener{
                textViewNombreLogueado.setText(it.get("nombre") as String?)
            }
            setup(email)
        }

    }

    private fun setup(email: String){

        editTextDate.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
            datePicker.show(supportFragmentManager, "datePicker")
        }

        imageButtonModificarRegistro.setOnClickListener {
            modificarRegistro(email)
            limpiarCampos()
        }

        imageButtonEliminarRegistro.setOnClickListener {
            //eliminarRegistro()
            limpiarCampos()
        }

        imageButtonVolver.setOnClickListener {
           onBackPressed()
        }

    }
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDate.setText("$day-$month1-$year")
    }



    private fun modificarRegistro(email: String){
        if(editTextDate.text.isNotEmpty() && editTextTime.text.isNotEmpty() && edit_text_sistolica.text.isNotEmpty() && edit_text_diastolica.text.isNotEmpty() && edit_text_oxigenacion.text.isNotEmpty() && edit_text_peso.text.isNotEmpty()){
            database.collection(email.toString()).document(editTextDate.text.toString()+"-"+editTextTime.text.toString()).set(
                    hashMapOf("fecha" to editTextDate.text.toString(),
                            "hora" to editTextTime.text.toString(),
                            "sistolica" to edit_text_sistolica.text.toString().toDouble(),
                            "diastolica" to edit_text_diastolica.text.toString().toDouble(),
                            "oxigenacion" to edit_text_oxigenacion.text.toString().toInt(),
                            "peso" to edit_text_peso.text.toString().toDouble()
                    )
            )
            Toast.makeText(this, "REGISTRO AÑADIDO CON ÉXITO.", Toast.LENGTH_SHORT).show()
            limpiarCampos()

        }else{
            Toast.makeText(this, "LA EDAD DEBE ESTAR COMPRENDIDA ENTRE 18 Y 100 AÑOS. REVISELA, POR FAVOR.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun consultaFecha(email: String){
        val coleccionFechas = database.collection(email)
        val consulta = coleccionFechas.whereEqualTo("fecha", editTextDateModificacion)
    }

    private fun limpiarCampos(){
        editTextDate.text.clear()
        edit_text_sistolica.text.clear()
        edit_text_diastolica.text.clear()
        edit_text_oxigenacion.text.clear()
        edit_text_peso.text.clear()
    }
}
