package com.chema.ptoyecto_tfg.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import com.chema.ptoyecto_tfg.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

object Utils {

    fun StringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }

    fun ImageToString(bitmap: Bitmap?):String?{
        val baos = ByteArrayOutputStream()
        //val bitmap : Bitmap = imgUsuarioPerfil.drawToBitmap()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        var imageString : String? = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return imageString
    }

    fun getBytes(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    fun getBitmap(image: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }
    fun showAlert(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle((R.string.ERROR))
        builder.setMessage((R.string.ocurridoErrorAutenticacion))
        builder.setPositiveButton((R.string.aceptar),null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



    /*
   * Comprueba si el numero de telefono es correcto
   */
    fun checkMovil(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            if (target.length < 6 || target.length > 13) {
                false
            } else {
                Patterns.PHONE.matcher(target).matches()
            }
        }
    }

}