package com.jose_sanchis_hueso.CasualChef

import android.R
import OnItemClick
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityFiltroBinding
import com.jose_sanchis_hueso.CasualChef.model.Articulo

class FiltroActivity : AppCompatActivity() , OnItemClick {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lateinit var binding: ActivityFiltroBinding


        binding = ActivityFiltroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Para el spinner
        val spinner: Spinner = binding.spinnerFiltro
        val items = listOf("tags", "nombre", "autor", "ingredientes", "descripcion")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter



        binding.buttonFiltrar.setOnClickListener {

            var filtroPreferencia =
                this?.getSharedPreferences(
                    "filtro",
                    MODE_PRIVATE
                )
            filtroPreferencia?.edit()
                ?.putString("filtroClase", spinner.selectedItem.toString())
                ?.putString("valor", binding.textoFiltrar.text.toString())
                ?.apply()

            val navHostFragment =
                supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
            val navController = navHostFragment.navController

            val currentDestination = navController.currentDestination
            currentDestination?.let {
                navController.navigate(it.id)
            }
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    private fun abrirDetalle(id:String) {

        val intent = Intent(this,DetalleActivity::class.java)
        intent.putExtra("ID",id)
        startActivity(intent)
    }

    override fun onIntemClick(articulo: Articulo) {
        abrirDetalle(articulo.id)

    }

}











