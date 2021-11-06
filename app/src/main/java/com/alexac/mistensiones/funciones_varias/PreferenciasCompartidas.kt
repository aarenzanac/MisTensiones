package com.alexac.mistensiones.funciones_varias

import android.content.Context

class PreferenciasCompartidas(val context: Context) {

    val PREFERENCIAS_COMPARTIDAS = "prefs"
    val AVISO_TENSION = "avisoTension"
    val AVISO_IMC = "avisoIMC"
    val ALTURA = "altura"

    val almacenamiento = context.getSharedPreferences(PREFERENCIAS_COMPARTIDAS, 0)


    fun guardarPreferenciaTension(preferencia: Boolean){
        almacenamiento.edit().putBoolean(AVISO_TENSION, preferencia).apply()
    }

    fun guardarPreferenciaIMC(preferencia: Boolean){
        almacenamiento.edit().putBoolean(AVISO_IMC, preferencia).apply()
    }

    fun recuperarPrefenenciaTension(): Boolean{
        return almacenamiento.getBoolean(AVISO_TENSION, false)

    }

    fun recuperarPrefenenciaIMC(): Boolean{
        return almacenamiento.getBoolean(AVISO_IMC, false)

    }

    fun guardarPreferenciaAltura(preferencia: Int){
        almacenamiento.edit().putInt(ALTURA, preferencia).apply()
    }

    fun recuperarPrefenenciaAltura(): Int {
        return almacenamiento.getInt(ALTURA, 120)

    }
}