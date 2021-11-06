package com.alexac.mistensiones

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.funciones_varias.CargarPreferenciasCompartidas.Companion.preferenciasCompartidas
import kotlinx.android.synthetic.main.opciones_activity.*

class OpcionesActivity : AppCompatActivity() {

    private val avisoTension = "tension"
    private val avisoIMC = "imc"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.opciones_activity)

        val bundle = intent.extras
        val nombre = bundle?.getString("nombre")
        textViewNombreLogueadoOpciones.setText(nombre)
        if (nombre != null) {
            setup()
        }
    }


    private fun setup() {

        if(preferenciasCompartidas.recuperarPrefenenciaTension() == true){
            switchTension.isChecked = true
        }else{
            switchTension.isChecked = false
        }

        if (preferenciasCompartidas.recuperarPrefenenciaIMC() == true){
            switchIMC.isChecked = true
        }else{
            switchIMC.isChecked = false
        }

        button_modificar_opciones.setOnClickListener {
            goPrincipalActivity()
        }

        switchTension.setOnClickListener{
            if(switchTension.isChecked){
                preferenciasCompartidas.guardarPreferenciaTension(true)
            }else{
                preferenciasCompartidas.guardarPreferenciaTension(false)
            }
        }
        switchIMC.setOnClickListener {
            if(switchIMC.isChecked){
                preferenciasCompartidas.guardarPreferenciaIMC(true)
            }else{
                preferenciasCompartidas.guardarPreferenciaIMC(false)
            }
        }
    }


    //FUNCION PARA ACCEDER A LA PANTALLA DE INICIO
    private fun goPrincipalActivity() {
        onBackPressed()
    }
}
