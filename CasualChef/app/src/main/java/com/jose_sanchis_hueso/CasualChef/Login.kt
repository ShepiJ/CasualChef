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

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

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


        //Coge los datos de la base de datos de forma anonima
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { authResult ->
                saveFirestoreDataToJson(this)
            }

//El usuario no puede poner un nombre con espacios.
        binding.pasarMain.setOnClickListener {
            val email = binding.cogerUsuario.text.toString()+"@gmail.com"
            val password = binding.cogerContraseA.text.toString()

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
                            ?.putString("username", binding.cogerUsuario.text.toString())?.apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        //Paso 2. Se intenta crear una cuenta nueva ya que si el login da error es que no existe o está mal hecha
                        Log.e(TAG, "signInWithEmailAndPassword failed", task.exception)
                        mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    //Paso 3. Si no hay problema al crear la nueva cuenta se hace login a ella
                                    //Si no permite crearla es porque ya existe esa cuenta
                                    mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this) { task ->
                                            if (task.isSuccessful) {
                                                var usuarioPreferencia =
                                                    this?.getSharedPreferences(
                                                        "login",
                                                        MODE_PRIVATE
                                                    )
                                                usuarioPreferencia?.edit()?.putString(
                                                    "username",
                                                    binding.cogerUsuario.text.toString()
                                                )?.apply()

                                                val intent = Intent(this, MainActivity::class.java)
                                                startActivity(intent)
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "Authentication failed.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    //
                                    Toast.makeText(
                                        this,
                                        "La cuenta que está intentando crear ya existe.\nTambién ha podido la ha puesto la contraseña mal.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
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

