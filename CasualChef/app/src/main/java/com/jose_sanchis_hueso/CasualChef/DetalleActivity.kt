package com.jose_sanchis_hueso.CasualChef

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityDetalleBinding
import com.jose_sanchis_hueso.CasualChef.model.Receta
import ponerImagen

class DetalleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(
            ActivityDetalleBinding.inflate(layoutInflater).also { binding = it }.root
        )
        cargarVideoJuego()

        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

        if (!username.equals("Anónimo")) {
            cargarColoresXML()
        }

        binding.buttonEditar.setOnClickListener {
            val intent = Intent(this, ActivityEditarReceta::class.java)
            startActivity(intent)
        }

        binding.buttonBorrar.setOnClickListener {
            val sharedPrefs = getSharedPreferences("idGuardadaReceta", Context.MODE_PRIVATE)
            val id_aBorrar = sharedPrefs.getString("ID", "")

            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("recetas")
                .whereEqualTo("id", id_aBorrar)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        document.reference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Se ha borrado la receta sin problemas",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val handler = Handler()
                                handler.postDelayed({
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }, 2000)

                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Ha habido un error al borrar la receta",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
        }

        binding.tvDeveloper.setOnClickListener {

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
        try {
            val id = intent.getStringExtra("ID")
            val receta = Receta.getRecetaId(id.toString(), this)

            val imageView = binding.imageView
            val tvNombre = binding.tvNombre
            val tvDeveloper = binding.tvDeveloper
            val tvTag1 = binding.tvTag1
            val tvIngredients = binding.tvIngredientes
            val tvDescripcion = binding.tvDescripcion

            val storageRef =
                FirebaseStorage.getInstance().reference.child("images/${receta.imagen}")
            storageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val imageUrl = task.result.toString()
                    receta.imagen.ponerImagen(this, imageUrl, imageView)
                } else {
                    imageView.setImageResource(R.drawable.casualchef)
                }
            }

            val ratingBar = binding.ratingBar
            ratingBar.rating = receta.dificultad.toFloat()

            when (ratingBar.rating) {
                in 0.0..1.0 -> ratingBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
                in 1.0..2.0 -> ratingBar.progressTintList = ColorStateList.valueOf(Color.YELLOW)
                in 2.0..3.0 -> ratingBar.progressTintList =
                    ColorStateList.valueOf(Color.parseColor("#8B4513"))
                in 3.0..4.0 -> ratingBar.progressTintList =
                    ColorStateList.valueOf(Color.parseColor("#FFC0CB"))
                in 4.0..5.0 -> ratingBar.progressTintList =
                    ColorStateList.valueOf(Color.parseColor("#8B0000"))
            }

            binding.tiempoPrep.text = receta.tiempoPrep

            binding.bool1.isChecked = receta.condiciones[0]
            binding.bool2.isChecked = receta.condiciones[1]
            binding.bool3.isChecked = receta.condiciones[2]
            binding.bool4.isChecked = receta.condiciones[3]
            binding.bool5.isChecked = receta.condiciones[4]

            tvNombre.text = receta.nombre
            tvDeveloper.text = receta.desarrollador
            tvTag1.text = receta.tags
            tvIngredients.text = receta.ingredientes
            tvDescripcion.text = receta.descripcion

            val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
            val username = sharedPrefs.getString("username", "")

            if (binding.tvDeveloper.text.toString().equals(username)) {
                binding.buttonEditar.isEnabled = true
                binding.buttonEditar.isVisible = true
                binding.buttonBorrar.isEnabled = true
                binding.buttonBorrar.isVisible = true
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Ha habido un error al cargar la receta",
                Toast.LENGTH_SHORT
            ).show()
        }
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

                    val colorLetra = userData?.get("colorLetra").toString()
                    val colorEtiqueta = userData?.get("colorEtiqueta").toString()
                    val colorBotones = userData?.get("colorBotones").toString()
                    val fondoColorReceta = userData?.get("fondoColorReceta").toString()
                    with(binding) {
                        var hexColorCode = colorLetra
                        var color = Color.parseColor(hexColorCode)
                        tvTag1.setTextColor(color)
                        tvNombre.setTextColor(color)
                        tvIngredientes.setTextColor(color)
                        tvDescripcion.setTextColor(color)
                        tiempoPrep.setTextColor(color)
                        buttonBorrar.setTextColor(color)
                        buttonEditar.setTextColor(color)
                        bool1.setTextColor(color)
                        bool2.setTextColor(color)
                        bool3.setTextColor(color)
                        bool4.setTextColor(color)
                        bool5.setTextColor(color)
                        hexColorCode = colorEtiqueta
                        color = Color.parseColor(hexColorCode)
                        textView20.setTextColor(color)
                        textView21.setTextColor(color)
                        textView16.setTextColor(color)
                        textView18.setTextColor(color)
                        textView17.setTextColor(color)
                        textView13.setTextColor(color)
                        textView7.setTextColor(color)
                        hexColorCode = colorBotones
                        color = Color.parseColor(hexColorCode)
                        buttonBorrar.setBackgroundColor(color)
                        buttonEditar.setBackgroundColor(color)
                        hexColorCode = fondoColorReceta
                        color = Color.parseColor(hexColorCode)
                        constraint.setBackgroundColor(color)
                    }

                }
            }

    }
}

