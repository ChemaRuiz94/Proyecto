package com.chema.ptoyecto_tfg.activities.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.chema.ptoyecto_tfg.activities.ui.allUsers.AdapterAllArtistUsers
import com.chema.ptoyecto_tfg.databinding.FragmentHomeBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.BasicUser
import com.chema.ptoyecto_tfg.utils.Constantes
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AllBasicUserFragment : Fragment() {

    private lateinit var rv : RecyclerView
    private lateinit var myAdapterBasic : AdapterBasicUser
    private val db = FirebaseFirestore.getInstance()
    private var allBasicUser = ArrayList<BasicUser>()

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAllUsers()
        cargarRV(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cargarRV(view: View){
        rv = view.findViewById(R.id.rv_all_basic_user)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(requireActivity())
        myAdapterBasic = AdapterBasicUser(requireActivity() as AppCompatActivity,allBasicUser)
        rv.adapter = myAdapterBasic
        //scroll to bottom
    }

    fun getAllUsers(){
        db.collection("${Constantes.collectionUser}")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(context, R.string.ERROR, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                obtenerDatos(snapshots)
            }
    }

    private fun obtenerDatos(datos: QuerySnapshot?) {
        var pos = 0
        for(dc: DocumentChange in datos?.documentChanges!!){

            var ba= BasicUser(
                dc.document.get("userId").toString(),
                dc.document.get("userName").toString(),
                dc.document.get("email").toString(),
                dc.document.get("phone").toString().toInt(),
                dc.document.get("img").toString(),
                dc.document.get("rol").toString(),
                dc.document.get("idFavoritos") as ArrayList<String>?
            )
            if(allBasicUser.contains(ba)){
                allBasicUser.remove(ba)
            }
            when(dc.type) {

                DocumentChange.Type.ADDED -> myAdapterBasic.addUser(ba)
                DocumentChange.Type.MODIFIED -> myAdapterBasic.updateUser(ba, pos)
                DocumentChange.Type.REMOVED -> myAdapterBasic.removeUser(ba)
            }
            pos++
        }
    }

}