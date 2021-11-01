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
import kotlinx.android.synthetic.main.principal_modificacion_activity.*

class HistorialActivity : AppCompatActivity(), DatosAdapter.OnDocumentoDatosClickListener {

    private val database = FirebaseFirestore.getInstance()
    private lateinit var datosRecyclerview: RecyclerView
    private lateinit var listaDocumentoDatos: ArrayList<DocumentoDatos>
    private var posicionItem = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial_activity)
        editTextDateHistorial.setInputType(InputType.TYPE_NULL);
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


        editTextDateHistorial.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
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
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateModificacion.setText("$day-$month1-$year")
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
        if(editTextDateModificacion.text.isNotEmpty()){
            val coleccionFechas = database.collection(email)
            coleccionFechas.whereEqualTo("fecha", editTextDateModificacion.text.toString()).get().addOnSuccessListener {documents ->
                for (document in documents) {
                    Log.d("Registro", "${document.id} => ${document.data}")
                }
                listaDocumentoDatos = parsearDatos(documents)
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

    override fun onItemClick(item: DocumentoDatos) {
        TODO("Not yet implemented")
    }

}
