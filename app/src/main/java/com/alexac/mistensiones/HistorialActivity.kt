package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexac.mistensiones.fecha_hora.DatePickerFragment
import com.alexac.mistensiones.funciones_varias.FuncionesVarias
import com.alexac.mistensiones.graficas.GraficaTension
import com.alexac.mistensiones.models.DocumentoDatos
import com.alexac.mistensiones.recyclerView.DatosAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.historial_activity.*

class HistorialActivity : AppCompatActivity(), DatosAdapter.OnDocumentoDatosClickListener {

    val funcionesVarias:FuncionesVarias = FuncionesVarias()
    private val database = FirebaseFirestore.getInstance()
    private lateinit var datosRecyclerview: RecyclerView
    private lateinit var listaDocumentoDatos: ArrayList<DocumentoDatos>
    var diaInicio = 0
    var mesInicio = 0
    var añoInicio = 0
    var diaFinal = 0
    var mesFinal = 0
    var añoFinal = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial_activity_responsive)
        editTextDateHistorialInicio.setInputType(InputType.TYPE_NULL);
        editTextDateHistorialFinal.setInputType(InputType.TYPE_NULL);
        listaDocumentoDatos = arrayListOf<DocumentoDatos>()
        datosRecyclerview = findViewById(R.id.listaDatosHistorial)
        datosRecyclerview.setHasFixedSize(true)
        datosRecyclerview.layoutManager = LinearLayoutManager(this)
        datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatos, this, this)


        val bundle = intent.extras
        val email = bundle?.getString("email")

        if (email != null) {
            database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener {
                textViewNombreLogueadoPantallaHistorial.setText(it.get("nombre") as String?)
            }
            setup(email)
            mostrarTodo(email)
        }
    }


    private fun setup(email: String){


        editTextDateHistorialInicio.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelectedInicio(day, month, year) }
            datePicker.show(supportFragmentManager, "datePicker")
        }

        editTextDateHistorialFinal.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelectedFinal(day, month, year) }
            datePicker.show(supportFragmentManager, "datePicker")
        }

        imageButtonVolverHistorial.setOnClickListener {
           onBackPressed()
        }

        imageButtonGraficas.setOnClickListener {
            val pantallaGraficaIntent = Intent(this, GraficaTension::class.java).apply {
                putExtra("email", email)

            }

            startActivity(pantallaGraficaIntent)
        }

        imageViewFiltrarHistorial.setOnClickListener {
            filtrar(email)
        }
    }

    //SETEA EL EDIT TEXT DE FECHA CON LA FECHA SELECCIONADA Y LAS VARIABLES DEL TIMESTAMP
    private fun onDateSelectedInicio(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateHistorialInicio.setText("$day-$month1-$year")
        diaInicio = day.toInt()
        mesInicio = month.toInt()
        añoInicio = year.toInt()-1900
    }


    //SETEA EL EDIT TEXT DE FECHA CON LA FECHA SELECCIONADA Y LAS VARIABLES DEL TIMESTAMP
    private fun onDateSelectedFinal(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateHistorialFinal.setText("$day-$month1-$year")
        diaFinal = day.toInt()+1
        mesFinal = month.toInt()
        añoFinal = year.toInt()-1900
    }


    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL
    private fun mostrarTodo(email: String){

        val coleccionFechas = database.collection(email)
        coleccionFechas.get().addOnSuccessListener { documents ->
            /*for (document in documents) {
                Log.d("Registro", "${document.id} => ${document.data}")
            }*/
            listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
            var listaDocumentoDatosOrdenada = funcionesVarias.ordenarMayorAMenor(listaDocumentoDatos)
            Log.d("Registro", "EXTRACCION DE HISTORIAL ACTIVITY ---- Numero de elementos: ${listaDocumentoDatos.size}")
            if (listaDocumentoDatosOrdenada.isEmpty()) {
                datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatosOrdenada, this, this)
                Toast.makeText(this, "NO HAY DOCUMENTOS PARA MOSTRAR.", Toast.LENGTH_SHORT).show()
            } else {
                datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatosOrdenada, this, this)
            }
        }

    }


    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL Y DE LA FECHA SELECCIONADA
    private fun filtrar(email: String){
        if(editTextDateHistorialInicio.text.isNotEmpty() && editTextDateHistorialFinal.text.isNotEmpty()){
            var timestampInicio = funcionesVarias.crearTimestamp(diaInicio, mesInicio, añoInicio)
            var timestampFinal = funcionesVarias.crearTimestamp(diaFinal, mesFinal, añoFinal)
            if(timestampFinal < timestampInicio){
                Toast.makeText(this, "LA FECHA FINAL NO PUEDE SER MENOR QUE LA INICIAL.", Toast.LENGTH_SHORT).show()

            }else{
                val coleccionFechas = database.collection(email)
                coleccionFechas.get().addOnSuccessListener {documents ->
                    /* for (document in documents) {
                         Log.d("Registro", "${document.id} => ${document.data}")
                     }*/
                    listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
                    var listaDocumentoDatosOrdenada = funcionesVarias.ordenarMayorAMenor(listaDocumentoDatos)
                    var listaDocumentosFiltrados = aplicarFiltroFechas(listaDocumentoDatosOrdenada)
                    if (listaDocumentosFiltrados.isEmpty()){
                        datosRecyclerview.adapter = DatosAdapter(listaDocumentosFiltrados, this, this)
                        Toast.makeText(this, "NO HAY DOCUMENTOS PARA MOSTRAR.", Toast.LENGTH_SHORT).show()
                    }else {
                        datosRecyclerview.adapter = DatosAdapter(listaDocumentosFiltrados, this, this)
                    }
                }
            }
        }else {
            Toast.makeText(this, "DEBE SELECCIONAR UNA FECHA PARA FILTRAR.", Toast.LENGTH_SHORT).show()
        }
    }


    //CREA UN ARRAY CON LOS ELEMENTOS COMPRENDIDOS ENTRE EL TIMESTAMP INICIO Y FINAL SELECCIONADO.
    private fun aplicarFiltroFechas(ListaDocumentoDatos: ArrayList<DocumentoDatos>): ArrayList<DocumentoDatos>{
        var timestampInicio = funcionesVarias.crearTimestamp(diaInicio, mesInicio, añoInicio)
        var timestampFinal = funcionesVarias.crearTimestamp(diaFinal, mesFinal, añoFinal)
        var arrayFiltrado = arrayListOf<DocumentoDatos>()
        for(doc in listaDocumentoDatos){
            if(doc.timestamp >= timestampInicio && doc.timestamp <= timestampFinal){
                arrayFiltrado.add(doc)
            }
        }
        return arrayFiltrado
    }


    //FUNCION HEREDADA ONCLICK. NO SE IMPLEMENTA NADA PORQUE NO ES NECESARIO HACER CLICK EN EL ELEMENTO EN EL HISTORIAL
    override fun onItemClick(item: DocumentoDatos) {

    }

}
