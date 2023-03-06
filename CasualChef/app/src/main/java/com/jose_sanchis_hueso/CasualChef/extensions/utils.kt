
import android.content.Context

fun String.ponerImagen(context: Context): Int {

    val nombreImagen = this.split(".")[0].lowercase()

    val id = context.resources.getIdentifier(
        nombreImagen,
        "drawable",
        context.packageName
    )

    return id

}