package com.jose_sanchis_hueso.CasualChef

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityLoginBinding
import java.io.OutputStream

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
                saveFirestoreDataToJson(this)
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
                            ?.putString("filtroClase", "nombre")
                            ?.putString("valor", "testCreador")
                            ?.apply()


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




fun saveFirestoreDataToJson(context: Context) {
    val db = FirebaseFirestore.getInstance()

    db.collection("recetas").get().addOnSuccessListener { querySnapshot ->
        val articleList = mutableListOf<Map<String, Any>>()

        for (document in querySnapshot) {
            articleList.add(document.data)
        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(articleList)

        val outputStream: OutputStream =
            context.openFileOutput("recetas.json", Context.MODE_PRIVATE)
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