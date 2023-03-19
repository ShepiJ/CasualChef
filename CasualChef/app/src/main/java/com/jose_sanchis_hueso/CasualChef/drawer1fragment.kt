package com.jose_sanchis_hueso.CasualChef

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentDrawer1fragmentBinding
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentTabbedPrincipalBinding

class drawer1fragment : Fragment() {

    private lateinit var binding: FragmentDrawer1fragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentDrawer1fragmentBinding.inflate(inflater,container,false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(requireContext(), Login::class.java)
        startActivity(intent)

    }


}


