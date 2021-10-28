package com.alexac.mistensiones

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class DatosAdapter(private val listaDocumentoDatos: ArrayList<DocumentoDatos>, private val context: Context): RecyclerView.Adapter<DatosAdapter.DatosHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatosHolder {

        val itemView = LayoutInflater.from(context).inflate(R.layout.datos_item, parent, false)
        itemView.setOnClickListener {
            Toast.makeText(context, "CLICK", Toast.LENGTH_SHORT).show()
        }
        return DatosHolder(itemView)
    }


    override fun onBindViewHolder(holder: DatosHolder, position: Int) {
        val itemActual = listaDocumentoDatos[position]
        holder.fecha.setText(itemActual.fecha)
        holder.hora.setText(itemActual.hora)
        holder.sistolica.setText(itemActual.sistolica.toString())
        holder.diastolica.setText(itemActual.diastolica.toString())
        holder.peso.setText(itemActual.peso.toString())
        holder.oxigenacion.setText(itemActual.oxigenacion.toString())
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

