package com.jose_sanchis_hueso.CasualChef

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityLoginBinding
import java.io.OutputStream

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth


    override fun onBackPressed() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val screenSplash = installSplashScreen()
        screenSplash.setKeepOnScreenCondition { false }

        //para filtros
        var filtroPreferencia =
            this?.getSharedPreferences(
                "filtro",
                MODE_PRIVATE
            )
        filtroPreferencia?.edit()
            ?.putString("filtroClase", "autor")
            ?.putString("valor", "")
            ?.putString("dificultad", "0.0")
            ?.putString("bool1", "false")
            ?.putString("bool2", "false")
            ?.putString("bool3", "false")
            ?.putString("bool4", "false")
            ?.putString("bool5", "false")
            ?.apply()

        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
        val isChecked = sharedPref.getBoolean("checkbox_checked", false)

        if (isChecked) {
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }, 0)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        //Coge los datos de la base de datos de forma anonima
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { authResult ->
                guardarColeccionJson(this, "recetas", "recetas.json")
            }

        //El usuario no puede poner un nombre con espacios.
        binding.pasarMain.setOnClickListener {

            binding.pasarMain.isEnabled = false
            binding.button2.isEnabled = false

            val email = binding.cogerUsuario.text.toString() + "@gmail.com"
            val password = binding.cogerContraseA.text.toString()

            if (password.isBlank()) {
                binding.pasarMain.isEnabled = true
                binding.button2.isEnabled = true
                Toast.makeText(this, "El campo Contraseña no puede estar vacio", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (email.isBlank()) {
                binding.pasarMain.isEnabled = true
                binding.button2.isEnabled = true
                Toast.makeText(this, "El campo Usuario no puede estar vacio", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (binding.cogerUsuario.text.toString().contains(" ")) {
                binding.pasarMain.isEnabled = true
                binding.button2.isEnabled = true
                Toast.makeText(
                    this,
                    "El correo electrónico no puede contener espacios",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //Paso 1. Se intenta hacer el login normal
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        var usuarioPreferencia =
                            this?.getSharedPreferences(
                                "login",
                                MODE_PRIVATE
                            )


                        usuarioPreferencia?.edit()
                            ?.putString("username", binding.cogerUsuario.text.toString())
                            ?.putString("contraseña", binding.cogerContraseA.text.toString())
                            ?.putBoolean("checkbox_checked", binding.checkBox.isChecked)
                            ?.apply()

                        //Generando los datos usuario

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener { authResult ->
                                val user = authResult.user
                                val sharedPrefs = this.getSharedPreferences(
                                    "login",
                                    Context.MODE_PRIVATE
                                )
                                val username = sharedPrefs.getString("username", "")
                                val firestore = FirebaseFirestore.getInstance()

                                firestore.collection("datos_usuario")
                                    .whereEqualTo("usuario", username.toString())
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        if (querySnapshot.isEmpty) {
                                            val datos_usuario_Nuevo: MutableMap<String, Any> = HashMap()
                                            datos_usuario_Nuevo["usuario"] = username.toString()
                                            datos_usuario_Nuevo["nombre"] = "default"
                                            datos_usuario_Nuevo["apellido"] = "default"
                                            datos_usuario_Nuevo["email"] = "default"
                                            datos_usuario_Nuevo["telefono"] = "default"
                                            datos_usuario_Nuevo["imagen"] = "default"
                                            datos_usuario_Nuevo["descripcion"] = "default"

                                            firestore.collection("datos_usuario")
                                                .add(datos_usuario_Nuevo)
                                        }
                                    }
                            }

                        //Generando los datos de interfaz del usuario

                        val nightModeFlags =
                            this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                        when (nightModeFlags) {
                            Configuration.UI_MODE_NIGHT_YES -> {
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener { authResult ->
                                        val user = authResult.user
                                        val sharedPrefs = this.getSharedPreferences(
                                            "login",
                                            Context.MODE_PRIVATE
                                        )
                                        val username = sharedPrefs.getString("username", "")
                                        val firestore = FirebaseFirestore.getInstance()

                                        firestore.collection("preferencias_interfaz")
                                            .whereEqualTo("usuario", username.toString())
                                            .get()
                                            .addOnSuccessListener { querySnapshot ->
                                                if (querySnapshot.isEmpty) {
                                                    val datos_interfaz_usuario: MutableMap<String, Any> =
                                                        HashMap()
                                                    datos_interfaz_usuario["usuario"] = username.toString()
                                                    datos_interfaz_usuario["colorLetra"] = "#FFFFFF"
                                                    datos_interfaz_usuario["colorEtiqueta"] = "#4970E3"
                                                    datos_interfaz_usuario["colorBotones"] = "#A6A8A8"
                                                    datos_interfaz_usuario["fondoColorReceta"] = "#151515"

                                                    firestore.collection("preferencias_interfaz")
                                                        .add(datos_interfaz_usuario)
                                                }
                                            }
                                    }
                            }
                            Configuration.UI_MODE_NIGHT_NO -> {
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener { authResult ->
                                        val user = authResult.user
                                        val sharedPrefs = this.getSharedPreferences(
                                            "login",
                                            Context.MODE_PRIVATE
                                        )
                                        val username = sharedPrefs.getString("username", "")
                                        val firestore = FirebaseFirestore.getInstance()

                                        firestore.collection("preferencias_interfaz")
                                            .whereEqualTo("usuario", username.toString())
                                            .get()
                                            .addOnSuccessListener { querySnapshot ->
                                                if (querySnapshot.isEmpty) {
                                                    val datos_interfaz_usuario: MutableMap<String, Any> =
                                                        HashMap()
                                                    datos_interfaz_usuario["usuario"] = username.toString()
                                                    datos_interfaz_usuario["colorLetra"] = "#000000"
                                                    datos_interfaz_usuario["colorEtiqueta"] = "#0025FA"
                                                    datos_interfaz_usuario["colorBotones"] = "#787A7A"
                                                    datos_interfaz_usuario["fondoColorReceta"] = "#8799AC"

                                                    firestore.collection("preferencias_interfaz")
                                                        .add(datos_interfaz_usuario)
                                                }
                                            }
                                    }
                            }

                            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener { authResult ->
                                        val user = authResult.user
                                        val sharedPrefs = this.getSharedPreferences(
                                            "login",
                                            Context.MODE_PRIVATE
                                        )
                                        val username = sharedPrefs.getString("username", "")
                                        val firestore = FirebaseFirestore.getInstance()

                                        firestore.collection("preferencias_interfaz")
                                            .whereEqualTo("usuario", username.toString())
                                            .get()
                                            .addOnSuccessListener { querySnapshot ->
                                                if (querySnapshot.isEmpty) {
                                                    val datos_interfaz_usuario: MutableMap<String, Any> =
                                                        HashMap()
                                                    datos_interfaz_usuario["usuario"] = username.toString()
                                                    datos_interfaz_usuario["colorLetra"] = "#000000"
                                                    datos_interfaz_usuario["colorEtiqueta"] = "#0025FA"
                                                    datos_interfaz_usuario["colorBotones"] = "#787A7A"
                                                    datos_interfaz_usuario["fondoColorReceta"] = "#8799AC"

                                                    firestore.collection("preferencias_interfaz")
                                                        .add(datos_interfaz_usuario)
                                                }
                                            }
                                    }
                            }
                        }




                        //Ir al main
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "La Contraseña/Usuario no se pueden encontrar en la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()

                        binding.pasarMain.isEnabled = true
                        binding.button2.isEnabled = true
                    }
                }
        }

        binding.button2.setOnClickListener {
            val intent = Intent(this, Registracion::class.java)
            startActivity(intent)
        }

        binding.incognito.setOnClickListener {
            var usuarioPreferencia =
                this?.getSharedPreferences(
                    "login",
                    MODE_PRIVATE
                )

            usuarioPreferencia?.edit()
                ?.putString("username", "Anónimo")
                ?.putString("contraseña", "")
                ?.putBoolean("checkbox_checked", binding.checkBox.isChecked)
                ?.apply()

            FirebaseAuth.getInstance().signInAnonymously()
                .addOnSuccessListener { authResult ->
                    guardarColeccionJson(this, "recetas", "recetas.json")
                }

            val intent = Intent(this, MainActivity_Anonimo::class.java)
            startActivity(intent)
        }


    }


    fun guardarColeccionJson(context: Context, coleccion: String, nombreFichero: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(coleccion).get().addOnSuccessListener { querySnapshot ->
            val articleList = mutableListOf<Map<String, Any>>()

            for (document in querySnapshot) {
                articleList.add(document.data)
            }

            val gson = GsonBuilder().setPrettyPrinting().create()
            val json = gson.toJson(articleList)

            val outputStream: OutputStream =
                context.openFileOutput(nombreFichero, Context.MODE_PRIVATE)
            outputStream.write(json.toByteArray())
            outputStream.close()
        }
    }
}