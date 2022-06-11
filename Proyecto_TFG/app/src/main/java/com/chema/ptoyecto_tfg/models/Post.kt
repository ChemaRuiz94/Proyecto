package com.chema.ptoyecto_tfg.models

import java.io.Serializable

data class Post(
    var postId: String?,
    var userId: String?,
    var img: String?,
    var etiquetas: ArrayList<String>?
): Serializable
