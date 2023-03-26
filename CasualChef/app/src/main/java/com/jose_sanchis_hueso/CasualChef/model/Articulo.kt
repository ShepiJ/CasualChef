package com.jose_sanchis_hueso.CasualChef.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

        fun getFiltro(context: Context, resultado: String = ""): List<Articulo> {
            val articuloList: MutableList<Articulo> = mutableListOf()

            val sharedPrefs = context.getSharedPreferences("filtro", Context.MODE_PRIVATE)
            val prefFiltro = sharedPrefs.getString("filtroClase", "")
            val prefValor = sharedPrefs.getString("valor", "")

            // Read from the JSON file
            val file = File(context.filesDir, "recetas.json")
            val jsonString = file.readText(Charset.defaultCharset())

            val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
            val gson = Gson()
            articuloList.addAll(gson.fromJson(jsonString, listType))

            return when (prefFiltro) {
                "tags" -> {
                    when (resultado) {
                        prefValor -> articuloList.filter { articulo -> articulo.tags.contains(resultado, ignoreCase = true)}
                        else -> articuloList.filter { articulo -> articulo.tags.contains(resultado, ignoreCase = true) }
                    }
                }
                "nombre" -> {
                    when (resultado) {
                        prefValor -> articuloList.filter { articulo -> articulo.nombre.contains(resultado, ignoreCase = true) }
                        else -> articuloList.filter { articulo -> articulo.nombre.contains(resultado, ignoreCase = true)}
                    }
                }
                else -> throw IllegalArgumentException("Propiedad inválida")
            }
        }

        fun getArticulo(context: Context, desarrollador: String = ""): List<Articulo> {
            val articuloList: MutableList<Articulo> = mutableListOf()

            val sharedPrefs = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            val username = sharedPrefs.getString("username", "")

            // Read from the JSON file
            val file = File(context.filesDir, "recetas.json")
            val jsonString = file.readText(Charset.defaultCharset())

            val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
            val gson = Gson()
            articuloList.addAll(gson.fromJson(jsonString, listType))

            return when (desarrollador) {
                username -> articuloList.filter { articulo -> articulo.desarrollador == desarrollador }
                else -> articuloList.filter { articulo -> articulo.desarrollador != username }
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
