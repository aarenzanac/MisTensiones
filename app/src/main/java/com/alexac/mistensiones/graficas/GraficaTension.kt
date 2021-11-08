package com.alexac.mistensiones.graficas

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.R
import com.alexac.mistensiones.fecha_hora.DatePickerFragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.grafica.*
import kotlinx.android.synthetic.main.historial_activity.*
import java.security.KeyStore


class GraficaTension: AppCompatActivity(){

    private val database = FirebaseFirestore.getInstance()
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
        /// mensaje de prueba para firestore functions commit


        val bundle = intent.extras
        val email = bundle?.getString("email")

        if (email != null) {
            database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener {
                textViewNombreLogueadoPantallaHistorial.setText(it.get("nombre") as String?)
            }
            setup(email)

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


        }

    }

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
        diaFinal = day.toInt()+1
        mesFinal = month.toInt()
        añoFinal = year.toInt()-1900
    }

    private fun setlineChartData(){
        val xvalue = ArrayList<String>()
        xvalue.add("11:00")
        xvalue.add("12:00")
        xvalue.add("13:00")
        xvalue.add("14:00")
        xvalue.add("15:00")
        xvalue.add("16:00")

        val lineentry = ArrayList<Entry>()
        lineentry.add(Entry(20f, 0f))
        lineentry.add(Entry(50f, 1f))
        lineentry.add(Entry(10f, 2f))
        lineentry.add(Entry(30f, 3f))
        lineentry.add(Entry(10f, 4f))
        lineentry.add(Entry(50f,5f))

        val linedataset = LineDataSet(lineentry, "First")
        linedataset.color = resources.getColor(R.color.purple_700)


        val data = LineData(linedataset)

        graficatension.data = data
        graficatension.setBackgroundColor(resources.getColor(R.color.white))
        graficatension.animateXY(3000, 3000)


    }
}