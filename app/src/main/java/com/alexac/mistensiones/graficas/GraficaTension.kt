package com.alexac.mistensiones.graficas

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.R
import com.alexac.mistensiones.fecha_hora.DatePickerFragment
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.grafica.*
import kotlinx.android.synthetic.main.grafica.graficatension
import kotlinx.android.synthetic.main.graficaprueba.*
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
        setContentView(R.layout.graficaprueba)
        //editTextDateGraficaInicio.setInputType(InputType.TYPE_NULL);
        //editTextDateGraficaFinal.setInputType(InputType.TYPE_NULL);


        val bundle = intent.extras
        val email = bundle?.getString("email")

        setlineChartData()

        /*if (email != null) {
            database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener {
                textViewNombreLogueadoGrafica.setText(it.get("nombre") as String?)
            }
            setup(email)
        }*/
    }



    /*private fun setup(email: String){


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
*/
    private fun setlineChartData(){

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

        gra.data = data
        gra.setBackgroundColor(resources.getColor(R.color.white))
        gra.animateXY(3000, 3000)


    }
}