package com.jose_sanchis_hueso.CasualChef.adapters

import OnItemClick
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jose_sanchis_hueso.CasualChef.R
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentCartaArticuloBinding
import com.jose_sanchis_hueso.CasualChef.model.Articulo
import ponerImagen

class MyRecyclerViewAdapter(
    private val articuloList: List<Articulo>,
    private val listener: OnItemClick?
) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentCartaArticuloBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val articulo = articuloList[position]
        Log.d("ArticulosFragmentLog", "idEKISDE = ${articulo.id}")
        holder.tvNombre.text = articulo.nombre
        holder.tvDesarrollador.text = articulo.desarrollador
        holder.ratingBar.numStars = 10
        holder.ratingBar.rating = articulo.puntuacion

        val bitmap = articulo.imagen.ponerImagen(holder.ivArticulo.context)
        if (bitmap != null) {
            holder.ivArticulo.setImageBitmap(bitmap)
        } else {
            holder.ivArticulo.setImageResource(R.drawable.casualchef)
        }

        holder.itemView.tag = articulo
        holder.itemView.setOnClickListener(holder)
    }

    override fun getItemCount(): Int = articuloList.size

    inner class ViewHolder(binding: FragmentCartaArticuloBinding) : RecyclerView.ViewHolder(binding.root),View.OnClickListener {

        val tvNombre: TextView = binding.tvTituloVideojuegos
        val tvDesarrollador: TextView = binding.tvDeveloper
        val ratingBar: RatingBar = binding.estrellas
        val ivArticulo: ImageView = binding.ivVideojuego

        override fun toString(): String {
            return super.toString() + "${tvNombre} is ${tvDesarrollador}"
        }

        override fun onClick(view: View?) {
            val articulo = view?.tag as Articulo
            listener?.onIntemClick(articulo)
        }
    }
}