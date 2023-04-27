package com.jose_sanchis_hueso.CasualChef

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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

        binding.tvDeveloper.setOnClickListener{

            var usuarioPreferencia =
                this?.getSharedPreferences(
                    "usuario",
                    MODE_PRIVATE
                )
            usuarioPreferencia?.edit()
                ?.putString("username", binding.tvDeveloper.text.toString())
                ?.apply()

            val intent = Intent(this, ActivityDatos_UsuarioLite::class.java)
            startActivity(intent)
        }

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

        val ratingBar = binding.ratingBar
        ratingBar.rating = articulo.dificultad.toFloat()

        when (ratingBar.rating) {
            in 0.0..1.0 -> ratingBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
            in 1.0..2.0 -> ratingBar.progressTintList = ColorStateList.valueOf(Color.YELLOW)
            in 2.0..3.0 -> ratingBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#8B4513"))
            in 3.0..4.0 -> ratingBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FFC0CB"))
            in 4.0..5.0 -> ratingBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#8B0000"))
        }

        binding.tiempoPrep.text = articulo.tiempoPrep

        binding.bool1.isChecked = articulo.condiciones[0]
        binding.bool2.isChecked = articulo.condiciones[1]
        binding.bool3.isChecked = articulo.condiciones[2]
        binding.bool4.isChecked = articulo.condiciones[3]
        binding.bool5.isChecked = articulo.condiciones[4]

        tvNombre.text = articulo.nombre
        tvDeveloper.text = articulo.desarrollador
        tvTag1.text = articulo.tags
        tvIngredients.text = articulo.ingredientes
        tvDescripcion.text = articulo.descripcion
    }
}

