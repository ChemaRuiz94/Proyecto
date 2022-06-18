package com.chema.ptoyecto_tfg.navigation.basic.ui.BasicUserSearch

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.activities.ListResutlActivity
import com.chema.ptoyecto_tfg.activities.SearchResultMapsActivity
import com.chema.ptoyecto_tfg.activities.SelectStudioMapsActivity
import com.chema.ptoyecto_tfg.databinding.FragmentBasicUserSearchBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.Post
import com.chema.ptoyecto_tfg.models.Rol
import com.chema.ptoyecto_tfg.rv.AdapterRvEtiquetas
import com.chema.ptoyecto_tfg.rv.AdapterRvFavorites
import com.chema.ptoyecto_tfg.utils.Constantes
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BasicUserSearchFragment : Fragment() {

    private lateinit var basicUserSearchViewModel: BasicUserSearchViewModel
    private var _binding: FragmentBasicUserSearchBinding? = null

    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null
    private val db = FirebaseFirestore.getInstance()

    private lateinit var btn_search: Button
    private lateinit var ed_txt_search_by_name: EditText
    private lateinit var ed_txt_max_distance: EditText
    private lateinit var ed_txt_max_price: EditText
    private lateinit var ed_txt_max_size: EditText
    private lateinit var ed_txt_new_style: EditText
    private lateinit var txt_styles_search: EditText
    private lateinit var rd_btn_list_mode: RadioButton
    private lateinit var rd_btn_map_mode: RadioButton
    private lateinit var flt_btn_new_style: FloatingActionButton

    private var result: ArrayList<ArtistUser> = ArrayList()
    private var removeList: ArrayList<ArtistUser> = ArrayList()
    private var finalResult: ArrayList<ArtistUser> = ArrayList()
    private var etiquetas: ArrayList<String> = ArrayList()
    private var postsResult: ArrayList<Post> = ArrayList()


    private lateinit var rv : RecyclerView
    private lateinit var miAdapter: AdapterRvEtiquetas


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        basicUserSearchViewModel = ViewModelProvider(this).get(BasicUserSearchViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        _binding = FragmentBasicUserSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_search = view.findViewById(R.id.btn_search)
        ed_txt_search_by_name = view.findViewById(R.id.ed_txt_search_by_name)
        ed_txt_max_distance = view.findViewById(R.id.ed_txt_max_distance)
        ed_txt_max_price = view.findViewById(R.id.ed_txt_max_price)
        ed_txt_max_size = view.findViewById(R.id.ed_txt_max_size)
        ed_txt_new_style = view.findViewById(R.id.ed_txt_new_style)
        rd_btn_list_mode = view.findViewById(R.id.rd_btn_list_mode)
        rd_btn_map_mode = view.findViewById(R.id.rd_btn_map_mode)
        flt_btn_new_style = view.findViewById(R.id.flt_btn_new_style)

        cargarRV(view)

        getMyLocation()

        flt_btn_new_style.setOnClickListener{
            addTag()
        }
        btn_search.setOnClickListener {
            applyFilters()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++

    private fun cargarRV(view: View){

        rv = view.findViewById(R.id.rv_etiquetas)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(view.context)
        miAdapter = AdapterRvEtiquetas(context as AppCompatActivity, etiquetas)
        rv.adapter = miAdapter
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                userLocation = location
            }
    }

    /*
    Calcula la distancia entre dos puntos
     */
    private fun calculationByDistance(location: Location?, lat2: Double?, lon2: Double?): Double {
        val Radius = 6371 // radius of earth in Km
        val lat1 = location!!.latitude
        val lon1 = location.longitude
        val dLat = Math.toRadians(lat2!! - lat1)
        val dLon = Math.toRadians(lon2!! - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        return Radius * c
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++

    private suspend fun getDataFromDataBaseStyles(): QuerySnapshot {
        postsResult.clear()
        etiquetas = miAdapter.getTags()

        try {
            return db.collection("${Constantes.collectionPost}")
                .whereArrayContainsAny("etiquetas", etiquetas)
                .get()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun getDataFromDataBase(): QuerySnapshot {
        result.clear()
        removeList.clear()
        try {
            return db.collection("${Constantes.collectionArtistUser}")
                .get()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun applyFilters() {
        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                var data = getDataFromDataBase()
                for (dc: DocumentChange in data.documentChanges!!) {
                    fillListFiltered(dc)
                }
            }
            job.join()

            if(etiquetas.size > 0){
                val job2: Job = launch(context = Dispatchers.Default) {
                    var dataPost = getDataFromDataBaseStyles()
                    for (dc: DocumentChange in dataPost.documentChanges!!) {
                        fillFilteredStyle(dc)
                    }
                }
                job2.join()
            }

        }
        filterResult()
    }

    private fun fillListFiltered(dc: DocumentChange){

        val art = ArtistUser(
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
        //no mostraremos el usuario actual en la lista
        if(!art.userId.toString().equals(VariablesCompartidas.idUsuarioActual)){
            result.add(art)
        }
    }

    private fun showResults() {
        if (rd_btn_list_mode.isChecked) {
            val resultIntent = Intent(requireContext(), ListResutlActivity::class.java)
            val args = Bundle()
            args.putSerializable("USER_LIST", result)
            args.putString("userLat", userLocation!!.latitude.toString())
            args.putString("userLon", userLocation!!.longitude.toString())
            resultIntent.putExtra("BUNDLE", args)
            startActivity(resultIntent)
        } else {
            val resultIntent = Intent(requireContext(), SearchResultMapsActivity::class.java)
            val args = Bundle()
            args.putSerializable("USER_LIST", result)
            args.putString("userLat", userLocation!!.latitude.toString())
            args.putString("userLon", userLocation!!.longitude.toString())
            resultIntent.putExtra("BUNDLE", args)
            startActivity(resultIntent)
        }
    }

    private fun filterResult() {
        for (artist in result) {
            checkAllEmpty(artist)
        }
        for (artist in removeList) {
            result.remove(artist)
        }
        showResults()
    }

    private fun applyFilterName(artistUser: ArtistUser, textName: String) {
        val artistUserName = artistUser.userName!!.lowercase()
        if (!artistUserName.contains(textName) || !artistUserName.startsWith(textName)) {
            removeList.add(artistUser)
        }
    }

    private fun applyFilterDistance(artistUser: ArtistUser, textDistance: String) {
        val dist = calculationByDistance(
            userLocation,
            artistUser.latitudUbicacion!!,
            artistUser.longitudUbicacion!!
        )
        if (dist > textDistance.toDouble()) {
            removeList.add(artistUser)
        }
    }

    private fun applyFilterPrice(artistUser: ArtistUser, textPrice: String) {
        val prices = artistUser.prices!!
        for (price in prices) {
            if(price.toDouble() <= textPrice.toDouble()) {
                return
            }
        }
        removeList.add(artistUser)
    }

    private fun applyFilterSize(artistUser: ArtistUser, textSize: String) {
        val sizes = artistUser.sizes!!
        for (size in sizes) {
            if(size.toDouble() <= textSize.toDouble()) {
                return
            }
        }
        removeList.add(artistUser)
    }

    private fun applyFilterStyles(artistUser: ArtistUser){
        for(post in postsResult){
            if(post.userId.equals(artistUser.userId)){
                return
            }
        }
        removeList.add(artistUser)
    }


    private fun addTag() {
        val tag = ed_txt_new_style.text.toString().trim()

        if (tag.isNotEmpty()) {
            miAdapter.addTag(tag)
            ed_txt_new_style.setText("")
        }
    }

    private fun fillFilteredStyle(dc: DocumentChange) {
        val po = Post(
            dc.document.get("postId").toString(),
            dc.document.get("userId").toString(),
            dc.document.get("imgId").toString(),
            dc.document.get("etiquetas") as ArrayList<String>?
        )

        postsResult.add(po)
    }


    private fun checkAllEmpty(artistUser: ArtistUser) {
        val name = ed_txt_search_by_name.text.toString().trim().lowercase()
        val distance = ed_txt_max_distance.text.toString().trim()
        val price = ed_txt_max_price.text.toString().trim()
        val size = ed_txt_max_size.text.toString().trim()

        if (name.isNotEmpty()) {
            applyFilterName(artistUser, name)
            return
        }

        if (distance.isNotEmpty()) {
            applyFilterDistance(artistUser, distance)
            return
        }

        if (size.isNotEmpty()) {
            applyFilterSize(artistUser, size)
            return
        }

        if (price.isNotEmpty()) {
            applyFilterPrice(artistUser, price)
            return
        }

        if(miAdapter.itemCount > 0){
            applyFilterStyles(artistUser)
            return
        }
    }


//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

}