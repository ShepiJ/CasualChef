package com.jose_sanchis_hueso.CasualChef.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jose_sanchis_hueso.CasualChef.R
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
)  {
    companion object {

        fun getArticulo(context: Context, tipo: String): List<Articulo> {
            var articuloList: MutableList<Articulo> = mutableListOf()

            // Read from the JSON file
            val jsonString = context.resources.openRawResource(R.raw.datos_articulos).bufferedReader().use { it.readText() }

            val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
            val gson = Gson()
            articuloList.addAll(gson.fromJson(jsonString, listType))

            val listaFiltrada = articuloList.filter { articulo -> articulo.tipo == tipo }

            return listaFiltrada
        }

        fun getArticulo(context: Context): List<Articulo> {
            var articuloList: MutableList<Articulo> = mutableListOf()

            // Read from the JSON file
            val jsonString = context.resources.openRawResource(R.raw.datos_articulos).bufferedReader().use { it.readText() }

            val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
            val gson = Gson()
            articuloList.addAll(gson.fromJson(jsonString, listType))

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
