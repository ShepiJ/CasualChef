package com.jose_sanchis_hueso.CasualChef

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityEditarRecetaBinding
import ponerImagenUsuario
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ActivityEditarReceta : AppCompatActivity() {
    private lateinit var binding: ActivityEditarRecetaBinding
    private var imageUri: Uri? = null
    private var nombreImagen: String = ""
    var imagenClicked = false

    companion object {
        const val GALLERY_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ponerTextos()

        binding.editDescripcion.setOnClickListener {
            cambiarTexto(binding.descripcionReceta)
        }
        binding.editIngredientes.setOnClickListener {
            cambiarTexto(binding.ingredientesReceta)
        }

        //Los watcher para no poner más horas de las pedidas
        val horasEditText: EditText = binding.horas
        val minutosEditText: EditText = binding.minutos

        horasEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().isEmpty() && s.toString().toInt() > 24) {
                    horasEditText.setText("24")
                    horasEditText.setSelection(horasEditText.text.length)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        minutosEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().isEmpty() && s.toString().toInt() > 60) {
                    minutosEditText.setText("60")
                    minutosEditText.setSelection(minutosEditText.text.length)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        horasEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = horasEditText.text.toString().padStart(2, '0')
                horasEditText.setText(text)
            }
        }

        minutosEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = minutosEditText.text.toString().padStart(2, '0')
                minutosEditText.setText(text)
            }
        }

        binding.btnMandar.setOnClickListener {

            if (horasEditText.toString().length == 1) {
                val text = horasEditText.text.toString().padStart(2, '0')
                horasEditText.setText(text)
            }

            if (minutosEditText.toString().length == 1) {
                val text = minutosEditText.text.toString().padStart(2, '0')
                minutosEditText.setText(text)
            }

            //Checkeos por de que algo no hay nada sin escribir
            if (binding.nombreReceta.text.isNotEmpty() &&
                binding.ingredientesReceta.text.isNotEmpty() &&
                binding.descripcionReceta.text.isNotEmpty() &&
                binding.tagsReceta.text.isNotEmpty() &&
                binding.horas.text.isNotEmpty() &&
                binding.minutos.text.isNotEmpty()
            ) {

                // Para evitar mandar más de una publicacion a la vez
                binding.btnMandar.isEnabled = false

                var hora = binding.horas.text.toString()
                var minutos = binding.minutos.text.toString()

                val sharedPrefs = getSharedPreferences("idGuardadaReceta", Context.MODE_PRIVATE)
                val idReceta = sharedPrefs.getString("ID", "")

                val sharedPrefsLogin = getSharedPreferences("login", Context.MODE_PRIVATE)
                val username = sharedPrefsLogin.getString("username", "")
                val pass = sharedPrefsLogin.getString("contraseña", "")

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(username.toString() + "@gmail.com", pass.toString())
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        val sharedPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)
                        val username = sharedPrefs.getString("username", "")
                        val firestore = FirebaseFirestore.getInstance()
                        val recetasRef = firestore.collection("recetas")
                        val paqueteReceta: MutableMap<String, Any> = HashMap()
                        paqueteReceta["id"] = idReceta.toString()
                        paqueteReceta["nombre"] = binding.nombreReceta.text.toString()
                        paqueteReceta["desarrollador"] = username.toString()
                        paqueteReceta["ingredientes"] = binding.ingredientesReceta.text.toString()
                        paqueteReceta["descripcion"] = binding.descripcionReceta.text.toString()
                        paqueteReceta["tags"] = binding.tagsReceta.text.toString()
                        val condiciones = listOf(
                            binding.bool1.isChecked,
                            binding.bool2.isChecked,
                            binding.bool3.isChecked,
                            binding.bool4.isChecked,
                            binding.bool5.isChecked
                        )
                        paqueteReceta["condiciones"] = condiciones
                        val rating: Float = binding.dificultad.getRating()
                        val ratingDouble = rating.toDouble()
                        paqueteReceta["dificultad"] = ratingDouble.toString()
                        if (horasEditText.text.toString().length == 1) {
                            val text = horasEditText.text.toString().padStart(2, '0')
                            hora = text
                        }

                        if (minutosEditText.text.toString().length == 1) {
                            val text = minutosEditText.text.toString().padStart(2, '0')
                            minutos = text
                        }

                        paqueteReceta["tiempoPrep"] =
                            hora + ":" + minutos

                        if (imagenClicked==true) {
                            var imagenID: String = UUID.randomUUID().toString() + ".png"

                            val storageRef =
                                FirebaseStorage.getInstance().reference.child("images/${imagenID}")

                            val uploadTask =
                                imageUri?.let { cambiarResolucionImagen_ComprimirGuardar(it) }?.let { storageRef.putFile(it) }
                            uploadTask?.continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let {
                                        throw it
                                    }
                                }

                                storageRef.downloadUrl
                            }
                            paqueteReceta["imagen"] = imagenID
                        } else {
                            paqueteReceta["imagen"] = nombreImagen
                        }
                        firestore.collection("recetas")
                            .whereEqualTo("id", idReceta.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                if (documents.size() > 0) {
                                    val document = documents.documents[0]
                                    document.reference.update(paqueteReceta)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "Se ha actualizado la receta",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Handler().postDelayed({
                                                val intent =
                                                    Intent(this, MainActivity::class.java)
                                                startActivity(intent)
                                            }, 2000)
                                        }

                                        .addOnFailureListener { e ->
                                            Log.e(ContentValues.TAG, "DBFallo", e)
                                            Toast.makeText(
                                                this,
                                                "Ha habido un error al actualizar la receta",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }?.addOnFailureListener { e ->
                                Log.e(ContentValues.TAG, "UploadFallo", e)
                                Toast.makeText(
                                    this,
                                    "Ha habido un error al subir la imagen",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

            } else {
                Toast.makeText(
                    this,
                    "Por favor, completa todos los campos",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        }

        binding.imagenSeleccion.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
                imagenClicked = true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            imageUri = data.data

            if (imageUri != null) {
                binding.imagenSeleccion.setImageURI(null)
                binding.imagenSeleccion.setImageBitmap(cambiarResolucionImagen_ReModelar(imageUri))
            }
        }
    }

    private fun cambiarResolucionImagen_ReModelar(imageUri: Uri?): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            imageUri?.let { this?.contentResolver?.openInputStream(it) },
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
            imageUri?.let { this?.contentResolver?.openInputStream(it) },
            null,
            options
        )!!
    }

    private fun cambiarResolucionImagen_ComprimirGuardar(imageUri: Uri?): Uri {
        if (imageUri == null) {
            throw IllegalArgumentException("imageUri no puede estar vacia")
        }
        val resizedBitmap = cambiarResolucionImagen_ReModelar(imageUri)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        val resizedImageFile = File(this?.cacheDir, "resized_image.jpg")
        val fos = FileOutputStream(resizedImageFile)
        fos.write(byteArray)
        fos.close()

        return Uri.fromFile(resizedImageFile)
    }

    private fun ponerTextos() {
        val sharedPrefs =
            this.getSharedPreferences("idGuardadaReceta", Context.MODE_PRIVATE)
        val id = sharedPrefs.getString("ID", "")

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("recetas")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->

                if (!querySnapshot.isEmpty) {
                    val userData = querySnapshot.documents[0].data

                    with(binding) {
                        nombreReceta.apply {
                            setText(userData?.get("nombre").toString())
                            inputType = InputType.TYPE_CLASS_TEXT
                            isFocusable = true
                            isFocusableInTouchMode = true
                        }

                        val condiciones = userData?.get("condiciones") as? List<Boolean>
                        bool1.isChecked = condiciones?.get(0) ?: false
                        bool2.isChecked = condiciones?.get(1) ?: false
                        bool3.isChecked = condiciones?.get(2) ?: false
                        bool4.isChecked = condiciones?.get(3) ?: false
                        bool5.isChecked = condiciones?.get(4) ?: false

                        val tiempo = userData?.get("tiempoPrep").toString()

                        nombreImagen = userData?.get("imagen").toString()

                        val rutaImagen = File(cacheDir, userData?.get("imagen").toString() + ".jpg")
                        imageUri = rutaImagen.toUri()

                        val horas = tiempo.substring(0, 2)
                        val minutos = tiempo.substring(3, 5)

                        binding.horas.apply {
                            setText(horas)
                            inputType = InputType.TYPE_CLASS_NUMBER
                            isFocusable = true
                            isFocusableInTouchMode = true
                        }
                        binding.minutos.apply {
                            setText(minutos)
                            inputType = InputType.TYPE_CLASS_NUMBER
                            isFocusable = true
                            isFocusableInTouchMode = true
                        }
                        binding.ingredientesReceta.text = userData?.get("ingredientes").toString()
                        binding.descripcionReceta.text = userData?.get("descripcion").toString()
                        tagsReceta.apply {
                            setText(userData?.get("tags").toString())
                            inputType = InputType.TYPE_CLASS_TEXT
                            isFocusable = true
                            isFocusableInTouchMode = true

                        }
                        dificultad.rating = userData?.get("dificultad").toString().toFloat()

                        val storageRef = FirebaseStorage.getInstance().reference.child(
                            "images/" + userData?.get("imagen").toString()
                        )
                        storageRef.downloadUrl.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val imageUrl = task.result.toString()
                                userData?.get("imagen").toString().ponerImagenUsuario(
                                    this@ActivityEditarReceta,
                                    imageUrl,
                                    imagenSeleccion
                                )
                            } else {
                                imagenSeleccion.setImageResource(R.drawable.casualchef)
                            }
                        }

                    }
                }
            }
    }

    fun cambiarTexto(nombreViejo: TextView) {
        val context = nombreViejo.context
        val editText = EditText(context)

        editText.setText(nombreViejo.text.toString())

        val dialog = AlertDialog.Builder(context)
            .setTitle("Cambiar campo")
            .setMessage("Ingrese el nuevo contenido:")
            .setView(editText)
            .setPositiveButton("Aceptar") { _, _ ->
                val nuevoNombre = editText.text.toString()

                nombreViejo.text = nuevoNombre
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()


    }
}