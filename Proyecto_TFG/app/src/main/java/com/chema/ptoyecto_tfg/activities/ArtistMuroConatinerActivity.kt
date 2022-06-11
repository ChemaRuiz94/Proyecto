package com.chema.ptoyecto_tfg.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.navigation.basic.ui.muro.MuroFragment
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas

class ArtistMuroConatinerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_muro_conatiner)
        VariablesCompartidas.userArtistVisitMode = true
        goToArtistMuro()
    }

    private fun goToArtistMuro(){
        val nextFrag = MuroFragment()
        this.getSupportFragmentManager().beginTransaction()
            .replace(R.id.activity_artist_muro_container, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit()
    }


    override fun onDestroy() {
        super.onDestroy()
        VariablesCompartidas.userArtistVisitMode = false
        VariablesCompartidas.usuarioArtistaVisitaMuro = null
        VariablesCompartidas.idUserArtistVisitMode = null
    }
}