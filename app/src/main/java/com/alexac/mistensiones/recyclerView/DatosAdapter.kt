package com.alexac.mistensiones.recyclerView

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexac.mistensiones.R
import com.alexac.mistensiones.funciones_varias.CargarPreferenciasCompartidas
import com.alexac.mistensiones.models.DocumentoDatos
import kotlinx.android.synthetic.main.datos_item.view.*

class DatosAdapter(private val listaDocumentoDatos: ArrayList<DocumentoDatos>, private val context: Context, private val itemClickListener: OnDocumentoDatosClickListener):  RecyclerView.Adapter<ViewHolderBasico<*>>(){

    interface OnDocumentoDatosClickListener{
        fun onItemClick(item: DocumentoDatos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBasico<*>{
        val itemView = LayoutInflater.from(context).inflate(R.layout.datos_item, parent, false)
        return DatosHolder(itemView)

    }

    override fun getItemCount(): Int {
        return listaDocumentoDatos.size
    }

    override fun onBindViewHolder(holder: ViewHolderBasico<*>, position: Int) {
        //WHEN PARA PODER ADAPTAR ESTE ADAPTER A DIFERENTES ITEMSVIEWS
        when(holder){
            is DatosHolder -> holder.bind(listaDocumentoDatos[position], position)
            else -> throw IllegalArgumentException("No se ha pasado viewholder en el bind")
        }
    }


    inner class DatosHolder(itemView: View): ViewHolderBasico<DocumentoDatos>(itemView) {

        override fun bind(item: DocumentoDatos, position: Int) {

            itemView.setOnClickListener { itemClickListener.onItemClick(item) }
            itemView.textViewRecyclerFecha.text = item.fecha
            itemView.textViewRecyclerHora.text = item.hora
            itemView.textViewRecyclerSistolica.text = item.sistolica.toString()
            itemView.textViewRecyclerDiastolica.text = item.diastolica.toString()
            itemView.textViewRecyclerOxigenacion.text = item.oxigenacion.toString()
            itemView.textViewRecyclerPeso.text = item.peso.toString()
            itemView.textViewRecyclerGlucemia.text = item.glucosa.toString()
            itemView.textViewRecyclerObservaciones.text = item.observaciones
            if(item.sistolica.toDouble() >=150 || item.diastolica.toDouble() >= 90.0){
                itemView.textViewSemaforo.setBackgroundColor(Color.parseColor("#E14336"))
            }else if(item.sistolica.toDouble() <= 130.0 && item.diastolica.toDouble() <= 75.0){
                itemView.textViewSemaforo.setBackgroundColor(Color.parseColor("#95D328"))
            }else{
                itemView.textViewSemaforo.setBackgroundColor(Color.parseColor("#E1BD36"))
            }
            val altura: Int = CargarPreferenciasCompartidas.preferenciasCompartidas.recuperarPrefenenciaAltura()
            val imc = item.peso / ((altura/100)*(altura/100))
            if(imc >= 27){
                itemView.textViewSemaforoIMC.setBackgroundColor(Color.parseColor("#E14336"))
            }else if(imc <= 19){
                itemView.textViewSemaforoIMC.setBackgroundColor(Color.parseColor("#E1BD36"))
            }else{
                itemView.textViewSemaforoIMC.setBackgroundColor(Color.parseColor("#95D328"))
            }
            val timestamp = item.timestamp
            val posicion = position
        }
    }
}

