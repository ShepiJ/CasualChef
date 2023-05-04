package com.jose_sanchis_hueso.CasualChef

import OnItemClick
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity(), OnItemClick {

    lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navControllerDrawer: NavController
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando...")
        progressDialog.setCancelable(false)

        progressDialog.show()

        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")
        val pass = sharedPrefs.getString("contraseña", "")


        FirebaseAuth.getInstance().signInWithEmailAndPassword(username.toString()+"@gmail.com", pass.toString())
            .addOnSuccessListener { authResult ->
                guardarColeccionJson(this, "recetas", "recetas.json"){
                    progressDialog.dismiss()
                }

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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }


        return NavigationUI.onNavDestinationSelected(
            item,
            navController
        ) || super.onOptionsItemSelected(item)

    }

    override fun onIntemClick(articulo: Articulo) {
        abrirDetalle(articulo.id)

    }

    fun guardarColeccionJson(
        context: Context,
        coleccion: String,
        nombreFichero: String,
        callback: () -> Unit
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

            // Call the callback when the data is downloaded
            callback()
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