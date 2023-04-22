package com.jose_sanchis_hueso.CasualChef.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.nio.charset.Charset

data class Datos_Usuario(
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val imagen: String,
    val usuario: String
) {

    fun getD_Usu(context: Context, desarrollador: String = ""): List<Articulo> {
        val articuloList: MutableList<Articulo> = mutableListOf()

        val sharedPrefs = context.getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

        // Read from the JSON file
        val file = File(context.filesDir, "datosusuario.json")
        val jsonString = file.readText(Charset.defaultCharset())

        val listType: Type = object : TypeToken<MutableList<Articulo?>?>() {}.type
        val gson = Gson()
        articuloList.addAll(gson.fromJson(jsonString, listType))

        return when (desarrollador) {
            username -> articuloList.filter { articulo -> articulo.desarrollador == desarrollador }
            else -> articuloList.filter { articulo -> articulo.desarrollador != username }
        }
    }

}