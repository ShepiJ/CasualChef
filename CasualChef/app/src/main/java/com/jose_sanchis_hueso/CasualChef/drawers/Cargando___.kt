package com.jose_sanchis_hueso.CasualChef.drawers

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jose_sanchis_hueso.CasualChef.ActivityDatos_Usuario
import com.jose_sanchis_hueso.CasualChef.databinding.CargandoBinding

class Cargando___ : Fragment() {
    private lateinit var binding: CargandoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return CargandoBinding.inflate(inflater,container,false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val intent = Intent(requireContext(), ActivityDatos_Usuario::class.java)
        startActivity(intent)

    }


}