package com.jose_sanchis_hueso.CasualChef.adapters

import OnItemClick
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.jose_sanchis_hueso.CasualChef.R
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentCartaArticuloBinding
import com.jose_sanchis_hueso.CasualChef.model.Receta
import ponerImagen

class MyRecyclerViewAdapter(
    private val recetaList: List<Receta>,
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

        val articulo = recetaList[position]
        Log.d("ArticulosFragmentLog", "idEKISDE = ${articulo.id}")
        holder.tvNombre.text = articulo.nombre
        holder.tvDesarrollador.text = articulo.desarrollador

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${articulo.imagen}")
        storageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val imageUrl = task.result.toString()
                articulo.imagen.ponerImagen(holder.ivArticulo.context, imageUrl, holder.ivArticulo)
            } else {
                holder.ivArticulo.setImageResource(R.drawable.casualchef)
            }
        }



        holder.itemView.tag = articulo
        holder.itemView.setOnClickListener(holder)
    }


    override fun getItemCount(): Int = recetaList.size

    inner class ViewHolder(binding: FragmentCartaArticuloBinding) : RecyclerView.ViewHolder(binding.root),View.OnClickListener {

        val tvNombre: TextView = binding.tvTituloVideojuegos
        val tvDesarrollador: TextView = binding.tvDeveloper
        val ivArticulo: ImageView = binding.ivVideojuego

        override fun toString(): String {
            return super.toString() + "${tvNombre} is ${tvDesarrollador}"
        }

        override fun onClick(view: View?) {
            val receta = view?.tag as Receta
            listener?.onIntemClick(receta)
        }
    }
}