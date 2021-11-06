package com.alexac.mistensiones.funciones_varias

import android.app.Application

class CargarPreferenciasCompartidas: Application() {

    companion object{
        lateinit var preferenciasCompartidas: PreferenciasCompartidas
    }

    override fun onCreate() {
        super.onCreate()
        preferenciasCompartidas = PreferenciasCompartidas(applicationContext)
    }
}