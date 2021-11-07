package com.alexac.mistensiones.funciones_varias

import android.content.Context

class PreferenciasCompartidas(val context: Context) {

    val PREFERENCIAS_COMPARTIDAS = "prefs"
    val LOGIN = "login"
    val AVISO_TENSION = "avisoTension"
    val AVISO_IMC = "avisoIMC"
    val ALTURA = "altura"
    val SUSCRITO = "suscrito"
    val EMAIL = "email"
    val PWD = "pwd"

    val almacenamiento = context.getSharedPreferences(PREFERENCIAS_COMPARTIDAS, 0)
    val login = context.getSharedPreferences(LOGIN, 0)


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

    fun guardarPreferenciaSuscripcionPush(preferencia: Boolean){
        almacenamiento.edit().putBoolean(SUSCRITO, preferencia).apply()
    }

    fun recuperarPrefenenciaSuscripcionPush(): Boolean {
        return almacenamiento.getBoolean(SUSCRITO, false)
    }

    fun guardarPreferenciaEmail(preferencia: String){
        login.edit().putString(EMAIL, preferencia).apply()
    }

    fun recuperarPrefenenciaEmail(): String? {
        return login.getString(EMAIL, "")
    }

    fun guardarPreferenciaPwd(preferencia: String){
        login.edit().putString(PWD, preferencia).apply()
    }

    fun recuperarPrefenenciaPwd(): String? {
        return login.getString(PWD, "")
    }

    fun borrarPreferenciasLogin(){
        login.edit().clear().apply()
    }

}