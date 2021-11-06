package com.alexac.mistensiones

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexac.mistensiones.fecha_hora.DatePickerFragment
import com.alexac.mistensiones.funciones_varias.FuncionesVarias
import com.alexac.mistensiones.models.DocumentoDatos
import com.alexac.mistensiones.recyclerView.DatosAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.principal_modificacion_activity.*

class PrincipalModificacionActivity : AppCompatActivity(), DatosAdapter.OnDocumentoDatosClickListener {

    private val database = FirebaseFirestore.getInstance()
    val funcionesVarias: FuncionesVarias = FuncionesVarias()
    private lateinit var datosRecyclerview: RecyclerView
    private lateinit var listaDocumentoDatos: ArrayList<DocumentoDatos>
    private var posicionItem = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal_modificacion_activity)
        editTextDateModificacion.setInputType(InputType.TYPE_NULL);
        listaDocumentoDatos = arrayListOf<DocumentoDatos>()
        datosRecyclerview = findViewById(R.id.listaDatos)
        datosRecyclerview.setHasFixedSize(true)
        datosRecyclerview.layoutManager = LinearLayoutManager(this)
        datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatos, this, this)




        val bundle = intent.extras
        val email = bundle?.getString("email")

        if (email != null) {
            database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener {
                textViewNombreLogueadoPantallaModificacion.setText(it.get("nombre") as String?)
            }
            setup(email)
        }
    }


    private fun setup(email: String){

        editTextDateModificacion.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
            datePicker.show(supportFragmentManager, "datePicker")
        }

        imageButtonModificarRegistro.setOnClickListener {
            modificarRegistro(email)
            limpiarCampos()
        }


        imageButtonEliminarRegistro.setOnClickListener {
            eliminarRegistro(email)
            limpiarCampos()
        }

        imageButtonVolver.setOnClickListener {
           onBackPressed()
        }

        imageViewFiltrar.setOnClickListener {
            limpiarCampos()
            filtrar(email)
        }
    }


    //SETEA EL EDIT TEXT DE FECHA CON LA FECHA SELECCIONADA
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateModificacion.setText("$day-$month1-$year")
    }


    //MODIFICA EL REGISTRO SELECCIONADO Y RECARFA EL RECYCLERVIEW
    private fun modificarRegistro(email: String){
        if(edit_text_sistolica_modificaion.text.isNotEmpty() && edit_text_diastolica_modificacion.text.isNotEmpty() && edit_text_peso_modificacion.text.isNotEmpty()){
            if(edit_text_glucemia_modificacion.text.isEmpty()){
                edit_text_glucemia_modificacion.setText("0.0")
            }
            if(edit_text_oxigenacion_modificacion.text.isEmpty()){
                edit_text_oxigenacion_modificacion.setText("0")
            }

            database.collection(email.toString()).document(listaDocumentoDatos[posicionItem].fecha+"-"+listaDocumentoDatos[posicionItem].hora).set(
                    hashMapOf("fecha" to listaDocumentoDatos[posicionItem].fecha,
                            "hora" to listaDocumentoDatos[posicionItem].hora,
                            "sistolica" to edit_text_sistolica_modificaion.text.toString().toDouble(),
                            "diastolica" to edit_text_diastolica_modificacion.text.toString().toDouble(),
                            "oxigenacion" to edit_text_oxigenacion_modificacion.text.toString().toLong(),
                            "peso" to edit_text_peso_modificacion.text.toString().toDouble(),
                            "glucemia" to edit_text_glucemia_modificacion.text.toString().toDouble(),
                            "observaciones" to edit_text_observaciones_modificacion.text.toString(),
                            "timestamp" to listaDocumentoDatos[posicionItem].timestamp
                    )
            )
            Toast.makeText(this, "REGISTRO MODIFICADO CON ÉXITO.", Toast.LENGTH_SHORT).show()
            filtrar(email)
            limpiarCampos()

        }else{
            Toast.makeText(this, "DEBE RELLENAR TODOS LOS CAMPOS. REVÍSELO, POR FAVOR.", Toast.LENGTH_SHORT).show()
        }
    }


    //ELIMINA EL REGISTRO DE LA BASE DE DATOS Y RECARGA EL RECYCLERVIEW
    fun eliminarRegistro(email: String){
        if(edit_text_sistolica_modificaion.text.isNotEmpty() && edit_text_diastolica_modificacion.text.isNotEmpty() && edit_text_oxigenacion_modificacion.text.isNotEmpty() && edit_text_peso_modificacion.text.isNotEmpty()) {
            database.collection(email.toString()).document(listaDocumentoDatos[posicionItem].fecha+"-"+listaDocumentoDatos[posicionItem].hora).delete()
            Toast.makeText(this, "REGISTRO ELIMINADO CON ÉXITO.", Toast.LENGTH_SHORT).show()
            filtrar(email)
            limpiarCampos()
        }else{
            Toast.makeText(this, "DEBE SELECCIONAR UN REGISTRO EN EL GRID.", Toast.LENGTH_SHORT).show()
        }
    }


    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL Y DE LA FECHA SELECCIONADA
    private fun filtrar(email: String){
        if(editTextDateModificacion.text.isNotEmpty()){
            val coleccionFechas = database.collection(email)
            coleccionFechas.whereEqualTo("fecha", editTextDateModificacion.text.toString()).get().addOnSuccessListener {documents ->
                for (document in documents) {
                    Log.d("Registro", "${document.id} => ${document.data}")
                }
                listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
                if (listaDocumentoDatos.isEmpty()){
                    datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatos, this, this)
                    Toast.makeText(this, "NO HAY DOCUMENTOS PARA MOSTRAR.", Toast.LENGTH_SHORT).show()
                }else {
                    datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatos, this, this)

                }
            }
        }else {
            Toast.makeText(this, "DEBE SELECCIONAR UNA FECHA PARA FILTRAR.", Toast.LENGTH_SHORT).show()
        }
    }



    //LIMPIA TODOS LOS CAMPOS
    private fun limpiarCampos(){
        edit_text_sistolica_modificaion.text.clear()
        edit_text_diastolica_modificacion.text.clear()
        edit_text_oxigenacion_modificacion.text.clear()
        edit_text_peso_modificacion.text.clear()
        edit_text_glucemia_modificacion.text.clear()
        edit_text_observaciones_modificacion.text.clear()
    }


    //FUNCION SOBEESCRITA DEL ADAPTER QUE SETEA LOS CAMPOS CON LOS DATOS DE LA TARJETA CLICADA
    override fun onItemClick(item: DocumentoDatos) {
        edit_text_sistolica_modificaion.setText(item.sistolica.toString())
        edit_text_diastolica_modificacion.setText(item.diastolica.toString())
        edit_text_oxigenacion_modificacion.setText(item.oxigenacion.toString())
        edit_text_peso_modificacion.setText(item.peso.toString())
        edit_text_glucemia_modificacion.setText(item.glucosa.toString())
        edit_text_observaciones_modificacion.setText(item.observaciones.toString())
        posicionItem = item.posicion
    }
}
