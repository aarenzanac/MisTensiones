package com.alexac.mistensiones

class DocumentoDatos {
    lateinit var listadoDocumentos: ArrayList<DocumentoDatos>
    var fecha: String = ""
    var hora: String = ""
    var sistolica: Double = 0.0
    var diastolica: Double = 0.0
    var peso: Double = 0.0
    var oxigenacion: Long = 0
    var glucosa: Double = 0.0
    var observaciones: String = ""
    var semaforo: Int = 0x00FF00
    var timestamp: Long = 0
    var posicion: Int = 0

    class DocumentoDatos(fecha: String, hora: String, sistolica: Double, diastolica: Double, peso: Double, oxigenacion: Long, glucosa: Double, observaciones: String, semaforo: Int, timestamp: Long, posicion: Int)
}