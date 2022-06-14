package com.chema.ptoyecto_tfg.models

import java.io.Serializable

data class Chat (
    var idChat : String?,
    var idUserArtist : String?,
    var userNameArtist : String?,
    var idUserOther : String?,
    var userNameOther : String?,
    var date : String?
        ) : Serializable