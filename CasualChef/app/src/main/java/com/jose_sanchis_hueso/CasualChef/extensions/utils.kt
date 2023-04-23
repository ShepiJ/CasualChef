
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jose_sanchis_hueso.CasualChef.R
import java.io.File

fun String.ponerImagen(context: Context, imageUrl: String, imageView: ImageView){
    Glide.with(context)
        .load(imageUrl)
        .placeholder(R.drawable.casualchef)
        .into(imageView)
}
