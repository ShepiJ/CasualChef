package com.jose_sanchis_hueso.CasualChef

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.storage.FirebaseStorage
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
        val id = intent.getStringExtra("ID")
        val articulo = Articulo.getVideoJuegoPorId(id.toString(), this)

        val imageView = binding.imageView
        val tvNombre = binding.tvNombre
        val tvDeveloper = binding.tvDeveloper
        val tvTag1 = binding.tvTag1
        val tvIngredients = binding.tvIngredientes
        val tvDescripcion = binding.tvDescripcion

        //val imageUrl = "gs://casualchef.appspot.com/images/${articulo.imagen}"
        //articulo.imagen.ponerImagen(this, imageUrl, imageView)

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${articulo.imagen}")
        storageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val imageUrl = task.result.toString()
                articulo.imagen.ponerImagen(this, imageUrl, imageView)
            } else {
                imageView.setImageResource(R.drawable.casualchef)
            }
        }

        tvNombre.text = articulo.nombre
        tvDeveloper.text = articulo.desarrollador
        tvTag1.text = articulo.tags
        tvIngredients.text = articulo.ingredientes
        tvDescripcion.text = articulo.descripcion
    }
}

