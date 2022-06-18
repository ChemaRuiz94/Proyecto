package com.chema.ptoyecto_tfg.models

import java.io.Serializable

data class BasicUser(
    var userId: String?,
    var userName: String?,
    var email: String?,
    var phone: Int?,
    var img: String?,
    var rol: String,
    var idFavoritos: ArrayList<String>?
):Serializable
