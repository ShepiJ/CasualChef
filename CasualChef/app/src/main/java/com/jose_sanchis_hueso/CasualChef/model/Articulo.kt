package com.jose_sanchis_hueso.CasualChef.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jose_sanchis_hueso.CasualChef.R
import java.io.File
import java.lang.reflect.Type
import java.nio.charset.Charset

data class Articulo(
    val id: Int,
    val nombre: String,
    val desarrollador: String,
    val tags: String,
    val puntuacion: Float,
    val imagen: String,
    val descripcion: String,
    val tipo: String
)  {
    companion object {

        fun getArticulo(context: Context, tipo: String): List<Articulo> {
            val articuloList: MutableList<Articulo> = mutableListOf()

            // Read from the JSON file
            val file = File(context.filesDir, "recetas.json")
            val jsonString = file.readText(Charset.defaultCharset())

            val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
            val gson = Gson()
            articuloList.addAll(gson.fromJson(jsonString, listType))

            val listaFiltrada = articuloList.filter { articulo -> articulo.tipo == tipo }

            return listaFiltrada
        }

        fun getArticulo(context: Context): List<Articulo> {
            val articuloList: MutableList<Articulo> = mutableListOf()

            // Read from the JSON file
            val file = File(context.filesDir, "recetas.json")
            val jsonString = file.readText(Charset.defaultCharset())

            val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
            val gson = Gson()
            articuloList.addAll(gson.fromJson(jsonString, listType))

            return articuloList
        }

        fun getVideoJuegoPorId(id: Int?, context: Context): Articulo {
            val articulo = getArticulo(context).filter { articulo ->
                articulo.id == id
            }
            return articulo[0]
        }
    }
}
