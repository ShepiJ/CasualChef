package com.jose_sanchis_hueso.CasualChef

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityDetalleBinding
import com.jose_sanchis_hueso.CasualChef.model.Articulo
import ponerImagen

class DetalleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(
            ActivityDetalleBinding.inflate(layoutInflater).also { binding = it }.root
        )
        cargarVideoJuego()

    }

    private fun cargarVideoJuego() {
        var id = intent.getStringExtra("ID")

        var articulo = Articulo.getVideoJuegoPorId(id.toString(),this)

        val imageView = binding.imageView
        val tvNombre = binding.tvNombre
        val tvDeveloper = binding.tvDeveloper
        val tvTag1 = binding.tvTag1
        val tvDescripcion = binding.tvDescripcion

        imageView.setImageResource(articulo.imagen.ponerImagen(imageView.context))
        tvNombre.text = articulo.nombre
        tvDeveloper.text = articulo.desarrollador
        tvTag1.text = articulo.tags
        tvDescripcion.text = articulo.descripcion
    }

}