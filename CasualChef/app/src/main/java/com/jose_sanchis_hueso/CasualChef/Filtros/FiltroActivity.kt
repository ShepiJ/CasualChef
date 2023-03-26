package com.jose_sanchis_hueso.CasualChef

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityFiltroBinding

class FiltroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var binding: ActivityFiltroBinding

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtro)
    }
}