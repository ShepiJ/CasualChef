package com.jose_sanchis_hueso.CasualChef.drawers

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jose_sanchis_hueso.CasualChef.ActivityPreferencias_Interfaz
import com.jose_sanchis_hueso.CasualChef.databinding.CargandoBinding

class Cargando__ : Fragment() {

    private lateinit var binding: CargandoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return CargandoBinding.inflate(inflater,container,false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(requireContext(), ActivityPreferencias_Interfaz::class.java)
        startActivity(intent)

    }


}
