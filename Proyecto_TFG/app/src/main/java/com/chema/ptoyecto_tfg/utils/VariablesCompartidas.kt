package com.chema.ptoyecto_tfg.utils

import com.chema.ptoyecto_tfg.models.BasicUser
import com.google.android.gms.maps.model.LatLng

object VariablesCompartidas {

    var emailUsuarioActual: String? = null
    var usuarioBasicoActual: BasicUser? = null


    var marcadorActual : LatLng = LatLng(40.416, -3.703)
    var latitudStudioSeleccionado : String? = null
    var longitudStudioSeleccionado: String? = null
}