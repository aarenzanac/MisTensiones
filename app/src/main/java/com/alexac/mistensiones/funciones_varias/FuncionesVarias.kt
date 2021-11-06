package com.alexac.mistensiones.funciones_varias

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.alexac.mistensiones.R
import com.alexac.mistensiones.models.Alimentos
import com.alexac.mistensiones.models.DocumentoDatos
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.sql.Timestamp

class FuncionesVarias {

    var avisoTension: Boolean = true
    var avisoIMC: Boolean= false

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


    //FUNCION QUE CREA EL TIMESTAMP EN FUNCION DE LAS VARIABLES SETEADAS POR LOS SELECTORES DE FECHA. UTILIZADO POR EL FILTRO DE FECHAS. UNICAMENTE DE FECHAS
    fun crearTimestamp(dia: Int, mes: Int, año: Int): Long{
        var timestamp = Timestamp(año, mes, dia, 0, 0, 0, 0)
        var milisegundos: Long = timestamp.toInstant().toEpochMilli()
        return milisegundos
    }


    //FUNCION QUE CREA EL TIMESTAMP EN FUNCION DE LAS VARIABLES SETEADAS POR LOS SELECTORES DE FECHA Y HORA. UTILIZADO PARA ORDENAR LOS DOCUMENTOS DE DATOS TAMBIEN SI HAY DOS O MAS ENTRADAS EL MISMO DÍA
    fun crearTimestampCompleto(dia: Int, mes: Int, año: Int, hora: Int, minutos: Int, segundos: Int, nanosegundos: Int): Long{
        var timestamp = Timestamp(año, mes, dia, hora, minutos, segundos, 0)
        var milisegundos: Long = timestamp.toInstant().toEpochMilli()
        return milisegundos
    }


    //MUESTRA EL DIALOG WARNING
    fun mostrarDialogoWarning(context: Context, textoAlimentos: String){
        val builder = AlertDialog.Builder(context)

        builder.setTitle(context.getString(R.string.precaucion_medico))
        builder.setMessage(context.getString(R.string.mensaje_warning) + textoAlimentos)
        builder.setPositiveButton(R.string.ok, {dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel()})
        builder.show()
    }


    //FUNCION QUE EXTRAE DE LA BASE DE DATOS LOS ALIMENTOS Y LOS TRANSFORMA EN UN ARRAY DE OBJETOS ALIMENTOS
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


    //FUNCION QUE PARSEA LA CONSULTA A LA BASE DE DATOS DE ALIMENTOS Y LO PARSEA A OBJETOS DE LA CLASE ALIMENTO. SELECCIONANDO UNICAMENTE 2
    fun parsearAlimentos(alimentos: QuerySnapshot): ArrayList<Alimentos> {
        val listaAlimentos = arrayListOf<Alimentos>()
        val listaDosAlimentosSeleccionados = arrayListOf<Alimentos>()
        var randomIndex1 = 0
        var randomIndex2 = 0
        for (alimento in alimentos) {
            var alimentoNuevo = Alimentos()
            alimentoNuevo.nombre = alimento["nombre"] as String
            alimentoNuevo.propiedades = alimento["Propiedades"] as String
            listaAlimentos.add(alimentoNuevo)
        }
        do{
            randomIndex1 = (0..listaAlimentos.size-1).random()
            randomIndex2 = (0..listaAlimentos.size-1).random()
        }while (randomIndex1 == randomIndex2)

        listaDosAlimentosSeleccionados.add(listaAlimentos[randomIndex1])
        listaDosAlimentosSeleccionados.add(listaAlimentos[randomIndex2])

        return listaDosAlimentosSeleccionados
    }


    //FUNCION QUE CREA UN STRING CON LOS ALIMENTOS PARA MOSTRAR EN EL ALERTDIALOG
    fun crearAlimentosWarning(listaDosAlimentosSeleccionados: ArrayList<Alimentos>): String{
        var textoAlimentosWarning = ""
        for (alimento in listaDosAlimentosSeleccionados){
            textoAlimentosWarning =textoAlimentosWarning + alimento.nombre + ": " + alimento.propiedades + "\n"
        }
        return textoAlimentosWarning
    }
}