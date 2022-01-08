package com.alexac.mistensiones.graficas

import android.Manifest
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.grafica.editTextDateGraficaFinal
import kotlinx.android.synthetic.main.grafica.editTextDateGraficaInicio
import kotlinx.android.synthetic.main.grafica.graficatension
import kotlinx.android.synthetic.main.grafica.imageButtonVolverGrafica
import kotlinx.android.synthetic.main.grafica.imageViewFiltrarGrafica
import kotlinx.android.synthetic.main.grafica.radioButtonGlucosa
import kotlinx.android.synthetic.main.grafica.radioButtonOxigeno
import kotlinx.android.synthetic.main.grafica.radioButtonPeso
import kotlinx.android.synthetic.main.grafica.radioButtonTensiones
import kotlinx.android.synthetic.main.grafica.textViewNombreLogueadoGrafica
import kotlinx.android.synthetic.main.graficaresponsive.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


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
    val permisos = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    lateinit var bitmap: Bitmap
    lateinit var bitmapEscalado: Bitmap
    val anchoPagina: Int = 1200
    lateinit var listaDocumentoDatosOrdenada: ArrayList<DocumentoDatos>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.graficaresponsive)
        editTextDateGraficaInicio.setInputType(InputType.TYPE_NULL);
        editTextDateGraficaFinal.setInputType(InputType.TYPE_NULL);
        listaDocumentoDatos = arrayListOf<DocumentoDatos>()
        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.titulo)
        bitmapEscalado = Bitmap.createScaledBitmap(bitmap, 900, 250, false)



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

        imageButtonPdf.setOnClickListener {
            //SOLICITA LOS PERMISOS SI NO LOS TIENE. SI LOS TIENE, CONTINUA SIN PEDIRLOS

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //PERMISO NO ACEPTADO POR EL MOMENTO
                requestStoragePermission()
            }else{
                //PERMISOS ACEPTADOS Y CREO EL PDF
                generarPDF()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 777){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                generarPDF()
            }else{
                //PERMISO NO ACEPTADO
                Toast.makeText(this, "PERMISOS RECHAZADOS POR PRIMERA VEZ.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //EL USUARIO HA RECHAZADO LOS PERMISOS Y DEBERÁ AUTORIZARLOS DESDE LA CONFIGURACION DEL MÓVIL
            Toast.makeText(this, "EL PERMISO HA SIDO RECHAZADO.", Toast.LENGTH_SHORT).show()
        }else{
            //PEDIMOS EL PERMISO
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 777)
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
            listaDocumentoDatosOrdenada = funcionesVarias.ordenarMenorAMayor(listaDocumentoDatos)
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
                    listaDocumentoDatosOrdenada = funcionesVarias.ordenarMenorAMayor(listaDocumentosFiltrados)


                    Log.d("Registro", "ARRAY DE DATOS PARA GRAFICA filtrados---- Numero de elementos: ${listaDocumentosFiltrados.size}")
                    setLineChartData(listaDocumentoDatosOrdenada, tipoDatos)
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
        lineDataSetSistolica.valueTextSize = 8F
        lineDataSetSistolica.setAxisDependency(YAxis.AxisDependency.LEFT)

        val lineDataSetDiastolica = LineDataSet(entryDiastolicas, "Diastólica mmHg")
        lineDataSetDiastolica.color = Color.BLUE
        lineDataSetDiastolica.valueTextSize = 8F
        lineDataSetDiastolica.setDrawValues(true)
        lineDataSetDiastolica.setAxisDependency(YAxis.AxisDependency.LEFT)

        val dataSets = arrayListOf(lineDataSetSistolica, lineDataSetDiastolica)

        val lineDataSetPeso = LineDataSet(entryPeso, "Peso Kg.")
        lineDataSetPeso.color = Color.BLUE
        lineDataSetPeso.valueTextSize = 8F
        lineDataSetPeso.setDrawValues(true)
        lineDataSetPeso.setAxisDependency(YAxis.AxisDependency.LEFT)

        val lineDataSetOxigeno = LineDataSet(entryOxigenacion, "Oxigenación %")
        lineDataSetOxigeno.color = Color.BLUE
        lineDataSetOxigeno.valueTextSize = 8F
        lineDataSetOxigeno.setDrawValues(true)
        lineDataSetOxigeno.setAxisDependency(YAxis.AxisDependency.LEFT)

        val lineDataSetGlucosa = LineDataSet(entryGlucemia, "Glucosa mg/dl")
        lineDataSetGlucosa.color = Color.BLUE
        lineDataSetGlucosa.valueTextSize = 8F
        lineDataSetGlucosa.setDrawValues(true)
        lineDataSetGlucosa.setAxisDependency(YAxis.AxisDependency.LEFT)


        when(tipoDatos){
            "tensiones" -> {
                val lineData = LineData(dataSets as List<ILineDataSet>?)
                graficatension.data = lineData
            }

            "peso" -> {
                val lineData = LineData(lineDataSetPeso)
                graficatension.data = lineData
            }

            "oxigeno" -> {
                val lineData = LineData(lineDataSetOxigeno)
                graficatension.data = lineData
            }

            "glucosa" -> {
                val lineData = LineData(lineDataSetGlucosa)
                graficatension.data = lineData
            }
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

    private fun generarPDF(){

        val nombreArchivo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val contextWrapper: ContextWrapper = ContextWrapper(applicationContext)
        val directorioDocumentos = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        val archivo: File = File (directorioDocumentos, nombreArchivo + ".pdf")
        var docPdf: PdfDocument = PdfDocument()

        //CREO LOS DATOS
        val stringDatos: String = montarDatos(listaDocumentoDatosOrdenada)


        val lineasDatos: ArrayList<String> = arrayListOf<String>()

        var saltoLinea: Int = 0
        var datosPorHoja: String = ""
        for (line in stringDatos.split("\n")) {
            datosPorHoja += line + "\n"
            saltoLinea += 1
            if(saltoLinea == 18){
                lineasDatos.add(datosPorHoja)
                datosPorHoja = ""
                saltoLinea = 0
            }
        }
        lineasDatos.add(datosPorHoja)

        try{
            docPdf = crearPagina(lineasDatos)
            docPdf.writeTo(FileOutputStream(archivo.path, true))
            docPdf.close()
            Toast.makeText(this, "DOCUMENTO " + nombreArchivo.toString() + ".pdf " + "CREADO CON ÉXITO.", Toast.LENGTH_SHORT).show()
        }catch (e: IOException){
            Toast.makeText(this, "ERROR CREANDO ARCHIVO. " + e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun montarDatos(listaDocumentoDatosOrdenada: ArrayList<DocumentoDatos>): String{
        var datosMontados = ""
        for(dato in listaDocumentoDatosOrdenada){
            val diaDatos: String = "Fecha: " + dato.fecha + " - - - - Hora: " + dato.hora + "\nSistólica: " + dato.sistolica + " mmHg - - - - Diastólica: " + dato.diastolica + " mmHg\nGlucemia: " + dato.glucosa + " mg/dl - - - - Oxigenación: " + dato.oxigenacion + " % - - - - Peso: " + dato.peso + " Kg.\n"
            datosMontados = datosMontados + diaDatos
        }
        //Log.d("Registro", datosMontados)

        return datosMontados
    }

    private fun crearPagina(lineasDatos: ArrayList<String>): PdfDocument{
        var numeroPagina: Int = 1
        val documentoPdf: PdfDocument = PdfDocument()
        val logo: Paint = Paint()    //PAINT DEL LOGOTIPO
        val titulo: Paint = Paint()  //PAINT DEL TÍTULO
        val datos: Paint = Paint()   //PAINT DE LOS DATOS
        val lineaSeparadora: Paint = Paint()   //PAINT DE LA LINEA SEPARADORA

        val margenIzquierdo: Float = 40F
        var margenSuperior: Float = 380F


        for(hojaDatos in lineasDatos){
            val miPaginaInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(1200, 2010, numeroPagina).create()
            var miPagina: PdfDocument.Page = documentoPdf.startPage(miPaginaInfo)
            val canvas: Canvas = miPagina.canvas
            //PINTO EL LOGOTIPO
            canvas.drawBitmap(bitmapEscalado, 150F, 0F, logo)
            //PINTO Y FORMATEO EL TÍTULO
            titulo.textAlign = Paint.Align.CENTER
            titulo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            titulo.textSize = 70F
            canvas.drawText("INFORME PDF -- Pag: " + numeroPagina + "/" + lineasDatos.size, anchoPagina/2F, 300F, titulo)
            //PINTO Y FORMATEO LOS DATOS
            datos.textAlign = Paint.Align.LEFT
            datos.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            datos.textSize = 35F

            var contadorSeparador: Int = 0
            for (line in hojaDatos.split("\n")) {
                contadorSeparador += 1
                canvas.drawText(line, margenIzquierdo, margenSuperior, datos);
                margenSuperior += 60F
                if(contadorSeparador == 3){
                    canvas.drawLine(20F, margenSuperior, 1100F, margenSuperior, lineaSeparadora)
                    margenSuperior += 70F
                    contadorSeparador = 0
                }
            }
            documentoPdf.finishPage(miPagina)
            numeroPagina += 1
            margenSuperior = 380F
        }
        return documentoPdf
    }
}



