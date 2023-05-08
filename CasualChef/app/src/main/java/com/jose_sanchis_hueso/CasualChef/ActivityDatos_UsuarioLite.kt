package com.jose_sanchis_hueso.CasualChef

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityDatosUsuarioBinding
import com.jose_sanchis_hueso.CasualChef.databinding.ActivityDatosUsuarioLiteBinding
import ponerImagenUsuario

class ActivityDatos_UsuarioLite : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioLiteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosUsuarioLiteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sharedPrefs = getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("datos_usuario")
            .whereEqualTo("usuario", username.toString())
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

                        val storageRef =
                            FirebaseStorage.getInstance().reference.child("usuariosImagenes/"+username + ".png")
                        storageRef.downloadUrl.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val imageUrl = task.result.toString()
                                username + ".png".ponerImagenUsuario(
                                    this@ActivityDatos_UsuarioLite,
                                    imageUrl,
                                    imagenUsu
                                )
                            } else {
                                imagenUsu.setImageResource(R.drawable.casualchef)
                            }
                        }

                    }
                }
            }
    }
}

