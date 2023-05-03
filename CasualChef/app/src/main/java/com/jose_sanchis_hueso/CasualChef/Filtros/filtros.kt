package com.jose_sanchis_hueso.CasualChef

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentFiltrosBinding

class Filtros : Fragment() {
    private lateinit var binding: FragmentFiltrosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentFiltrosBinding.inflate(inflater,container,false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(requireContext(), FiltroActivity::class.java)
        startActivity(intent)

    }
}