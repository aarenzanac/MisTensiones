package com.alexac.mistensiones

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.datos_inicio_activity.*
import kotlinx.android.synthetic.main.prinpipal_activity.*

class PrincipalActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()
    private var numeroRegistro = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.prinpipal_activity)
        editTextDate.setInputType(InputType.TYPE_NULL);
        editTextTime.setInputType(InputType.TYPE_NULL);

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

        imageButtonModificarDatosInicio.setOnClickListener {
            val pantallaModificarDatosIntent = Intent(this, ModificarDatosInicioActivity::class.java).apply {
                putExtra("email", email) }
            startActivity(pantallaModificarDatosIntent)
        }

        editTextDate.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
            datePicker.show(supportFragmentManager, "datePicker")
        }

        editTextTime.setOnClickListener {
            val timePicker = TimePickerFragment { onTimeSelected(it) }
            timePicker.show(supportFragmentManager, "timePicker")
        }

        imageButtonAñadir.setOnClickListener {
            insertarRegistro(email)
        }

        imageButtonSalir.setOnClickListener {
            val pantallaPrincipalIntent = Intent(this, LoginActivity::class.java).apply {}
            FirebaseAuth.getInstance().signOut()
            startActivity(pantallaPrincipalIntent)
        }


    }
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDate.setText("$day-$month1-$year")
    }

    private fun onTimeSelected(time: String) {
        editTextTime.setText("$time")
    }

    private fun insertarRegistro(email: String){
        if(editTextDate.text.isNotEmpty() && editTextTime.text.isNotEmpty() && edit_text_sistolica.text.isNotEmpty() && edit_text_diastolica.text.isNotEmpty() && edit_text_oxigenacion.text.isNotEmpty() && edit_text_peso.text.isNotEmpty()){
            database.collection("datosUsuarios").document(email + numeroRegistro.toString()).set(
                    hashMapOf("Registro nº" to numeroRegistro,
                            "email" to email,
                            "fecha" to editTextDate.text.toString(),
                            "hora" to editTextTime.text.toString(),
                            "sistolica" to edit_text_sistolica.text.toString().toDouble(),
                            "diastolica" to edit_text_diastolica.text.toString().toDouble(),
                            "oxigenacion" to edit_text_oxigenacion.text.toString().toInt(),
                            "peso" to edit_text_peso.text.toString().toDouble()
                    )
            )
            numeroRegistro += 1
            Toast.makeText(this, "REGISTRO AÑADIDO CON ÉXITO.", Toast.LENGTH_SHORT).show()
            limpiarCampos()

        }else{
            Toast.makeText(this, "LA EDAD DEBE ESTAR COMPRENDIDA ENTRE 18 Y 100 AÑOS. REVISELA, POR FAVOR.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos(){
        editTextDate.text.clear()
        editTextTime.text.clear()
        edit_text_sistolica.text.clear()
        edit_text_diastolica.text.clear()
        edit_text_oxigenacion.text.clear()
        edit_text_peso.text.clear()
    }
}
