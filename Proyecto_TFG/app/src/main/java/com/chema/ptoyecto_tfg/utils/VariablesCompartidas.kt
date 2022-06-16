package com.chema.ptoyecto_tfg.utils

import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.BasicUser
import com.google.android.gms.maps.model.LatLng

object VariablesCompartidas {

    var emailUsuarioActual: String? = null
    var idUsuarioActual: String? = null
    var usuarioBasicoActual: BasicUser? = null
    var usuarioArtistaActual: ArtistUser? = null
    var usuarioArtistaVisitaMuro: ArtistUser? = null

    var userArtistVisitMode : Boolean = false
    var userArtistOwner : Boolean = false
    var idUserArtistVisitMode : String? = null

    var marcadorActual : LatLng = LatLng(40.416, -3.703)
    var latitudStudioSeleccionado : String? = null
    var longitudStudioSeleccionado: String? = null
}