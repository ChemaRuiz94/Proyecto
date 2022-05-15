package com.chema.ptoyecto_tfg.models

import java.io.Serializable

data class BasicUser(
    var userId: String?,
    var userName: String?,
    var email: String?,
    var phone: String?,
    var img: String?,
    var rol: ArrayList<Rol>?,
    var idFavoritos: ArrayList<String?>?
):Serializable
