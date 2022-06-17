package com.chema.ptoyecto_tfg.models

import java.io.Serializable
import java.util.*

data class Comentario(
    var idComentario: String?,
    var idChat : String?,
    var idUser : String?,
    var userNameAutor : String?,
    var comentario: String?,
    var horaComentario: Int = Calendar.HOUR,
    var minComentario: Int = Calendar.MINUTE,
    var segComentario: Int = Calendar.SECOND,
    var diaComentario: Int = Calendar.DAY_OF_MONTH,
    var mesComentario: Int = Calendar.MONTH,
    var yearComentario: Int = Calendar.YEAR,
) : Serializable