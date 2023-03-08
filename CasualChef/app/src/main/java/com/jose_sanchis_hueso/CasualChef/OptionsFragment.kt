package com.jose_sanchis_hueso.CasualChef

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityMainBinding
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentOptionsBinding
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess


class OptionsFragment : Fragment() {
    private lateinit var binding: FragmentOptionsBinding
    private lateinit var binding2: ActivityMainBinding

    companion object {
        const val GALLERY_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding2 = (activity as MainActivity).binding

        binding.btnMandar.setOnClickListener {

            FirebaseAuth.getInstance().signInAnonymously()
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    val firestore = FirebaseFirestore.getInstance()
                    val cosas: MutableMap<String, Any> = HashMap()
                    cosas["id"] = UUID.randomUUID().toString()
                    cosas["nombre"] = binding.nombreReceta.text.toString()
                    cosas["desarrollador"] = "cosa"
                    cosas["descripcion"] = binding.descripcionReceta.text.toString()
                    cosas["puntuacion"] = 0
                    cosas["tags"] = binding.tagsReceta.text.toString()
                    cosas["imagen"] = "monsterHunter.jpg"

                        // Add the `cosas` map to the `recetas` collection in Firestore
                        firestore.collection("recetas")
                            .add(cosas)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Se ha publicado la receta", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "DBFallo", e)
                                Toast.makeText(context, "Ha habido un error al publicar la receta", Toast.LENGTH_SHORT).show()
                            }
                    }



            Handler().postDelayed({
                binding2.navHostFragment.visibility = View.VISIBLE
                binding2.navHostFragment2.visibility = View.INVISIBLE
            }, 2000)
        }

        binding.btnVolver.setOnClickListener {
            binding2.navHostFragment.visibility = View.VISIBLE
            binding2.navHostFragment2.visibility = View.INVISIBLE
        }

        binding.imagenSeleccion.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Get the selected image URI
            val imageUri: Uri? = data.data

            // Set the image view with the selected image
            binding.imagenSeleccion.setImageURI(imageUri)
        }
    }
}

