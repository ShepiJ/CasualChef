package com.jose_sanchis_hueso.CasualChef.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jose_sanchis_hueso.CasualChef.R
import java.io.File
import java.lang.reflect.Type
import java.nio.charset.Charset

data class Articulo(
    val id: String,
    val nombre: String,
    val desarrollador: String,
    val tags: String,
    val puntuacion: Float,
    val imagen: String,
    val descripcion: String
) {
    companion object {

        fun getArticulo(context: Context, desarrollador: String = ""): List<Articulo> {
            val articuloList: MutableList<Articulo> = mutableListOf()

            // Read from the JSON file
            val file = File(context.filesDir, "recetas.json")
            val jsonString = file.readText(Charset.defaultCharset())

            val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
            val gson = Gson()
            articuloList.addAll(gson.fromJson(jsonString, listType))

            return when (desarrollador) {
                "kneegrows" -> articuloList.filter { articulo -> articulo.desarrollador == desarrollador }
                else -> articuloList.filter { articulo -> articulo.desarrollador != "kneegrows" }
            }
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

        fun getVideoJuegoPorId(id: String?, context: Context): Articulo {
            val articulo = getArticulo(context).filter { articulo ->
                articulo.id.equals(id)
            }
            return articulo[0]
        }
    }
}
