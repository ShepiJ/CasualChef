package com.jose_sanchis_hueso.CasualChef

import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.getString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentTabbedPrincipalBinding

    class TabbedPrincipalFragment : Fragment() {
        private lateinit var binding: FragmentTabbedPrincipalBinding


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return FragmentTabbedPrincipalBinding.inflate(inflater,container,false).also { binding = it }.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.viewPager.adapter = PageAdapter(requireContext(), this)

            TabLayoutMediator(
                binding.tabLayout, binding.viewPager
            ) {tab, position ->
                when (position){
                    0 -> tab.text = "Mis Recetas"
                    1 -> tab.text = "Recetas Globales"
                    else -> tab.text = "Recetas Globales"
                }
            }.attach()
        }

        class PageAdapter(private val context: Context, fragment: Fragment) : FragmentStateAdapter(fragment) {

            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                val sharedPrefs = context.getSharedPreferences("login", Context.MODE_PRIVATE)
                val username = sharedPrefs.getString("username", "")

                //Dependiendo de que posicion esté el fragmento cargará los articulos dependiendo del tipo que tengas
                return when(position){
                    0 -> ArticulosFragment(username.toString())
                    1 -> ArticulosFragment("otro")
                    else -> ArticulosFragment("otro")
                }
            }
        }
    }