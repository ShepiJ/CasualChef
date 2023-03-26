package com.jose_sanchis_hueso.CasualChef

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
import com.jose_sanchis_hueso.CasualChef.adapters.MyRecyclerViewAdapter
import com.jose_sanchis_hueso.CasualChef.model.Articulo


class ArticulosFragmentFiltro(opcion: String) : Fragment() {
    private var columnCount = 1
    var listener: OnItemClick? = null
    var filtro = opcion

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

            val sharedPrefs = context?.getSharedPreferences("filtro", Context.MODE_PRIVATE)
            val prefFiltro = sharedPrefs?.getString("filtroClase", "")
            val prefValor = sharedPrefs?.getString("valor", "")

            val articuloList = if (filtro == prefValor) {
                Articulo.getFiltro(requireContext(), prefValor)
            } else {
                Articulo.getFiltro(requireContext()).filter {
                    when (prefFiltro) {
                        "tags" -> it.tags.contains(prefValor.toString(), ignoreCase = true)
                        "nombre" -> it.nombre.contains(prefValor.toString(), ignoreCase = true)
                        else -> throw IllegalArgumentException("Propiedad inválida")
                    }
                }
            }

            view.adapter = MyRecyclerViewAdapter(articuloList, listener)
        }

        return view
    }
}