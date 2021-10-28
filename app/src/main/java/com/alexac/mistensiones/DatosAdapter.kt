package com.alexac.mistensiones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DatosAdapter(private val listaDocumentoDatos: ArrayList<DocumentoDatos>): RecyclerView.Adapter<DatosAdapter.DatosHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatosHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.datos_item, parent, false)
        return DatosHolder(itemView)
    }

    override fun onBindViewHolder(holder: DatosHolder, position: Int) {
        val itemActual = listaDocumentoDatos[position]

        holder.fecha.text = itemActual.fecha
        holder.hora.text = itemActual.hora
        holder.sistolica.text = itemActual.sistolica.toString()
        holder.diastolica.text = itemActual.diastolica.toString()
        holder.peso.text = itemActual.peso.toString()
        holder.oxigenacion.text = itemActual.oxigenacion.toString()


    }

    override fun getItemCount(): Int {
        return listaDocumentoDatos.size
    }

    class DatosHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val fecha: TextView = itemView.findViewById(R.id.textViewRecyclerFecha)
        val hora: TextView = itemView.findViewById(R.id.textViewRecyclerHora)
        val sistolica: TextView = itemView.findViewById(R.id.textViewRecyclerSistolica)
        val diastolica: TextView = itemView.findViewById(R.id.textViewRecyclerDiastolica)
        val peso: TextView = itemView.findViewById(R.id.textViewRecyclerPeso)
        val oxigenacion: TextView = itemView.findViewById(R.id.textViewRecyclerOxigenacion)
    }
}