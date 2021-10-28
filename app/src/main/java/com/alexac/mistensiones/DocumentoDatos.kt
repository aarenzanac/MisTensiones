package com.alexac.mistensiones

import java.io.StringBufferInputStream

class DocumentoDatos {
    lateinit var listadoDocumentos: ArrayList<DocumentoDatos>
    var fecha: String = ""
    var hora: String = ""
    var sistolica: Double = 0.0
    var diastolica: Double = 0.0
    var peso: Double = 0.0
    var oxigenacion: Long = 0

    class DocumentoDatos(fecha: String, hora: String, sistolica: Double, diastolica: Double, peso: Double, oxigenacion: Long)
}