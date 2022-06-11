package com.chema.ptoyecto_tfg.navigation.basic.ui.BasicUserSearch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.activities.ListResutlActivity
import com.chema.ptoyecto_tfg.activities.SelectStudioMapsActivity
import com.chema.ptoyecto_tfg.databinding.FragmentBasicUserSearchBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.Rol
import com.chema.ptoyecto_tfg.utils.Constantes
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList




class BasicUserSearchFragment : Fragment() {

    private lateinit var basicUserSearchViewModel: BasicUserSearchViewModel
    private var _binding : FragmentBasicUserSearchBinding? = null

    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private lateinit var btn_search : Button
    private lateinit var ed_txt_search_by_name : EditText

    private var result : ArrayList<ArtistUser> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        basicUserSearchViewModel = ViewModelProvider(this).get(BasicUserSearchViewModel::class.java)


        _binding = FragmentBasicUserSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_search = view.findViewById(R.id.btn_search)
        ed_txt_search_by_name = view.findViewById(R.id.ed_txt_search_by_name)

        btn_search.setOnClickListener{
            busqueda()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun busqueda(){
        runBlocking {
            val job : Job = launch(context = Dispatchers.Default) {
                var datos : QuerySnapshot = getDataFromFireStore() as QuerySnapshot //Obtenermos la colección
                if(ed_txt_search_by_name.text.isNotEmpty()){
                    datos = getDataFromFireStoreByName() as QuerySnapshot //Obtenermos la colección
                }

                obtenerDatos(datos as QuerySnapshot?)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }
        val resultIntent = Intent(requireContext(), ListResutlActivity::class.java)
        val args = Bundle()
        args.putSerializable("USER_LIST", result )
        resultIntent.putExtra("BUNDLE", args)
        startActivity(resultIntent)
    }

    suspend fun getDataFromFireStoreByName()  : QuerySnapshot? {
        return try{
            val data = db.collection("${Constantes.collectionArtistUser}")
                .whereEqualTo("userName","${ed_txt_search_by_name.text.toString()}")
                .get()
                .await()
            data
        }catch (e : Exception){
            null
        }
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
        result.clear()
        for(dc: DocumentChange in datos?.documentChanges!!){
            if (dc.type == DocumentChange.Type.ADDED){

                var fav= ArtistUser(
                    dc.document.get("userId").toString(),
                    dc.document.get("userName").toString(),
                    dc.document.get("email").toString(),
                    dc.document.get("phone").toString().toInt(),
                    dc.document.get("img").toString(),
                    dc.document.get("rol") as ArrayList<Rol>?,
                    dc.document.get("idFavoritos") as ArrayList<String>?,
                    dc.document.get("cif").toString(),
                    dc.document.get("latitudUbicacion").toString().toDouble(),
                    dc.document.get("longitudUbicacion").toString().toDouble()
                )
                result.add(fav)
            }
        }

    }
}