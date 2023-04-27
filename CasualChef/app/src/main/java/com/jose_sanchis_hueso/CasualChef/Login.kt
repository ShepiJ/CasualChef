package com.jose_sanchis_hueso.CasualChef

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityLoginBinding
import java.io.OutputStream
import kotlin.collections.HashMap

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
        val isChecked = sharedPref.getBoolean("checkbox_checked", false)

        if (isChecked) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        //Coge los datos de la base de datos de forma anonima
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { authResult ->
                guardarColeccionJson(this,"recetas","recetas.json")
            }

//El usuario no puede poner un nombre con espacios.
        binding.pasarMain.setOnClickListener {
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

                        //para filtros
                        var filtroPreferencia =
                            this?.getSharedPreferences(
                                "filtro",
                                MODE_PRIVATE
                            )
                        filtroPreferencia?.edit()
                            ?.putString("filtroClase", "autor")
                            ?.putString("valor", "")
                            ?.apply()


                        //Generando los datos usuario

                        FirebaseAuth.getInstance().signInAnonymously()
                            .addOnSuccessListener { authResult ->
                                val user = authResult.user
                                val sharedPrefs = this.getSharedPreferences(
                                    "login",
                                    Context.MODE_PRIVATE
                                )
                                val username = sharedPrefs.getString("username", "")
                                val firestore = FirebaseFirestore.getInstance()

                                // Check if a document already exists for the user
                                firestore.collection("datos_usuario")
                                    .whereEqualTo("usuario", username.toString())
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        if (querySnapshot.isEmpty) {
                                            // If no documents exist, add a new one
                                            val datos_usuario_Nuevo: MutableMap<String, Any> = HashMap()
                                            datos_usuario_Nuevo["usuario"] = username.toString()
                                            datos_usuario_Nuevo["nombre"] = "default"
                                            datos_usuario_Nuevo["apellido"] = "default"
                                            datos_usuario_Nuevo["email"] = "default"
                                            datos_usuario_Nuevo["telefono"] = "default"
                                            datos_usuario_Nuevo["imagen"] = "default"
                                            datos_usuario_Nuevo["descripcion"] = "default"

                                            firestore.collection("datos_usuario").add(datos_usuario_Nuevo)
                                        }
                                    }
                            }

                        FirebaseAuth.getInstance().signInAnonymously()
                            .addOnSuccessListener { authResult ->
                                guardarColeccionJson(this,"datos_usuario","usuarios.json")
                            }


                        //Ir al main
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                                } else {
                                    //
                                    Toast.makeText(
                                        this,
                                        "La Contraseña/Usuario no se pueden encontrar en la base de datos",
                                        Toast.LENGTH_SHORT
                                    ).show()
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

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
                }
        }




fun guardarColeccionJson(context: Context,coleccion: String,nombreFichero: String) {
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

/**
val screenSplash = installSplashScreen()
screenSplash.setKeepOnScreenCondition { false }

Thread.sleep(1000)
screenSplash.setOnExitAnimationListener { splashScreenView ->
val slideBack = ObjectAnimator.ofFloat(
splashScreenView.view,
View.TRANSLATION_Y,
0f,
splashScreenView.view.width.toFloat(),
-splashScreenView.view.width.toFloat()
).apply {
interpolator = DecelerateInterpolator()
duration = 600
doOnEnd { splashScreenView.remove() }
}

val icon = splashScreenView.iconView
val iconAnimator = ValueAnimator
.ofInt(icon.height, 0)
.setDuration(1000)

iconAnimator.addUpdateListener {
val value = it.animatedValue as Int
icon.layoutParams.width = value
icon.layoutParams.height = value
icon.requestLayout()
if (value == 0) slideBack.start()
}

AnimatorSet().apply {
interpolator = AnticipateInterpolator(5f)
play(iconAnimator)
start()
}
}
 **/