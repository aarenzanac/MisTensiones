package com.alexac.mistensiones.graficas

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.R
import com.alexac.mistensiones.fecha_hora.DatePickerFragment
import com.alexac.mistensiones.funciones_varias.FuncionesVarias
import com.alexac.mistensiones.models.DocumentoDatos
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.grafica.*


class GraficaTension: AppCompatActivity(){

    private val database = FirebaseFirestore.getInstance()
    val funcionesVarias: FuncionesVarias = FuncionesVarias()
    lateinit var listaDocumentoDatos: ArrayList<DocumentoDatos>
    var diaInicio = 0
    var mesInicio = 0
    var añoInicio = 0
    var diaFinal = 0
    var mesFinal = 0
    var añoFinal = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.grafica)
        editTextDateGraficaInicio.setInputType(InputType.TYPE_NULL);
        editTextDateGraficaFinal.setInputType(InputType.TYPE_NULL);


        val bundle = intent.extras
        val email = bundle?.getString("email")



        if (email != null) {
            database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener {
                textViewNombreLogueadoGrafica.setText(it.get("nombre") as String?)
            }
            setup(email)
            mostrarTodo(email)
        }
    }



    private fun setup(email: String){


        editTextDateGraficaInicio.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelectedInicio(day, month, year) }
            datePicker.show(supportFragmentManager, "datePicker")
        }

        editTextDateGraficaFinal.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelectedFinal(day, month, year) }
            datePicker.show(supportFragmentManager, "datePicker")
        }

        imageButtonVolverGrafica.setOnClickListener {
            onBackPressed()
        }

        imageViewFiltrarGrafica.setOnClickListener {
            filtrar(email)
        }


    }

    private fun onDateSelectedInicio(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateGraficaInicio.setText("$day-$month1-$year")
        diaInicio = day.toInt()
        mesInicio = month.toInt()
        añoInicio = year.toInt()-1900
    }

    //SETEA EL EDIT TEXT DE FECHA CON LA FECHA SELECCIONADA
    private fun onDateSelectedFinal(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateGraficaFinal.setText("$day-$month1-$year")
        diaFinal = day.toInt()+1
        mesFinal = month.toInt()
        añoFinal = year.toInt()-1900
    }

    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL
    private fun mostrarTodo(email: String){

        val coleccionFechas = database.collection(email)
        coleccionFechas.get().addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d("Registro", "${document.id} => ${document.data}")
            }
            listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
        }
    }

    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL Y DE LA FECHA SELECCIONADA
    private fun filtrar(email: String): ArrayList<DocumentoDatos>{
        var listaDocumentosFiltrados = arrayListOf<DocumentoDatos>()
        if(editTextDateGraficaInicio.text.isNotEmpty() && editTextDateGraficaFinal.text.isNotEmpty()){
            val coleccionFechas = database.collection(email)
            coleccionFechas.get().addOnSuccessListener {documents ->
                for (document in documents) {
                    Log.d("Registro", "${document.id} => ${document.data}")
                }
                listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
                listaDocumentosFiltrados = aplicarFiltroFechas(listaDocumentoDatos)
            }

        }else {
            Toast.makeText(this, "DEBE SELECCIONAR UNA FECHA PARA FILTRAR.", Toast.LENGTH_SHORT).show()
        }
        return listaDocumentosFiltrados
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


    private fun setLineChartData(listaDatosChart: ArrayList<DocumentoDatos>){
        val arrayTensionesSistolicas: ArrayList<Double> = arrayListOf()
        val arrayTensionesDiastolicas: ArrayList<Double> = arrayListOf()
        val arrayOxigenacion: ArrayList<Int> = arrayListOf()
        val arrayPeso: ArrayList<Double> = arrayListOf()
        val arrayGlucemia: ArrayList<Double> = arrayListOf()
        val arrayFecha: ArrayList<String> = arrayListOf()
        for(dato in listaDatosChart){
            arrayTensionesSistolicas.add(dato.sistolica)
            arrayTensionesDiastolicas.add(dato.diastolica)
            arrayOxigenacion.add(dato.oxigenacion.toInt())
            arrayPeso.add(dato.peso)
            arrayGlucemia.add(dato.glucosa)
            arrayFecha.add(dato.fecha)
        }


    }
    /*private fun setlineChartData(){

        val lineentry = ArrayList<Entry>()
        //el eje x sería los días y el y las tensiones
        lineentry.add(Entry(1f, 0f))
        lineentry.add(Entry(2f, 126f))
        lineentry.add(Entry(3f, 144f))
        lineentry.add(Entry(4f, 118f))
        lineentry.add(Entry(5f, 148f))
        lineentry.add(Entry(6f, 133f))

        val linedataset = LineDataSet(lineentry, "First")
        linedataset.color = resources.getColor(R.color.purple_700)


        val data = LineData(linedataset)

        graficatension.data = data
        graficatension.setBackgroundColor(resources.getColor(R.color.white))
        graficatension.animateXY(3000, 3000)
    }*/
}