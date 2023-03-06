package com.jose_sanchis_hueso.CasualChef

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CartaVIdeojuegoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var id: Int? = null
    private var objName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_carta_articulo, container, false)
    }
}