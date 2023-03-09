package com.jose_sanchis_hueso.CasualChef

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityMainBinding
import com.jose_sanchis_hueso.CasualChef.databinding.FragmentOptionsBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess


class OptionsFragment : Fragment() {
    private lateinit var binding: FragmentOptionsBinding
    private lateinit var binding2: ActivityMainBinding
    private var imageUri: Uri? = null

    companion object {
        const val GALLERY_REQUEST_CODE = 100
        const val MAX_IMAGE_SIZE = 1024 // 1 MB in kilobytes
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding2 = (activity as MainActivity).binding

        // pide acceso a archivos y tal, sin esto no podria mandar imagenes
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                GALLERY_REQUEST_CODE
            )
        } else {
            // permission already granted, do something here
        }

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

                    // Upload the selected image to Firebase Storage
                    var imagenID:String=UUID.randomUUID().toString()

                    val storageRef =
                        FirebaseStorage.getInstance().reference.child("images/${imagenID}")

                    val uploadTask =
                        imageUri?.let { resizeImage(it) }?.let { storageRef.putFile(it) }
                    uploadTask?.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        storageRef.downloadUrl
                    }?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cosas["imagen"] = imagenID

                            // Add the `cosas` map to the `recetas` collection in Firestore
                            firestore.collection("recetas")
                                .add(cosas)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Se ha publicado la receta",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    Handler().postDelayed({
                                        binding2.navHostFragment.visibility = View.VISIBLE
                                        binding2.navHostFragment2.visibility = View.INVISIBLE
                                    }, 2000)
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "DBFallo", e)
                                    Toast.makeText(
                                        context,
                                        "Ha habido un error al publicar la receta",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }?.addOnFailureListener { e ->
                        Log.e(TAG, "UploadFallo", e)
                        Toast.makeText(
                            context,
                            "Ha habido un error al subir la imagen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


        }

        binding.btnVolver.setOnClickListener {
            binding2.navHostFragment.visibility = View.VISIBLE
            binding2.navHostFragment2.visibility = View.INVISIBLE
        }

        binding.imagenSeleccion.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Get the selected image URI
            imageUri = data.data

            if (imageUri != null) {
                // Set the image view with the selected image after resizing it
                binding.imagenSeleccion.setImageURI(null)
                binding.imagenSeleccion.setImageBitmap(getResizedBitmap(imageUri))
            }
        }
    }

    private fun getResizedBitmap(imageUri: Uri?): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            imageUri?.let { context?.contentResolver?.openInputStream(it) },
            null,
            options
        )

        val maxDimension = 1080
        var width = options.outWidth
        var height = options.outHeight

        val scaleFactor = Math.min(
            width / maxDimension,
            height / maxDimension
        )

        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor

        return BitmapFactory.decodeStream(
            imageUri?.let { context?.contentResolver?.openInputStream(it) },
            null,
            options
        )!!
    }

    private fun resizeImage(imageUri: Uri?): Uri {
        if (imageUri == null) {
            throw IllegalArgumentException("imageUri cannot be null")
        }
        val resizedBitmap = getResizedBitmap(imageUri)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        // Create a new file in the app's local storage directory
        val resizedImageFile = File(context?.cacheDir, "resized_image.jpg")
        val fos = FileOutputStream(resizedImageFile)
        fos.write(byteArray)
        fos.close()

        // Return the URI of the new file
        return Uri.fromFile(resizedImageFile)
    }


}
