package com.chema.ptoyecto_tfg.navigation.artist.ui.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.databinding.FragmentFavoritesBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.rv.AdapterRvFavorites
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

import android.R.attr
import com.chema.ptoyecto_tfg.navigation.artist.ui.muro.MuroFragment


class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private var userBasicAct: BasicUser? = null
    private var userArtistAct : ArtistUser? = null
    private var isBasicUser : Boolean = true
    var favorites : ArrayList<ArtistUser> = ArrayList<ArtistUser>()
    private lateinit var rv : RecyclerView
    private lateinit var miAdapter: AdapterRvFavorites

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(VariablesCompartidas.usuarioArtistaActual != null){
            userArtistAct = VariablesCompartidas.usuarioArtistaActual as ArtistUser
            isBasicUser = false
        }
        if(VariablesCompartidas.usuarioBasicoActual != null){
            userBasicAct = VariablesCompartidas.usuarioBasicoActual as BasicUser
            isBasicUser = true
        }

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = view.findViewById(R.id.rv_fav)
        if(VariablesCompartidas.usuarioArtistaActual != null){
            userArtistAct = VariablesCompartidas.usuarioArtistaActual as ArtistUser
            isBasicUser = false
        }
        if(VariablesCompartidas.usuarioBasicoActual != null){
            userBasicAct = VariablesCompartidas.usuarioBasicoActual as BasicUser
            isBasicUser = true
        }


        runBlocking {
            val job : Job = launch(context = Dispatchers.Default) {
                val datos : QuerySnapshot = getDataFromFireStore() as QuerySnapshot
                obtenerDatos(datos as QuerySnapshot?)
            }
            job.join()
        }

        cargarRV(view)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun cargarRV(view: View){

        rv = view.findViewById(R.id.rv_fav)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(view.context)
        miAdapter = AdapterRvFavorites(view.context as AppCompatActivity, favorites)
        rv.adapter = miAdapter
    }

    suspend fun getDataFromFireStore()  : QuerySnapshot? {
        return try{
            val data = db.collection("${Constantes.collectionArtistUser}")
                .get()
                .await()
            data
        }catch (e : Exception){
            null
        }
    }

    private fun obtenerDatos(datos: QuerySnapshot?) {
        favorites.clear()

        var listIdFavoritesUser : ArrayList<String> = ArrayList()

        if(isBasicUser){
            for(idFav in userBasicAct!!.idFavoritos!!){
                listIdFavoritesUser.add(idFav)
            }
        }else{
            for(idFav in userArtistAct!!.idFavoritos!!){
                listIdFavoritesUser.add(idFav)
            }

        }

        for(dc: DocumentChange in datos?.documentChanges!!){
            if (dc.type == DocumentChange.Type.ADDED){

                var fav= ArtistUser(
                    dc.document.get("userId").toString(),
                    dc.document.get("userName").toString(),
                    dc.document.get("email").toString(),
                    dc.document.get("phone").toString().toInt(),
                    dc.document.get("img").toString(),
                    dc.document.get("rol").toString(),
                    dc.document.get("idFavoritos") as ArrayList<String>?,
                    dc.document.get("prices") as ArrayList<String>?,
                    dc.document.get("sizes") as ArrayList<String>?,
                    dc.document.get("cif").toString(),
                    dc.document.get("latitudUbicacion").toString().toDouble(),
                    dc.document.get("longitudUbicacion").toString().toDouble()
                )

                if(listIdFavoritesUser.contains(fav.userId.toString().trimEnd())){
                    favorites.add(fav)
                }
            }
        }

    }
}