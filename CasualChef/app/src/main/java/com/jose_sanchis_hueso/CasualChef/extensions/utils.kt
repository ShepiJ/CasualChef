
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

fun String.ponerImagen(context: Context): Bitmap? {
    val fileName = "$this.jpg"
    val file = File(context.cacheDir, fileName)
    return BitmapFactory.decodeFile(file.path)
}