package com.alexac.mistensiones.models

class DocumentoDatos {
    var fecha: String = ""
    var hora: String = ""
    var sistolica: Double = 0.0
    var diastolica: Double = 0.0
    var peso: Double = 0.0
    var oxigenacion: Long = 0
    var glucosa: Double = 0.0
    var observaciones: String = ""
    var timestamp: Long = 0
    var posicion: Int = 0

    class DocumentoDatos(fecha: String, hora: String, sistolica: Double, diastolica: Double, peso: Double, oxigenacion: Long, glucosa: Double, observaciones: String, timestamp: Long, posicion: Int)
}