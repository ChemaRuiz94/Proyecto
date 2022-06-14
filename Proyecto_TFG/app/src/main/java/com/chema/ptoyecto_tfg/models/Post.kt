package com.chema.ptoyecto_tfg.models

import android.net.Uri
import java.io.Serializable

data class Post(
    var postId: String?,
    var userId: String?,
    var imgId: String?,
    var etiquetas: ArrayList<String>?
): Serializable
