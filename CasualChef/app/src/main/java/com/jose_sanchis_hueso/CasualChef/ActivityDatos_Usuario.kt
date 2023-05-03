package com.jose_sanchis_hueso.CasualChef

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityDatosUsuarioBinding
import ponerImagenUsuario
import java.io.File
import java.io.FileOutputStream

class ActivityDatos_Usuario : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("datos_usuario")
            .whereEqualTo("usuario", username)
            .get()
            .addOnSuccessListener { querySnapshot ->

                if (!querySnapshot.isEmpty) {
                    val userData = querySnapshot.documents[0].data

                    with(binding) {
                        usuarioUsu.text = userData?.get("usuario").toString()
                        nombreUsu.text = userData?.get("nombre").toString()
                        apellidoUsu.text = userData?.get("apellido").toString()
                        emailUsu.text = userData?.get("email").toString()
                        telefonoUsu.text = userData?.get("telefono").toString()
                        descripcionUsu.text = userData?.get("descripcion").toString()

                        val storageRef = FirebaseStorage.getInstance().reference.child("usuariosImagenes/"+username+".png")
                        storageRef.downloadUrl.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val imageUrl = task.result.toString()
                                username+".png".ponerImagenUsuario(this@ActivityDatos_Usuario, imageUrl, imagenUsu)
                            } else {
                                imagenUsu.setImageResource(R.drawable.casualchef)
                            }
                        }

                    }
                }
            }

        binding.editNombreUsu.setOnClickListener {
            cambiarTexto(binding.nombreUsu)
        }
        binding.editApellidoUsu.setOnClickListener {
            cambiarTexto(binding.apellidoUsu)
        }
        binding.editEmailUsu.setOnClickListener {
            cambiarTexto(binding.emailUsu)
        }
        binding.editTelefUsu.setOnClickListener {
            cambiarTexto(binding.telefonoUsu)
        }
        binding.editDescriUsu.setOnClickListener {
            cambiarTexto(binding.descripcionUsu)
        }

        binding.editImgUsu.setOnClickListener {
            cambiarImagen()
        }

        binding.pasarMain.setOnClickListener {
            hacerUpdate()
            uploadImageToFirebaseStorage()
            Toast.makeText(
                this,
                "Se han guardado los datos correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    fun cambiarTexto(nombreViejo: TextView) {
        val context = nombreViejo.context
        val editText = EditText(context)

        // Crea un Dialog para poner lo que quieras
        val dialog = AlertDialog.Builder(context)
            .setTitle("Cambiar nombre")
            .setMessage("Ingrese un nuevo nombre:")
            .setView(editText)
            .setPositiveButton("Aceptar") { _, _ ->
                val nuevoNombre = editText.text.toString()

                // Update the TextView with the new name
                nombreViejo.text = nuevoNombre
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private val PICK_IMAGE_REQUEST = 1
    private val IMAGE_WIDTH = 100
    private val IMAGE_HEIGHT = 100

    fun cambiarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data

            val imageView: ImageView = findViewById(R.id.imagenUsu)
            imageView.setImageURI(imageUri)

            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, false)

            val cacheDir = cacheDir // get the cache directory
            val fileName = username+".png"
            val file = File(cacheDir, fileName)

            try {
                val outputStream = FileOutputStream(file)
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                val resizedImageUri = Uri.fromFile(file)
                imageView.setImageURI(resizedImageUri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun hacerUpdate() {
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { authResult ->

                val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
                val username = sharedPrefs.getString("username", "")

                val firestore = FirebaseFirestore.getInstance()

                val datos_usuario_Nuevo: MutableMap<String, Any> = HashMap()
                datos_usuario_Nuevo["usuario"] = username.toString()
                datos_usuario_Nuevo["nombre"] = binding.usuarioUsu.text.toString()
                datos_usuario_Nuevo["apellido"] = binding.apellidoUsu.text.toString()
                datos_usuario_Nuevo["email"] = binding.emailUsu.text.toString()
                datos_usuario_Nuevo["telefono"] = binding.telefonoUsu.text.toString()
                datos_usuario_Nuevo["imagen"] = username.toString()+".png"
                datos_usuario_Nuevo["descripcion"] = binding.descripcionUsu.text.toString()

                firestore.collection("datos_usuario").document(username.toString())
                    .set(datos_usuario_Nuevo)
                    .addOnSuccessListener {
                        Log.d(TAG, "Document updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error updating document", e)
                    }
            }
    }


    fun uploadImageToFirebaseStorage() {
        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

        val storageRef = FirebaseStorage.getInstance().reference.child("usuariosImagenes/"+username+".png")

        val file = File(this.cacheDir, username+".png")
        val imageData = file.readBytes()

        val uploadTask = storageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            Log.d(TAG, "Image uploaded successfully")
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error uploading image: ", exception)
        }
    }

}





