package com.alexac.mistensiones.funciones_varias

import android.util.Log
import com.alexac.mistensiones.DocumentoDatos
import com.google.firebase.firestore.QuerySnapshot
import java.sql.Timestamp

class FuncionesVarias {

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


}