package com.jose_sanchis_hueso.CasualChef.tabbed

import OnItemClick
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jose_sanchis_hueso.CasualChef.R
import com.jose_sanchis_hueso.CasualChef.adapters.MyRecyclerViewAdapter
import com.jose_sanchis_hueso.CasualChef.model.Receta


class RecetasFragment(desarrollador: String) : Fragment() {
    private var columnCount = 1
    var listener: OnItemClick? = null
    var desarrollador = desarrollador

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnItemClick) listener = context
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_articulos, container, false)
        if (view is RecyclerView) {
            view.layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }


            val sharedPrefs = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)
            val username = sharedPrefs.getString("username", "")

            val recetaLists = if (desarrollador == username) {
                Receta.getReceta(requireContext(), username)
            } else {
                Receta.getReceta(requireContext()).filter { it.desarrollador != username }
            }
            view.adapter = MyRecyclerViewAdapter(recetaLists, listener)
        }
        return view
    }
}