package com.alexac.mistensiones.funciones_varias

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.provider.Settings.Global.getString
import android.util.Log
import com.alexac.mistensiones.models.DocumentoDatos
import com.alexac.mistensiones.R
import com.alexac.mistensiones.models.Alimentos
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.sql.Timestamp

class FuncionesVarias {

    private val database = FirebaseFirestore.getInstance()

    //PARSEA TODOS LAS COLECCIONES CLAVE:VALOR DE FIREBASE A OBJETOS DE LA CLASE DOCUMENTODATOS Y DEVUELVE UN ARRAY CON OBJETOS DE LOS RESULTADOS
    fun parsearDatos(documents: QuerySnapshot): ArrayList<DocumentoDatos>{
        val listaDocumentoDatos = arrayListOf<DocumentoDatos>()
        var posicion = 0
        for (document in documents) {
            var DocumentoDatos = DocumentoDatos()
            Log.d("Registro", "${document.id} => ${document.data}")
            DocumentoDatos.fecha = document["fecha"] as String
            DocumentoDatos.hora = document["hora"] as String
            DocumentoDatos.sistolica = document["sistolica"] as Double
            DocumentoDatos.diastolica = document["diastolica"] as Double
            DocumentoDatos.peso = document["peso"] as Double
            DocumentoDatos.oxigenacion = document["oxigenacion"] as Long
            DocumentoDatos.glucosa = document["glucemia"] as Double
            DocumentoDatos.observaciones = document["observaciones"] as String
            DocumentoDatos.posicion = posicion
            DocumentoDatos.timestamp = document["timestamp"] as Long
            listaDocumentoDatos.add(DocumentoDatos)
        }
        var listaDocumentoDatosOrdenada = ordenarMayorAMenor(listaDocumentoDatos)
        for (documento in listaDocumentoDatosOrdenada) {
            documento.posicion = posicion
            posicion += 1
        }
        return listaDocumentoDatosOrdenada
    }



    //ORDENA LOS DOCUMENTOS OBTENIDOS DE MAYOR A MENOR TIMESTAMP, ES DECIR, PRIMERO LOS MAS RECIENTES
    fun ordenarMayorAMenor(listaDocumentoDatos: ArrayList<DocumentoDatos>): ArrayList<DocumentoDatos> {
        var temp: DocumentoDatos
        for (x in 0 until listaDocumentoDatos.size){
            for(y in 0 until listaDocumentoDatos.size){
                if(listaDocumentoDatos[x].timestamp > listaDocumentoDatos[y].timestamp){
                    temp = listaDocumentoDatos[x]
                    listaDocumentoDatos[x] = listaDocumentoDatos[y]
                    listaDocumentoDatos[y] = temp
                }
            }
        }

        return listaDocumentoDatos
    }

    fun crearTimestamp(dia: Int, mes: Int, año: Int): Long{
        var timestamp = Timestamp(año, mes, dia, 0, 0, 0, 0)
        var milisegundos: Long = timestamp.toInstant().toEpochMilli()
        return milisegundos
    }

    fun crearTimestampCompleto(dia: Int, mes: Int, año: Int, hora: Int, minutos: Int, segundos: Int, nanosegundos: Int): Long{
        var timestamp = Timestamp(año, mes, dia, hora, minutos, segundos, 0)
        var milisegundos: Long = timestamp.toInstant().toEpochMilli()
        return milisegundos
    }

    fun mostrarDialogoWarning(context: Context, textoAlimentos: String){
        val builder = AlertDialog.Builder(context)

        builder.setTitle(R.string.precaucion)
        builder.setMessage(R.string.mensaje_warning)
        builder.setPositiveButton(R.string.ok, {dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel()})
        builder.show()
    }

    fun extraerAlimentos(context: Context): ArrayList<Alimentos>{
        var listaAlimentos: ArrayList<Alimentos> = arrayListOf<Alimentos>()
        val coleccionAlimentos = database.collection("comidaSana")
        coleccionAlimentos.get().addOnSuccessListener { alimentos ->
            for (alimento in alimentos) {
                Log.d("Registro", "${alimento.id} => ${alimento.data}")
            }
            listaAlimentos = parsearAlimentos(alimentos)

            mostrarDialogoWarning(context, crearAlimentosWarning(listaAlimentos))
        }

        return listaAlimentos
    }

    fun parsearAlimentos(alimentos: QuerySnapshot): ArrayList<Alimentos> {
        val listaAlimentos = arrayListOf<Alimentos>()
        for (alimento in alimentos) {
            var alimentoNuevo = Alimentos()
            alimentoNuevo.nombre = alimento["nombre"] as String
            alimentoNuevo.propiedades = alimento["Propiedades"] as String
            listaAlimentos.add(alimentoNuevo)
        }
        return listaAlimentos
    }

    fun crearAlimentosWarning(listaAlimentos: ArrayList<Alimentos>): String{
        var textoAlimentosWarning = ""
        for (alimento in listaAlimentos){
            textoAlimentosWarning =textoAlimentosWarning + alimento.nombre + ": " + alimento.propiedades + "\n"
        }
        return textoAlimentosWarning
    }

}