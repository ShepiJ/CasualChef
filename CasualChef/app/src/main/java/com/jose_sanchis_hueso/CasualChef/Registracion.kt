package com.jose_sanchis_hueso.CasualChef

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityRegistracionBinding

class Registracion : AppCompatActivity() {

    private lateinit var binding: ActivityRegistracionBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistracionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        //Coge los datos de la base de datos de forma anonima
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { authResult ->
                guardarColeccionJson(this,"recetas","recetas.json")
            }

//El usuario no puede poner un nombre con espacios.
        binding.button2.setOnClickListener {
            val email = binding.cogerUsuario.text.toString()+"@gmail.com"
            val password = binding.cogerContraseA.text.toString()

            if (password.isBlank()) {
                Toast.makeText(this, "El campo Contraseña no puede estar vacio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isBlank()) {
                Toast.makeText(this, "El campo Usuario no puede estar vacio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.contains(" ")) {
                Toast.makeText(this, "El correo electrónico no puede contener espacios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                        mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this) { task ->
                                            if (task.isSuccessful) {
                                                val intent = Intent(this, MainActivity::class.java)
                                                startActivity(intent)
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "Ha habido un problema conectando al servidor",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    //
                                    Toast.makeText(
                                        this,
                                        "La cuenta que está intentando crear ya existe.\nPruebe otro usuario",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
        }
    }

