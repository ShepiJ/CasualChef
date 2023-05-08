
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.jose_sanchis_hueso.CasualChef.R

//Ya que por alguna razon no quiere utilizar la cache que se guardaba pues ahora ya no guarda nada

fun String.ponerImagen(context: Context, imageUrl: String, imageView: ImageView){
    Glide.with(context)
        .load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.casualchef)
        .into(imageView)
}

fun String.ponerImagenUsuario(context: Context, imageUrl: String, imageView: ImageView){
    Glide.with(context)
        .load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.user)
        .into(imageView)
}
