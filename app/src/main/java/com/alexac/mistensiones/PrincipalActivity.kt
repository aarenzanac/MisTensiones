package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.fecha_hora.DatePickerFragment
import com.alexac.mistensiones.fecha_hora.TimePickerFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.principal_activity.*

class PrincipalActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal_activity)
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

        imageButtonInsertarRegistro.setOnClickListener {
            insertarRegistro(email)
        }

        imageButtonModificar.setOnClickListener {
            val pantallaModificacionRegistrosIntent = Intent(this, PrincipalModificacionActivity::class.java).apply {
                putExtra("email", email)
            }
            startActivity(pantallaModificacionRegistrosIntent)
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
            Toast.makeText(this, "COMPLETE LOS DATOS, POR FAVOR.", Toast.LENGTH_SHORT).show()
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
