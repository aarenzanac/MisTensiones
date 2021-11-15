package com.alexac.mistensiones

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexac.mistensiones.funciones_varias.CargarPreferenciasCompartidas.Companion.preferenciasCompartidas
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.opciones_activity.*
import kotlinx.android.synthetic.main.opciones_activity.button_modificar_opciones
import kotlinx.android.synthetic.main.opciones_activity.switchIMC
import kotlinx.android.synthetic.main.opciones_activity.switchPush
import kotlinx.android.synthetic.main.opciones_activity.switchTension
import kotlinx.android.synthetic.main.opciones_activity.textViewNombreLogueadoOpciones
import kotlinx.android.synthetic.main.opciones_activity_responsive.*

class OpcionesActivity : AppCompatActivity() {

    lateinit var suscrito: Task<Void>


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.opciones_activity_responsive)

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

        if (preferenciasCompartidas.recuperarPrefenenciaSuscripcionPush() == true){
            switchPush.isChecked = true
        }else{
            switchPush.isChecked = false
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

        switchPush.setOnClickListener {
            if(switchPush.isChecked){
                preferenciasCompartidas.guardarPreferenciaSuscripcionPush(true)
                notification()
            }else{
                preferenciasCompartidas.guardarPreferenciaSuscripcionPush(false)
                noNotificaion()
            }
        }
    }

    private fun notification(){
        suscrito = FirebaseMessaging.getInstance().subscribeToTopic("suscritos")
    }

    private fun noNotificaion(){

        suscrito =  FirebaseMessaging.getInstance().unsubscribeFromTopic("suscritos");
    }


    //FUNCION PARA ACCEDER A LA PANTALLA DE INICIO
    private fun goPrincipalActivity() {
        onBackPressed()
    }
}
