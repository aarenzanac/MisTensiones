package com.alexac.mistensiones.graficas

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.R
import com.alexac.mistensiones.fecha_hora.DatePickerFragment
import com.alexac.mistensiones.funciones_varias.FuncionesVarias
import com.alexac.mistensiones.models.DocumentoDatos
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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
        listaDocumentoDatos = arrayListOf<DocumentoDatos>()


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
            var arrayFiltrado = filtrar(email)
            setLineChartData(arrayFiltrado)
        }
    }

    private fun onDateSelectedInicio(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateGraficaInicio.setText("$day-$month1-$year")
        diaInicio = day.toInt()
        mesInicio = month1.toInt()
        añoInicio = year.toInt()-1900
    }

    //SETEA EL EDIT TEXT DE FECHA CON LA FECHA SELECCIONADA
    private fun onDateSelectedFinal(day: Int, month: Int, year: Int) {
        val month1 = month + 1 // PORQUE EL MES 0 ES ENERO
        editTextDateGraficaFinal.setText("$day-$month1-$year")
        diaFinal = day.toInt()+1
        mesFinal = month1.toInt()
        añoFinal = year.toInt()-1900
    }

    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL
    private fun mostrarTodo(email: String){

        val coleccionTotal = database.collection(email)
        coleccionTotal.get().addOnSuccessListener { documents ->
            /*for (document in documents) {
                Log.d("Registro", "${document.id} => ${document.data}")
            }*/
            listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
            Log.d("Registro", "EXTRACCION DE GRAFICA ACTIVITY ---- Numero de elementos: ${listaDocumentoDatos.size}")
            setLineChartData(listaDocumentoDatos)
        }

    }

    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL Y DE LA FECHA SELECCIONADA
    private fun filtrar(email: String): ArrayList<DocumentoDatos>{
        var listaDocumentosFiltrados = arrayListOf<DocumentoDatos>()
        if(editTextDateGraficaInicio.text.isNotEmpty() && editTextDateGraficaFinal.text.isNotEmpty()){
            val coleccionFechas = database.collection(email)
            coleccionFechas.get().addOnSuccessListener {documents ->
                /*for (document in documents) {
                    Log.d("Registro", "${document.id} => ${document.data}")
                }*/
                listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
                listaDocumentosFiltrados = aplicarFiltroFechas(listaDocumentoDatos)
            }

        }else {
            Toast.makeText(this, "DEBE SELECCIONAR UNA FECHA PARA FILTRAR.", Toast.LENGTH_SHORT).show()
        }
        return listaDocumentosFiltrados
    }


    //CREA UN ARRAY CON LOS ELEMENTOS COMPRENDIDOS ENTRE EL TIMESTAMP INICIO Y FINAL SELECCIONADO.
    private fun aplicarFiltroFechas(listaDocumentoDatosBrutos: ArrayList<DocumentoDatos>): ArrayList<DocumentoDatos>{
        var timestampInicio = funcionesVarias.crearTimestamp(diaInicio, mesInicio, añoInicio)
        var timestampFinal = funcionesVarias.crearTimestamp(diaFinal, mesFinal, añoFinal)
        var arrayFiltrado = arrayListOf<DocumentoDatos>()
        for(doc in listaDocumentoDatosBrutos){
            if(doc.timestamp >= timestampInicio && doc.timestamp <= timestampFinal){
                arrayFiltrado.add(doc)
            }
        }
        return arrayFiltrado
    }


    private fun setLineChartData(listaDatosChart: ArrayList<DocumentoDatos>){
        Log.d("Registro", "ARRAY DE DATOS PARA GRAFICA ---- Numero de elementos: ${listaDatosChart.size}")
        val arrayTensionesSistolicas = arrayListOf<Float>()
        val arrayTensionesDiastolicas = arrayListOf<Float>()
        val arrayOxigenacion = arrayListOf<Float>()
        val arrayPeso = arrayListOf<Float>()
        val arrayGlucemia = arrayListOf<Float>()
        val arrayFecha = arrayListOf<String>()
        for(dato in listaDatosChart){
            arrayTensionesSistolicas.add(dato.sistolica.toFloat())
            arrayTensionesDiastolicas.add(dato.diastolica.toFloat())
            arrayOxigenacion.add(dato.oxigenacion.toFloat())
            arrayPeso.add(dato.peso.toFloat())
            arrayGlucemia.add(dato.glucosa.toFloat())
            arrayFecha.add(dato.fecha)
        }

        //CON LOS ARRAYS DE DATOS, CREO LOS PUNTOS DE LA GRÁFICA
        val entrySistolicas = arrayTensionesSistolicas.mapIndexed { index, arrayList ->
            Entry(index.toFloat(), arrayTensionesSistolicas[index])
        }

        val entryDiastolicas = arrayTensionesDiastolicas.mapIndexed { index, arrayList ->
            Entry(index.toFloat(), arrayTensionesDiastolicas[index])
        }

        val entryOxigenacion = arrayOxigenacion.mapIndexed { index, arrayList ->
            Entry(index.toFloat(), arrayOxigenacion[index])
        }

        val entryPeso = arrayPeso.mapIndexed { index, arrayList ->
            Entry(index.toFloat(), arrayPeso[index])
        }

        val entryGlucemia= arrayGlucemia.mapIndexed { index, arrayList ->
            Entry(index.toFloat(), arrayGlucemia[index])
        }


        val lineDataSetSistolica = LineDataSet(entrySistolicas, "Sistólica")
        lineDataSetSistolica.color = Color.RED
        lineDataSetSistolica.setDrawValues(false)
        lineDataSetSistolica.setAxisDependency (YAxis.AxisDependency.LEFT)

        val lineDataSetDiastolica = LineDataSet(entryDiastolicas, "Diastólica")
        lineDataSetSistolica.color = Color.BLUE
        lineDataSetSistolica.setDrawValues(false)
        lineDataSetSistolica.setAxisDependency (YAxis.AxisDependency.LEFT)

        val dataSets = arrayListOf(lineDataSetSistolica, lineDataSetDiastolica)



        val lineData = LineData(dataSets as List<ILineDataSet>?)

        graficatension.data = lineData
        graficatension.setBackgroundColor(Color.WHITE)
        graficatension.animateXY(3000, 3000)

        graficatension.invalidate()
    }
}

