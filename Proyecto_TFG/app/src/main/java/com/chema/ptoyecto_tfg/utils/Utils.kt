package com.chema.ptoyecto_tfg.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.lang.Exception

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
}