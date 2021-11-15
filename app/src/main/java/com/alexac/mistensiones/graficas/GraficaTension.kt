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
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
    var tipoDatos: String = ""
    var arrayFiltrado: ArrayList<DocumentoDatos> = arrayListOf()
    var arrayFecha = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.graficaresponsive)
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
            mostrarTodo(email, "tensiones")

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
            filtrar(email, "tensiones")
            radioButtonTensiones.isChecked = true

        }

        radioButtonTensiones.setOnClickListener {
            tipoDatos = "tensiones"
            if(editTextDateGraficaInicio.text.isEmpty() && editTextDateGraficaFinal.text.isEmpty()){
                mostrarTodo(email, tipoDatos)
            }else{
                filtrar(email, tipoDatos)
            }
        }

        radioButtonPeso.setOnClickListener {
            tipoDatos = "peso"
            if(editTextDateGraficaInicio.text.isEmpty() && editTextDateGraficaFinal.text.isEmpty()){
                mostrarTodo(email, tipoDatos)
            }else{
                filtrar(email, tipoDatos)
            }
        }

        radioButtonOxigeno.setOnClickListener {
            tipoDatos = "oxigeno"
            if(editTextDateGraficaInicio.text.isEmpty() && editTextDateGraficaFinal.text.isEmpty()){
                mostrarTodo(email, tipoDatos)
            }else{
                filtrar(email, tipoDatos)
            }
        }

        radioButtonGlucosa.setOnClickListener {
            tipoDatos = "glucosa"
            if(editTextDateGraficaInicio.text.isEmpty() && editTextDateGraficaFinal.text.isEmpty()){
                mostrarTodo(email, tipoDatos)
            }else{
                filtrar(email, tipoDatos)
            }
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
    private fun mostrarTodo(email: String, tipoDatos: String){

        val coleccionTotal = database.collection(email)
        coleccionTotal.get().addOnSuccessListener { documents ->
            /*for (document in documents) {
                Log.d("Registro", "${document.id} => ${document.data}")
            }*/
            listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
            var listaDocumentoDatosOrdenada = funcionesVarias.ordenarMenorAMayor(listaDocumentoDatos)
            Log.d("Registro", "EXTRACCION DE GRAFICA ACTIVITY ---- Numero de elementos: ${listaDocumentoDatos.size}")
            setLineChartData(listaDocumentoDatosOrdenada, tipoDatos)
        }

    }

    // FILTRA LOS DATOS EN FUNCIÓN DEL MAIL Y DE LA FECHA SELECCIONADA
    private fun filtrar(email: String, tipoDatos: String){
        var listaDocumentosFiltrados = arrayListOf<DocumentoDatos>()
        if(editTextDateGraficaInicio.text.isNotEmpty() && editTextDateGraficaFinal.text.isNotEmpty()){
            var timestampInicio = funcionesVarias.crearTimestamp(diaInicio, mesInicio, añoInicio)
            var timestampFinal = funcionesVarias.crearTimestamp(diaFinal, mesFinal, añoFinal)
            if(timestampFinal < timestampInicio){
                Toast.makeText(this, "LA FECHA FINAL NO PUEDE SER MENOR QUE LA INICIAL.", Toast.LENGTH_SHORT).show()
            }else {
                val coleccionFechas = database.collection(email)
                coleccionFechas.get().addOnSuccessListener { documents ->
                    /*for (document in documents) {
                    Log.d("Registro", "${document.id} => ${document.data}")
                }*/
                    listaDocumentoDatos = funcionesVarias.parsearDatos(documents)
                    listaDocumentosFiltrados = aplicarFiltroFechas(listaDocumentoDatos)
                    var listaDocumentoDatosOrdenada = funcionesVarias.ordenarMenorAMayor(listaDocumentosFiltrados)


                    Log.d("Registro", "ARRAY DE DATOS PARA GRAFICA filtrados---- Numero de elementos: ${listaDocumentosFiltrados.size}")
                    setLineChartData(listaDocumentosFiltrados, tipoDatos)
                }
            }
        }else {
            Toast.makeText(this, "DEBE SELECCIONAR FECHA DE INICIO Y FINAL PARA FILTRAR.", Toast.LENGTH_SHORT).show()
        }
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


    private fun setLineChartData(listaDatosChart: ArrayList<DocumentoDatos>, tipoDatos: String){
        Log.d("Registro", "ARRAY DE DATOS PARA GRAFICA ---- Numero de elementos: ${listaDatosChart.size}")
        val arrayTensionesSistolicas = arrayListOf<Float>()
        val arrayTensionesDiastolicas = arrayListOf<Float>()
        val arrayOxigenacion = arrayListOf<Float>()
        val arrayPeso = arrayListOf<Float>()
        val arrayGlucemia = arrayListOf<Float>()
        arrayFecha = arrayListOf<String>()
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

        var ejeX: XAxis = graficatension.xAxis
        ejeX.valueFormatter = MyAxisFormatter()


        val lineDataSetSistolica = LineDataSet(entrySistolicas, "Sistólica nmmHg")
        lineDataSetSistolica.color = Color.RED
        lineDataSetSistolica.setDrawValues(true)
        lineDataSetSistolica.setAxisDependency (YAxis.AxisDependency.LEFT)

        val lineDataSetDiastolica = LineDataSet(entryDiastolicas, "Diastólica mmHg")
        lineDataSetSistolica.color = Color.BLUE
        lineDataSetSistolica.setDrawValues(true)
        lineDataSetSistolica.setAxisDependency (YAxis.AxisDependency.LEFT)

        val dataSets = arrayListOf(lineDataSetSistolica, lineDataSetDiastolica)

        val lineDataSetPeso = LineDataSet(entryPeso, "Peso Kg.")
        lineDataSetPeso.color = Color.BLUE
        lineDataSetPeso.setDrawValues(true)
        lineDataSetPeso.setAxisDependency (YAxis.AxisDependency.LEFT)

        val lineDataSetOxigeno = LineDataSet(entryOxigenacion, "Oxigenación %")
        lineDataSetOxigeno.color = Color.BLUE
        lineDataSetOxigeno.setDrawValues(true)
        lineDataSetOxigeno.setAxisDependency (YAxis.AxisDependency.LEFT)

        val lineDataSetGlucosa = LineDataSet(entryGlucemia, "Glucosa mg/dl")
        lineDataSetGlucosa.color = Color.BLUE
        lineDataSetGlucosa.setDrawValues(true)
        lineDataSetGlucosa.setAxisDependency (YAxis.AxisDependency.LEFT)


        when(tipoDatos){
            "tensiones" -> {val lineData = LineData(dataSets as List<ILineDataSet>?)
                graficatension.data = lineData}

            "peso" -> {val lineData = LineData(lineDataSetPeso)
                graficatension.data = lineData}

            "oxigeno" -> {val lineData = LineData(lineDataSetOxigeno)
                graficatension.data = lineData}

            "glucosa" -> {val lineData = LineData(lineDataSetGlucosa)
                graficatension.data = lineData}
        }

        graficatension.setBackgroundColor(Color.WHITE)
        graficatension.animateXY(1000, 1000)
        graficatension.invalidate()

    }

    //FUNCION PARA CAMBIAR LOS VALORES DEL EJE DE LAS X PONIENDO LAS FECHAS
    inner class MyAxisFormatter : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < arrayFecha.size) {
                arrayFecha[index]
            } else {
                ""
            }
        }
    }
}



