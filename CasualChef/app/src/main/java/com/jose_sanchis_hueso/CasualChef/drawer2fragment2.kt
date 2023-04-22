package com.jose_sanchis_hueso.CasualChef

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentDrawer1fragmentBinding
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentDrawer2fragment2Binding

class drawer2fragment2 : Fragment() {

    private lateinit var binding: FragmentDrawer2fragment2Binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentDrawer2fragment2Binding.inflate(inflater,container,false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(requireContext(), ActivityPreferencias_Interfaz::class.java)
        startActivity(intent)

    }


}
