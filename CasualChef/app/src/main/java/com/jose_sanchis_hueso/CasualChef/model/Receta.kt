package com.jose_sanchis_hueso.CasualChef.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.nio.charset.Charset


data class Receta(
    val id: String,
    val nombre: String,
    val desarrollador: String,
    val tags: String,
    val imagen: String,
    val descripcion: String,
    val ingredientes: String,
    val condiciones: List<Boolean>,
    val dificultad: Double,
    val tiempoPrep: String
) {
    companion object {

        fun getFiltro(context: Context, resultado: String = ""): List<Receta> {
            val recetaList: MutableList<Receta> = mutableListOf()

            val sharedPrefs = context.getSharedPreferences("filtro", Context.MODE_PRIVATE)
            val prefFiltro = sharedPrefs.getString("filtroClase", "")
            val prefValor = sharedPrefs.getString("valor", "")
            val dificultad = sharedPrefs.getString("dificultad", "")
            val bool1 = sharedPrefs.getString("bool1", "")
            val bool2 = sharedPrefs.getString("bool2", "")
            val bool3 = sharedPrefs.getString("bool3", "")
            val bool4 = sharedPrefs.getString("bool4", "")
            val bool5 = sharedPrefs.getString("bool5", "")


            val file = File(context.filesDir, "recetas.json")
            val jsonString = file.readText(Charset.defaultCharset())

            val listType: Type = object : TypeToken<MutableList<Receta?>?>() {}.type
            val gson = Gson()
            recetaList.addAll(gson.fromJson(jsonString, listType))

            val filteredList = when (prefFiltro) {
                "tags" -> {
                    when (resultado) {
                        prefValor -> recetaList.filter { receta ->
                            receta.tags.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                        else -> recetaList.filter { receta ->
                            !receta.tags.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                    }
                }
                "nombre" -> {
                    when (resultado) {
                        prefValor -> recetaList.filter { receta ->
                            receta.nombre.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                        else -> recetaList.filter { receta ->
                            !receta.nombre.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                    }
                }
                "autor" -> {
                    when (resultado) {
                        prefValor -> recetaList.filter { receta ->
                            receta.desarrollador.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                        else -> recetaList.filter { receta ->
                            !receta.desarrollador.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                    }
                }
                "ingredientes" -> {
                    when (resultado) {
                        prefValor -> recetaList.filter { receta ->
                            receta.ingredientes.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                        else -> recetaList.filter { receta ->
                            !receta.ingredientes.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                    }
                }
                "descripcion" -> {
                    when (resultado) {
                        prefValor -> recetaList.filter { receta ->
                            receta.descripcion.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                        else -> recetaList.filter { receta ->
                            !receta.descripcion.contains(
                                resultado,
                                ignoreCase = true
                            )
                        }
                    }
                }
                else -> throw IllegalArgumentException("Propiedad inválida")
            }

            val boolean1 = bool1.toBoolean()
            val boolean2 = bool2.toBoolean()
            val boolean3 = bool3.toBoolean()
            val boolean4 = bool4.toBoolean()
            val boolean5 = bool5.toBoolean()

            val allBooleansFalse =
                boolean1.not() && boolean2.not() && boolean3.not() && boolean4.not() && boolean5.not()

            val allBooleansTrue =
                boolean1 && boolean2 && boolean3 && boolean4 && boolean5

            return if (allBooleansTrue) {
                filteredList.filter { receta ->
                    receta.condiciones[0] && receta.condiciones[1] && receta.condiciones[2] &&
                            receta.condiciones[3] && receta.condiciones[4]
                }.filter { receta ->
                    receta.dificultad >= dificultad.toString().toDouble()
                }
            } else if (allBooleansFalse) {
                filteredList.filter { receta ->
                    receta.dificultad >= dificultad.toString().toDouble()
                }
            } else {
                filteredList.filter { receta ->
                    var containsAllSelected = true
                    if (boolean1) {
                        containsAllSelected = containsAllSelected && receta.condiciones[0]
                    }
                    if (boolean2) {
                        containsAllSelected = containsAllSelected && receta.condiciones[1]
                    }
                    if (boolean3) {
                        containsAllSelected = containsAllSelected && receta.condiciones[2]
                    }
                    if (boolean4) {
                        containsAllSelected = containsAllSelected && receta.condiciones[3]
                    }
                    if (boolean5) {
                        containsAllSelected = containsAllSelected && receta.condiciones[4]
                    }
                    containsAllSelected
                }.filter { receta ->
                    receta.dificultad >= dificultad.toString().toDouble()
                }
            }
        }



        fun getReceta(context: Context, desarrollador: String = ""): List<Receta> {
            val recetaList: MutableList<Receta> = mutableListOf()

            val sharedPrefs = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            val username = sharedPrefs.getString("username", "")

            // Read from the JSON file
            val file = File(context.filesDir, "recetas.json")
            val jsonString = file.readText(Charset.defaultCharset())

            val listType: Type = object : TypeToken<MutableList<Receta?>?>() {}.type
            val gson = Gson()
            recetaList.addAll(gson.fromJson(jsonString, listType))

            return when (desarrollador) {
                username -> recetaList.filter { articulo -> articulo.desarrollador == desarrollador }
                else -> recetaList.filter { articulo -> articulo.desarrollador != username }
            }
        }


        fun getReceta(context: Context): List<Receta> {
            val recetaList: MutableList<Receta> = mutableListOf()

            // Read from the JSON file
            val file = File(context.filesDir, "recetas.json")
            val jsonString = file.readText(Charset.defaultCharset())

            val listType: Type = object : TypeToken<MutableList<Receta?>?>() {}.type
            val gson = Gson()
            recetaList.addAll(gson.fromJson(jsonString, listType))

            return recetaList
        }

        fun getRecetaId(id: String?, context: Context): Receta {
            val articulo = getReceta(context).filter { articulo ->
                articulo.id.equals(id)
            }
            return articulo[0]
        }

    }
}
