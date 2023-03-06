package com.jose_sanchis_hueso.CasualChef

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityMainBinding
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentOptionsBinding


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
            binding2.navHostFragment.visibility = View.VISIBLE
            binding2.navHostFragment2.visibility = View.INVISIBLE
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

