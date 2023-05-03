package com.jose_sanchis_hueso.CasualChef.drawers

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jose_sanchis_hueso.CasualChef.Login
import com.jose_sanchis_hueso.CasualChef.databinding.CargandoBinding

class Cargando_ : Fragment() {

    private lateinit var binding: CargandoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return CargandoBinding.inflate(inflater,container,false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var usuarioPreferencia =
            requireContext().getSharedPreferences(
                "login",
                AppCompatActivity.MODE_PRIVATE
            )

        usuarioPreferencia.edit()
            .putBoolean("checkbox_checked", false)
            .apply()


        val intent = Intent(requireContext(), Login::class.java)
        startActivity(intent)

    }


}


