package com.chema.ptoyecto_tfg.activities.ui.allUsers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.databinding.FragmentDashboardBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.utils.Constantes
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AllArtistUsersFragment : Fragment() {

    private lateinit var rv : RecyclerView
    private lateinit var miAdapterArtist: AdapterAllArtistUsers
    private val db = FirebaseFirestore.getInstance()
    private var allArtistUser = ArrayList<ArtistUser>()


    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
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
        rv = view.findViewById(R.id.rv_all_users)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(requireActivity())
        miAdapterArtist = AdapterAllArtistUsers(requireActivity() as AppCompatActivity,allArtistUser)
        rv.adapter = miAdapterArtist
        //scroll to bottom
    }

    fun getAllUsers(){
        db.collection("${Constantes.collectionArtistUser}")
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

            var ba= ArtistUser(
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
                dc.document.get("longitudUbicacion").toString().toDouble(),
            )

            when(dc.type) {

                DocumentChange.Type.ADDED -> miAdapterArtist.addUser(ba)
                DocumentChange.Type.MODIFIED -> miAdapterArtist.updateUser(ba, pos)
                DocumentChange.Type.REMOVED -> miAdapterArtist.removeUser(ba)
            }
            pos++
        }
    }
}