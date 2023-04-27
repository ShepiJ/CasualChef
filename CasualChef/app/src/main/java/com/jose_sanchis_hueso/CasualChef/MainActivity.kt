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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityMainBinding
import com.jose_sanchis_hueso.CasualChef.model.Articulo
import java.io.File
import java.io.OutputStream
import kotlin.math.cos

class MainActivity : AppCompatActivity(), OnItemClick {

    lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navControllerDrawer: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

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
                R.id.optionsFragment,
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
        NavigationUI.setupWithNavController(binding.toolbar, navControllerDrawer, appBarConfiguration)



// crea el json con los datos nuevos
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { authResult ->
                saveFirestoreDataToJson(this)
            }
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
        menuInflater.inflate(R.menu.options_menu,menu)
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
                /**
                binding.navHostFragment.visibility = View.INVISIBLE

                binding.navHostFragment2.visibility = View.VISIBLE
                **/
            }
            R.id.filtros -> {
                val intent = Intent(this, FiltroActivity::class.java)
                startActivity(intent)
            }
        }


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

            // download and cache all images
            downloadAndCacheImages(context)
        }
    }


    fun downloadAndCacheImages(context: Context) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("images")

        // get a list of all files in the "images" folder
        storageRef.listAll().addOnSuccessListener { listResult ->
            // loop through each file and download it
            listResult.items.forEach { item ->
                val cacheFile = File(context.cacheDir, "${item.name}.jpg") // append ".jpg" to the cache file name

                // if the image is not already cached, download it and cache it
                if (!cacheFile.exists()) {
                    // download the image to a temporary file
                    val tempFile = File.createTempFile("image", null, context.cacheDir)
                    item.getFile(tempFile).addOnSuccessListener {
                        // move the temporary file to the cache directory
                        tempFile.renameTo(cacheFile)

                        // cache the image using Glide
                        Glide.with(context)
                            .load(cacheFile)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .preload()
                    }
                }
            }
        }
    }

    private fun abrirDetalle(id:String) {

        val intent = Intent(this,DetalleActivity::class.java)
        intent.putExtra("ID",id)
        startActivity(intent)
    }
}