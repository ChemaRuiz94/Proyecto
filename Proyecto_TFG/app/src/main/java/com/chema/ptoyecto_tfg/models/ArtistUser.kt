package com.chema.ptoyecto_tfg.models

import java.io.Serializable

data class ArtistUser(
    var userId: String?,
    var userName: String?,
    var email: String?,
    var phone: Int?,
    var img: String?,
    var rol: ArrayList<Rol>?,
    var idFavoritos: ArrayList<String>?,
    var cif: String?,
    var latitudUbicacion: Double?,
    var longitudUbicacion: Double?,
):Serializable
