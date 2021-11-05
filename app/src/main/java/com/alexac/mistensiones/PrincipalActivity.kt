package com.alexac.mistensiones

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.fecha_hora.DatePickerFragment
import com.alexac.mistensiones.fecha_hora.TimePickerFragment
import com.alexac.mistensiones.funciones_varias.FuncionesVarias
import com.alexac.mistensiones.models.DocumentoDatos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.principal_activity.*


class PrincipalActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()
    val funcionesVarias: FuncionesVarias = FuncionesVarias()
    private lateinit var listaDocumentoDatos: ArrayList<DocumentoDatos>
    var dia = 0
    var mes = 0
    var año = 0
    var hora = 0
    var minutos = 0
    var segundos = 0
    var nanosegundos = 0
    var contadorWarnings: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal_activity)
        editTextDate.setInputType(InputType.TYPE_NULL);
        editTextTime.setInputType(InputType.TYPE_NULL);
        var documento: DocumentoDatos = DocumentoDatos()
        listaDocumentoDatos = arrayListOf<DocumentoDatos>()
        listaDocumentoDatos.add(documento)


        val bundle = intent.extras
        val email = bundle?.getString("email")

        if (email != null) {
            database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener{
                textViewNombreLogueado.setText(it.get("nombre") as String?)
                mostrarTodo(email)
            }


        }

    }

    private fun setup(email: String){

        cargarUltimaEntrada(listaDocumentoDatos[0])

        imageButtonModificarDatosInicio.setOnClickListener {
            val pantallaModificarDatosIntent = Intent(
                this,
                ModificarDatosInicioActivity::class.java
            ).apply {
                putExtra("email", email) }
            startActivity(pantallaModificarDatosIntent)
        }

        editTextDate.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelected(
                day,
                month,
                year
            ) }
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

        imageButtonHistorial.setOnClickListener {
            val pantallaHistorialIntent = Intent(this, HistorialActivity::class.java).apply {
                putExtra("email", email)
            }
            startActivity(pantallaHistorialIntent)
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
        dia = day.toInt()
        mes = month.toInt()
        año = year.toInt() - 1900
    }

    private fun onTimeSelected(time: String) {
        editTextTime.setText("$time")
        val stringArray: List<String> = time.split(":")
        hora = stringArray[0].toInt()
        minutos = stringArray[1].toInt()

    }

    private fun insertarRegistro(email: String){
        if(editTextDate.text.isNotEmpty() && editTextTime.text.isNotEmpty() && edit_text_sistolica.text.isNotEmpty() && edit_text_diastolica.text.isNotEmpty() && edit_text_peso.text.isNotEmpty()){
            if(editTextGlucemia.text.isEmpty()){
                editTextGlucemia.setText("0.0")
            }
            if(edit_text_oxigenacion.text.isEmpty()){
                edit_text_oxigenacion.setText("0")
            }
            var timestamp: Long = funcionesVarias.crearTimestampCompleto(dia, mes, año, hora, minutos, segundos, nanosegundos)
            database.collection(email.toString()).document(editTextDate.text.toString() + "-" + editTextTime.text.toString()).set(
                hashMapOf(
                    "fecha" to editTextDate.text.toString(),
                    "hora" to editTextTime.text.toString(),
                    "sistolica" to edit_text_sistolica.text.toString().toDouble(),
                    "diastolica" to edit_text_diastolica.text.toString().toDouble(),
                    "oxigenacion" to edit_text_oxigenacion.text.toString().toLong(),
                    "peso" to edit_text_peso.text.toString().toDouble(),
                    "glucemia" to editTextGlucemia.text.toString().toDouble(),
                    "observaciones" to editTextObservaciones.text.toString(),
                    "timestamp" to timestamp
                )
            )
            Toast.makeText(this, "REGISTRO AÑADIDO CON ÉXITO.", Toast.LENGTH_SHORT).show()
            var ultimoDocumento: DocumentoDatos = DocumentoDatos()
            ultimoDocumento.fecha = editTextDate.text.toString()
            ultimoDocumento.hora = editTextTime.text.toString()
            ultimoDocumento.sistolica = edit_text_sistolica.text.toString().toDouble()
            ultimoDocumento.diastolica = edit_text_diastolica.text.toString().toDouble()
            ultimoDocumento.peso = edit_text_peso.text.toString().toDouble()
            ultimoDocumento.oxigenacion = edit_text_oxigenacion.text.toString().toLong()
            ultimoDocumento.observaciones = editTextObservaciones.text.toString()
            ultimoDocumento.glucosa = editTextGlucemia.text.toString().toDouble()
            cargarUltimaEntrada(ultimoDocumento)
            if(ultimoDocumento.sistolica >= 160.0 || ultimoDocumento.diastolica >= 90.0){
                contadorWarnings += 1
            }
            if(contadorWarnings >= 3){

                funcionesVarias.extraerAlimentos(this)
            }
            limpiarCampos()
        }else{
            Toast.makeText(this, "COMPLETE LOS DATOS, POR FAVOR.", Toast.LENGTH_SHORT).show()
        }



    }

    private fun cargarUltimaEntrada(ultimoDocumento: DocumentoDatos) {

        textviewFechaUltimaEntrada.setText(ultimoDocumento.fecha)
        textViewHoraUltimaEntrada.setText(ultimoDocumento.hora)
        textViewSistolicaUltimaEntrada.setText(ultimoDocumento.sistolica.toString())
        textViewDiastolicaUltimaEntrada.setText(ultimoDocumento.diastolica.toString())
        textViewPesoUltimaEntrada.setText(ultimoDocumento.peso.toString())
        textViewOxigenacionUltimaEntrada.setText(ultimoDocumento.oxigenacion.toString())
        textViewGlucemiaUltimaEntrada.setText(ultimoDocumento.glucosa.toString())
        textViewObservacionesUltimaEntrada.setText(ultimoDocumento.observaciones)
        if(ultimoDocumento.sistolica >=180 || ultimoDocumento.diastolica >= 90.0){
            textViewSemaforoUltimaEntrada.setBackgroundColor(Color.parseColor("#E14336"))
        }else if(ultimoDocumento.sistolica <= 140.0 && ultimoDocumento.diastolica <= 70.0){
            textViewSemaforoUltimaEntrada.setBackgroundColor(Color.parseColor("#95D328"))
        }else{
            textViewSemaforoUltimaEntrada.setBackgroundColor(Color.parseColor("#E1BD36"))
        }
    }

    private fun mostrarTodo(email: String) {

        val coleccionTotal = database.collection(email)
        coleccionTotal.get().addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d("Registro", "${document.id} => ${document.data}")
            }
            listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
            setup(email)
        }
    }

    private fun limpiarCampos(){
        editTextDate.setText("")
        editTextTime.setText("")
        edit_text_sistolica.text.clear()
        edit_text_diastolica.text.clear()
        edit_text_oxigenacion.text.clear()
        edit_text_peso.text.clear()
        editTextGlucemia.text.clear()
        editTextObservaciones.text.clear()
    }

    /*private fun llamarCloudFunctionSubidaDatos(){
        cloudFunctions.getHttpsCallable("subirDatos").call()
                .addOnFailureListener {
                    Log.wtf("Error en la llamada de subir datos. ", it)
                }
                .addOnSuccessListener {
                    Toast.makeText(this, "exito en la llamada de la funcion en la nube.", Toast.LENGTH_SHORT).show()
                }
    }*/
}
