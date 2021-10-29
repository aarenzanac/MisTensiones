package com.alexac.mistensiones

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.principal_activity.*
import kotlinx.android.synthetic.main.principal_modificacion_activity.*

class PrincipalModificacionActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()
    private lateinit var datosRecyclerview: RecyclerView
    private lateinit var listaDocumentoDatos: ArrayList<DocumentoDatos>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal_modificacion_activity)
        editTextDateModificacion.setInputType(InputType.TYPE_NULL);
        listaDocumentoDatos = arrayListOf<DocumentoDatos>()
        datosRecyclerview = findViewById(R.id.listaDatos)
        datosRecyclerview.setHasFixedSize(true)
        datosRecyclerview.layoutManager = LinearLayoutManager(this)
        datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatos, this)




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
            //modificarRegistro(email)
            limpiarCampos()
        }


        imageButtonEliminarRegistro.setOnClickListener {
            //eliminarRegistro()
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

    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL Y DE LA FECHA SELECCIONADA
    private fun filtrar(email: String){
        if(editTextDateModificacion.text.isNotEmpty()){
            val coleccionFechas = database.collection(email)
            coleccionFechas.whereEqualTo("fecha", editTextDateModificacion.text.toString()).get().addOnSuccessListener {documents ->
                for (document in documents) {
                    Log.d("Registro", "${document.id} => ${document.data}")
                }
                listaDocumentoDatos = parsearDatos(documents)
                if (listaDocumentoDatos.isEmpty()){
                    datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatos, this)
                    Toast.makeText(this, "NO HAY DOCUMENTOS PARA MOSTRAR.", Toast.LENGTH_SHORT).show()
                }else {
                    datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatos, this)

                }
            }
        }else {
            Toast.makeText(this, "DEBE SELECCIONAR UNA FECHA PARA FILTRAR.", Toast.LENGTH_SHORT).show()
        }
    }

    //PARSEA TODOS LAS COLECCIONES CLAVE:VALOR DE FIREBASE A OBJETOS DE LA CLASE DOCUMENTODATOS Y DEVUELVE UN ARRAY CON OBJETOS DE LOS RESULTADOS
    private fun parsearDatos(documents: QuerySnapshot): ArrayList<DocumentoDatos>{
        val listaDocumentoDatos = arrayListOf<DocumentoDatos>()
        for (document in documents) {
            var DocumentoDatos = DocumentoDatos()
            Log.d("Registro", "${document.id} => ${document.data}")
            DocumentoDatos.fecha = document["fecha"] as String
            DocumentoDatos.hora = document["hora"] as String
            DocumentoDatos.sistolica = document["sistolica"] as Double
            DocumentoDatos.diastolica = document["diastolica"] as Double
            DocumentoDatos.peso = document["peso"] as Double
            DocumentoDatos.oxigenacion = document["oxigenacion"] as Long
            listaDocumentoDatos.add(DocumentoDatos)
        }
        return listaDocumentoDatos
    }

    private fun limpiarCampos(){
        edit_text_sistolica_modificaion.text.clear()
        edit_text_oxigenacion_modificacion.text.clear()
        edit_text_oxigenacion_modificacion.text.clear()
        edit_text_peso_modificacion.text.clear()
    }
}
