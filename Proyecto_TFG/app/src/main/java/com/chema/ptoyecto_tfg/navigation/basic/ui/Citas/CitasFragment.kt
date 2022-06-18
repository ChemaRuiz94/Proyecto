package com.chema.ptoyecto_tfg.navigation.basic.ui.Citas

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.databinding.FragmentCitasBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.models.Chat
import com.chema.ptoyecto_tfg.models.Rol
import com.chema.ptoyecto_tfg.rv.AdapterRvCitas
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.gms.location.LocationServices
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

class CitasFragment : Fragment() {

    private lateinit var citasViewModel: CitasViewModel
    private var _binding : FragmentCitasBinding? = null

    private val binding get() = _binding!!
    private var view2: View? = null

    private val db = Firebase.firestore
    private var id : String? = null
    private var userBasicAct: BasicUser? = null
    private var userArtistAct : ArtistUser? = null
    private var isBasicUser : Boolean = true
    private var allChat : ArrayList<Chat> = ArrayList()
    private lateinit var miAdapter: AdapterRvCitas
    private lateinit var rv : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        citasViewModel = ViewModelProvider(this).get(CitasViewModel::class.java)

        _binding = FragmentCitasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view2 = view
        rv = view.findViewById(R.id.rv_citas)
        id  = ""
        if(VariablesCompartidas.usuarioArtistaActual != null){
            userArtistAct = VariablesCompartidas.usuarioArtistaActual as ArtistUser
            isBasicUser = false
            id = userArtistAct!!.userId.toString()
        }
        if(VariablesCompartidas.usuarioBasicoActual != null){
            userBasicAct = VariablesCompartidas.usuarioBasicoActual as BasicUser
            isBasicUser = true
            id = userBasicAct!!.userId.toString()
        }
        cargarRV(view)
        if(isBasicUser){
            getDataFromFireStore("idUserOther",id)
        }else{
            getDataFromFireStore("idUserOther",id)
            getDataFromFireStore("idUserArtist",id)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++
    private fun cargarRV(view: View){

        rv = view.findViewById(R.id.rv_citas)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(view.context)
        miAdapter = AdapterRvCitas(view.context as AppCompatActivity, allChat)
        rv.adapter = miAdapter
    }

    fun getDataFromFireStore(field : String ,id : String?) {
        try{
            val data = db.collection("${Constantes.collectionChat}")
                .whereEqualTo("${field.toString()}", id)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Toast.makeText(context, R.string.ERROR, Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }
                    obtenerDatos(snapshots)
                }
        }catch (e : Exception){
            throw e
        }
    }

    private fun obtenerDatos(datos: QuerySnapshot?) {
        for(dc: DocumentChange in datos?.documentChanges!!){
            var chat= Chat(
                dc.document.get("idChat").toString(),
                dc.document.get("idUserArtist").toString(),
                dc.document.get("userNameArtist").toString(),
                dc.document.get("idUserOther").toString(),
                dc.document.get("userNameOther").toString(),
                dc.document.get("date").toString()
            )
            if (dc.type == DocumentChange.Type.ADDED){
                miAdapter.addChat(chat)
            }
            if (dc.type == DocumentChange.Type.REMOVED) {
                miAdapter.removeChat(chat)
            }
        }
    }
}