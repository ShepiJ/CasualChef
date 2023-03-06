package com.jose_sanchis_hueso.CasualChef.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jose_sanchis_hueso.CasualChef.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type

data class Articulo(
    val id: Int,
    val nombre: String,
    val desarrollador: String,
    val tags: Array<String>,
    val puntuacion: Float,
    val imagen: String,
    val descripcion: String,
    val tipo: String
)  { companion object {

    fun getArticulo(context: Context, tipo: String): List<Articulo> {
        var articuloList: MutableList<Articulo> = mutableListOf()
        val raw = context.resources.openRawResource(R.raw.datos_articulos)
        val rd = BufferedReader(InputStreamReader(raw))
        val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
        val gson = Gson()
        articuloList.addAll(gson.fromJson(rd, listType))

        val listaFiltrada = articuloList.filter { articulo -> articulo.tipo == tipo }

        return listaFiltrada
    }

    fun getArticulo(context: Context): List<Articulo> {
        var articuloList: MutableList<Articulo> = mutableListOf()
        val raw = context.resources.openRawResource(R.raw.datos_articulos)
        val rd = BufferedReader(InputStreamReader(raw))
        val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
        val gson = Gson()
        articuloList.addAll(gson.fromJson(rd, listType))

        return articuloList
    }

    fun getVideoJuegoPorId(id: Int?,context: Context): Articulo {
        val articulo = getArticulo(context).filter { articulo ->
            articulo.id == id
        }
        return articulo[0]
    }
}
}
