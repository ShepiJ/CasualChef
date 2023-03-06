package com.jose_sanchis_hueso.CasualChef

import OnItemClick
import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityMainBinding
import com.jose_sanchis_hueso.CasualChef.model.Articulo
import java.io.OutputStream
import kotlin.math.cos

class MainActivity : AppCompatActivity(), OnItemClick {

    lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {




        val screenSplash = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(
            ActivityMainBinding.inflate(layoutInflater).also { binding = it }.root
        )
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment2) as NavHostFragment

        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.optionsFragment
            )
        )
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)

        Thread.sleep(1000)
        screenSplash.setKeepOnScreenCondition{false}

        screenSplash.setOnExitAnimationListener{splashScreenView ->
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

            val icon=splashScreenView.iconView
            val iconAnimator = ValueAnimator
                .ofInt(icon.height,0)
                .setDuration(1000)

            iconAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                icon.layoutParams.width = value
                icon.layoutParams.height = value
                icon.requestLayout()
                if (value==0) slideBack.start()
            }

            AnimatorSet().apply {
                interpolator = AnticipateInterpolator(5f)
                play(iconAnimator)
                start()
            }
        }






        //Lo del firebase
/*
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                val firestore = FirebaseFirestore.getInstance()
                val cosas: MutableMap<String, Any> = HashMap()
                cosas["id"] = 1
                cosas["nombre"] = "ben dover"
                cosas["desarrollador"] = "kneegrows"
                cosas["tags"] = "tag"
                cosas["imagen"] = "monsterHunter.jpg"
                cosas["descripcion"] = "descripcosa"
                cosas["tipo"] = "consola"
                firestore.collection("recetas")
                    .add(cosas)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(applicationContext, "Se ha guardado la receta", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "DBFallo", e)
                        Toast.makeText(applicationContext, "Ha habido un error", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Authentication failed", e)
                Toast.makeText(applicationContext, "Ha habido un error de autenticación", Toast.LENGTH_SHORT).show()
            }
*/
// crea el json con los datos nuevos
        saveFirestoreDataToJson(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu,menu)
        return true
    }

    //Cuando selecciono una opción del options menú hace invisible el fragmento del tabbed para que no se solape y viceversa
    override fun onOptionsItemSelected(item: MenuItem): Boolean {


            binding.navHostFragment.visibility = View.INVISIBLE

            binding.navHostFragment2.visibility = View.VISIBLE


        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item)
    }

    override fun onIntemClick(articulo: Articulo) {
                abrirDetalle(articulo.id)

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

            val outputStream: OutputStream = context.openFileOutput("recetas.json", Context.MODE_PRIVATE)
            outputStream.write(json.toByteArray())
            outputStream.close()
        }
    }


    private fun abrirDetalle(id:Int) {

        val intent = Intent(this,DetalleActivity::class.java)
        intent.putExtra("ID",id)
        startActivity(intent)
    }
}