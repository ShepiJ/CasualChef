package com.jose_sanchis_hueso.CasualChef

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentTabbedPrincipalfiltroBinding

class TabbedPrincipalFragmentFiltro : Fragment() {
        private lateinit var binding: FragmentTabbedPrincipalfiltroBinding


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return FragmentTabbedPrincipalfiltroBinding.inflate(inflater,container,false).also { binding = it }.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.viewPager.adapter = PageAdapter(requireContext(), this)

        }

        class PageAdapter(private val context: Context, fragment: Fragment) : FragmentStateAdapter(fragment) {

            override fun getItemCount(): Int = 1

            override fun createFragment(position: Int): Fragment {
                val sharedPrefs = context?.getSharedPreferences("filtro", Context.MODE_PRIVATE)
                val prefValor = sharedPrefs?.getString("valor", "")

                //Dependiendo de que posicion esté el fragmento cargará los articulos dependiendo del tipo que tengas
                return when(position){
                    0 -> RecetasFragmentFiltro(prefValor.toString())
                    else -> RecetasFragmentFiltro(prefValor.toString())
                }
            }
        }
    }