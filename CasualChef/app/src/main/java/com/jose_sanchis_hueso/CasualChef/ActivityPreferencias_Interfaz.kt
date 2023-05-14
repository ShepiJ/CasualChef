package com.jose_sanchis_hueso.CasualChef

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityPreferenciasInterfazBinding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class ActivityPreferencias_Interfaz : AppCompatActivity() {
    lateinit var binding: ActivityPreferenciasInterfazBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferenciasInterfazBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarColoresXML()

        binding.info.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Estas opciones se aplicarán solo a las recetas.")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, id ->
                }
            val alert = builder.create()
            alert.show()
        }

        binding.guardar.setOnClickListener {
            hacerUpdate()
        }
        binding.restablecer.setOnClickListener {
            restablecer()
        }

        binding.editColorLetra.setOnClickListener {
            ponerColor(binding.colorLetra, binding.buttonColorLetra)
        }
        binding.editColorBotones.setOnClickListener {
            ponerColor(binding.colorBotones, binding.buttonColorBotones)
        }
        binding.editColorEtiqueta.setOnClickListener {
            ponerColor(binding.colorEtiqueta, binding.buttonColorEtiqueta)
        }
        binding.editColorFondoReceta.setOnClickListener {
            ponerColor(binding.fondocolorReceta, binding.buttonColorFondoReceta)
        }


    }

    private fun ponerColor(textview: TextView, boton: Button) {
        ColorPickerDialog.Builder(this)
            .setTitle("Seleccione un color")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton(("Confirmar"),
                ColorEnvelopeListener { envelope, fromUser ->
                    run {

                        var hexCode = envelope.hexCode
                        var hexCodeWithoutPrefix = hexCode.substring(2)

                        textview.text = "#" + hexCodeWithoutPrefix

                        ensenarColor(textview, boton)

                    }
                })
            .setNegativeButton(
                "Cancelar"
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }

    private fun ensenarColor(textview: TextView, boton: Button) {
        val hexColorCode = textview.text.toString()
        val color = Color.parseColor(hexColorCode)
        boton.setBackgroundColor(color)
    }


    private fun cargarColoresXML() {

        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("preferencias_interfaz")
            .whereEqualTo("usuario", username)
            .get()
            .addOnSuccessListener { querySnapshot ->

                if (!querySnapshot.isEmpty) {
                    val userData = querySnapshot.documents[0].data

                    with(binding) {
                        binding.colorLetra.text = userData?.get("colorLetra").toString()
                        binding.colorEtiqueta.text = userData?.get("colorEtiqueta").toString()
                        binding.colorBotones.text = userData?.get("colorBotones").toString()
                        binding.fondocolorReceta.text = userData?.get("fondoColorReceta").toString()


                        ensenarColor(binding.colorLetra, binding.buttonColorLetra)
                        ensenarColor(binding.colorBotones, binding.buttonColorBotones)
                        ensenarColor(binding.colorEtiqueta, binding.buttonColorEtiqueta)
                        ensenarColor(binding.fondocolorReceta, binding.buttonColorFondoReceta)
                    }

                }

            }


    }

    fun hacerUpdate() {
        val sharedPrefsLogin = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefsLogin.getString("username", "")
        val pass = sharedPrefsLogin.getString("contraseña", "")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(username.toString()+"@gmail.com", pass.toString())
            .addOnSuccessListener { authResult ->

                val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
                val username = sharedPrefs.getString("username", "")

                val firestore = FirebaseFirestore.getInstance()

                val paqueteInterfaz: MutableMap<String, Any> = HashMap()

                paqueteInterfaz["colorLetra"] = binding.colorLetra.text.toString()
                paqueteInterfaz["colorEtiqueta"] = binding.colorEtiqueta.text.toString()
                paqueteInterfaz["colorBotones"] = binding.colorBotones.text.toString()
                paqueteInterfaz["fondoColorReceta"] = binding.fondocolorReceta.text.toString()
                paqueteInterfaz["usuario"] = username.toString()

                firestore.collection("preferencias_interfaz")
                    .whereEqualTo("usuario", username.toString())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documentSnapshot = querySnapshot.documents[0]
                            documentSnapshot.reference.update(paqueteInterfaz)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Se han actualizado los colores",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Ha habido un problema actualizando los colores",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
            }
    }

    fun restablecer() {

        val nightModeFlags =
            this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                    binding.colorLetra.text = "#FFFFFF"
                    binding.colorEtiqueta.text = "#4970E3"
                    binding.colorBotones.text = "#A6A8A8"
                    binding.fondocolorReceta.text = "#151515"
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.colorLetra.text = "#000000"
                binding.colorEtiqueta.text = "#0025FA"
                binding.colorBotones.text = "#787A7A"
                binding.fondocolorReceta.text = "#8799AC"
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                binding.colorLetra.text = "#000000"
                binding.colorEtiqueta.text = "#0025FA"
                binding.colorBotones.text = "#787A7A"
                binding.fondocolorReceta.text = "#8799AC"
            }
        }

        ensenarColor(binding.colorLetra, binding.buttonColorLetra)
        ensenarColor(binding.colorBotones, binding.buttonColorBotones)
        ensenarColor(binding.colorEtiqueta, binding.buttonColorEtiqueta)
        ensenarColor(binding.fondocolorReceta, binding.buttonColorFondoReceta)


    }

}


