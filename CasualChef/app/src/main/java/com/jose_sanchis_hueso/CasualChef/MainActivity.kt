package com.jose_sanchis_hueso.CasualChef

import OnItemClick
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityMainBinding
import com.jose_sanchis_hueso.CasualChef.model.Receta
import java.io.OutputStream

class MainActivity : AppCompatActivity(), OnItemClick {

    lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navControllerDrawer: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")
        val pass = sharedPrefs.getString("contraseña", "")


        FirebaseAuth.getInstance().signInWithEmailAndPassword(username.toString()+"@gmail.com", pass.toString())
            .addOnSuccessListener { authResult ->
                guardarColeccionJson(this, "recetas", "recetas.json")
            }



        if (username != null) {
            val appName = getString(R.string.app_name)
            val newAppName = appName.replace("CasualChef", username)
            setTitle(newAppName)
        }

        super.onCreate(savedInstanceState)
        setContentView(
            ActivityMainBinding.inflate(layoutInflater).also { binding = it }.root
        )
        setSupportActionBar(binding.toolbar)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment2) as NavHostFragment

        val navHostFragmentDrawer =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment3) as NavHostFragment

        navControllerDrawer = navHostFragmentDrawer.navController
        navController = navHostFragment.navController


        //Mete el normal options

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.options_vacio,
            )
        )
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)

        //Mete el drawer, lo que sale de la izquierda

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.vacio,
            ),
            binding.drawerLayout
        )
        NavigationUI.setupWithNavController(binding.navigationView, navControllerDrawer)
        NavigationUI.setupWithNavController(
            binding.toolbar,
            navControllerDrawer,
            appBarConfiguration
        )



    }

    override fun onResume() {
        super.onResume()
        navControllerDrawer.navigate(R.id.vacio)

        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

        if (username != null) {
            val appName = getString(R.string.app_name)
            val newAppName = appName.replace("CasualChef", username)
            setTitle(newAppName)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onBackPressed() {
        navControllerDrawer.navigate(R.id.vacio)

        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

        if (username != null) {
            val appName = getString(R.string.app_name)
            val newAppName = appName.replace("CasualChef", username)
            setTitle(newAppName)
        }
    }

    //Cuando selecciono una opción del options menú hace invisible el fragmento del tabbed para que no se solape y viceversa
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.optionsFragment1 -> {
                val intent = Intent(this, Crear::class.java)
                startActivity(intent)
            }
            R.id.filtros -> {
                val intent = Intent(this, FiltroActivity::class.java)
                startActivity(intent)
            }
            R.id.reset -> {
                recargar()
            }
            R.id.reset -> {

                recargar()
            }
        }
        return NavigationUI.onNavDestinationSelected(
            item,
            navController
        ) || super.onOptionsItemSelected(item)
    }

    override fun onIntemClick(receta: Receta) {
        abrirDetalle(receta.id)

    }

    fun guardarColeccionJson(
        context: Context,
        coleccion: String,
        nombreFichero: String,
    ) {
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

    fun recargar() {
        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")
        val pass = sharedPrefs.getString("contraseña", "")


        FirebaseAuth.getInstance().signInWithEmailAndPassword(username.toString()+"@gmail.com", pass.toString())
            .addOnSuccessListener { authResult ->
                guardarColeccionJson(this, "recetas", "recetas.json")
            }

        //Recarga el los datos del recycleviewer por si algo nuevo no ha salido porque le json no se ha descargado con tiempo o algo parecido
        val progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE

        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration = 1000
        animator.interpolator = LinearInterpolator()
        animator.start()

        animator.doOnEnd {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            val currentDestination = navController.currentDestination
            currentDestination?.let {
                navController.navigate(it.id)
            }

            progressBar.visibility = View.GONE
            //Me gustaria que pudiera reiniciar el nav con el tab en el que estaba el usuario pero nose como hacerlo.
        }
    }

    private fun abrirDetalle(id: String) {
        try {
            var guardarIdReceta =
                this?.getSharedPreferences(
                    "idGuardadaReceta",
                    MODE_PRIVATE
                )
            guardarIdReceta?.edit()
                ?.putString("ID", id)
                ?.apply()

            val intent = Intent(this, DetalleActivity::class.java)
            intent.putExtra("ID", id)
            startActivity(intent)
        } catch (e: Exception) {
        }
    }
}