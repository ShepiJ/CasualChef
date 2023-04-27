package com.jose_sanchis_hueso.CasualChef

import android.R
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityPreferenciasInterfazBinding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class ActivityPreferencias_Interfaz : AppCompatActivity() {
    lateinit var binding: ActivityPreferenciasInterfazBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferenciasInterfazBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ensenarColor(binding.colorFondo, binding.buttonColorFondo)
        ensenarColor(binding.colorLetra, binding.buttonColorLetra)
        ensenarColor(binding.colorBotones, binding.buttonColorBotones)
        ensenarColor(binding.colorAppBar, binding.buttonColorAppBar)


        //Para el spinner
        val spinner: Spinner = binding.spinnerDefault
        val items = listOf(" ", "Modo Claro", "Modo Oscuro")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        binding.editColorFondo.setOnClickListener {
            ponerColor(binding.colorFondo, binding.buttonColorFondo)
        }
        binding.editColorLetra.setOnClickListener {
            ponerColor(binding.colorLetra, binding.buttonColorLetra)
        }
        binding.editColorBotones.setOnClickListener {
            ponerColor(binding.colorBotones, binding.buttonColorBotones)
        }
        binding.editColorAppBar.setOnClickListener {
            ponerColor(binding.colorAppBar, binding.buttonColorAppBar)
        }

    }

    private fun ponerColor(textview: TextView, boton: Button) {
        ColorPickerDialog.Builder(this)
            .setTitle("ColorPicker Dialog")
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
                getString(R.string.cancel)
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


}