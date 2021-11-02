package com.alexac.mistensiones

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexac.mistensiones.fecha_hora.DatePickerFragment
import com.alexac.mistensiones.recyclerView.DatosAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.historial_activity.*
import java.sql.Timestamp

class HistorialActivity : AppCompatActivity(), DatosAdapter.OnDocumentoDatosClickListener {

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
        setContentView(R.layout.historial_activity)
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

        imageViewFiltrarHistorial.setOnClickListener {
            filtrar(email)

        }
        
    }

    //SETEA EL EDIT TEXT DE FECHA CON LA FECHA SELECCIONADA
    private fun onDateSelectedInicio(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateHistorialInicio.setText("$day-$month1-$year")
        diaInicio = day.toInt()
        mesInicio = month.toInt()
        añoInicio = year.toInt()-1900
    }

    //SETEA EL EDIT TEXT DE FECHA CON LA FECHA SELECCIONADA
    private fun onDateSelectedFinal(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateHistorialFinal.setText("$day-$month1-$year")
        diaFinal = day.toInt()
        mesFinal = month.toInt()
        añoFinal = year.toInt()-1900
    }

    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL Y DE LA FECHA SELECCIONADA
    private fun mostrarTodo(email: String){

        val coleccionFechas = database.collection(email)
        coleccionFechas.get().addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d("Registro", "${document.id} => ${document.data}")
            }
            listaDocumentoDatos = parsearDatos(documents)
            if (listaDocumentoDatos.isEmpty()) {
                datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatos, this, this)
                Toast.makeText(this, "NO HAY DOCUMENTOS PARA MOSTRAR.", Toast.LENGTH_SHORT).show()
            } else {
                datosRecyclerview.adapter = DatosAdapter(listaDocumentoDatos, this, this)

            }
        }
    }


    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL Y DE LA FECHA SELECCIONADA
    private fun filtrar(email: String){
        if(editTextDateHistorialInicio.text.isNotEmpty() && editTextDateHistorialFinal.text.isNotEmpty()){
            val coleccionFechas = database.collection(email)
            coleccionFechas.get().addOnSuccessListener {documents ->
                for (document in documents) {
                    Log.d("Registro", "${document.id} => ${document.data}")
                }
                listaDocumentoDatos = parsearDatos(documents)
                var listaDocumentosFiltrados = aplicarFiltroFechas(listaDocumentoDatos)
                if (listaDocumentoDatos.isEmpty()){
                    datosRecyclerview.adapter = DatosAdapter(listaDocumentosFiltrados, this, this)
                    Toast.makeText(this, "NO HAY DOCUMENTOS PARA MOSTRAR.", Toast.LENGTH_SHORT).show()
                }else {
                    datosRecyclerview.adapter = DatosAdapter(listaDocumentosFiltrados, this, this)

                }
            }
        }else {
            Toast.makeText(this, "DEBE SELECCIONAR UNA FECHA PARA FILTRAR.", Toast.LENGTH_SHORT).show()
        }
    }

    //PARSEA TODOS LAS COLECCIONES CLAVE:VALOR DE FIREBASE A OBJETOS DE LA CLASE DOCUMENTODATOS Y DEVUELVE UN ARRAY CON OBJETOS DE LOS RESULTADOS
    private fun parsearDatos(documents: QuerySnapshot): ArrayList<DocumentoDatos>{
        val listaDocumentoDatos = arrayListOf<DocumentoDatos>()
        var posicion = 0
        for (document in documents) {
            var DocumentoDatos = DocumentoDatos()
            Log.d("Registro", "${document.id} => ${document.data}")
            DocumentoDatos.fecha = document["fecha"] as String
            DocumentoDatos.hora = document["hora"] as String
            DocumentoDatos.sistolica = document["sistolica"] as Double
            DocumentoDatos.diastolica = document["diastolica"] as Double
            DocumentoDatos.peso = document["peso"] as Double
            DocumentoDatos.oxigenacion = document["oxigenacion"] as Long
            DocumentoDatos.glucosa = document["glucemia"] as Double
            DocumentoDatos.observaciones = document["observaciones"] as String
            DocumentoDatos.posicion = posicion
            DocumentoDatos.timestamp = document["timestamp"] as Long
            listaDocumentoDatos.add(DocumentoDatos)
            posicion += 1
        }
        var listaDocumentoDatosOrdenada = ordenarMayorAMenor(listaDocumentoDatos)
        return listaDocumentoDatosOrdenada
    }

    //ORDENA LOS DOCUMENTOS OBTENIDOS DE MAYOR A MENOR TIMESTAMP, ES DECIR, PRIMERO LOS MAS RECIENTES
    private fun ordenarMayorAMenor(listaDocumentoDatos: ArrayList<DocumentoDatos>): ArrayList<DocumentoDatos> {
        var temp: DocumentoDatos
        for (x in 0 until listaDocumentoDatos.size){
            for(y in 0 until listaDocumentoDatos.size){
                if(listaDocumentoDatos[x].timestamp > listaDocumentoDatos[y].timestamp){
                    temp = listaDocumentoDatos[x]
                    listaDocumentoDatos[x] = listaDocumentoDatos[y]
                    listaDocumentoDatos[y] = temp
                }
            }
        }

        return listaDocumentoDatos
    }

    private fun aplicarFiltroFechas(ListaDocumentoDatos: ArrayList<DocumentoDatos>): ArrayList<DocumentoDatos>{
        var timestampInicio = crearTimestamp(diaInicio, mesInicio, añoInicio)
        var timestampFinal = crearTimestamp(diaFinal, mesFinal, añoFinal)
        var arrayFiltrado = arrayListOf<DocumentoDatos>()
        for(doc in listaDocumentoDatos){
            if(doc.timestamp >= timestampInicio && doc.timestamp <= timestampFinal){
                arrayFiltrado.add(doc)
            }
        }

        return arrayFiltrado
    }

    override fun onItemClick(item: DocumentoDatos) {
        TODO("Not yet implemented")
    }

    private fun crearTimestamp(dia: Int, mes: Int, año: Int): Long{
        var timestamp = Timestamp(año, mes, dia, 0, 0, 0, 0)
        var milisegundos: Long = timestamp.toInstant().toEpochMilli()
        return milisegundos
    }

}
