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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.activities.ListResutlActivity
import com.chema.ptoyecto_tfg.activities.SearchResultMapsActivity
import com.chema.ptoyecto_tfg.activities.SelectStudioMapsActivity
import com.chema.ptoyecto_tfg.databinding.FragmentBasicUserSearchBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.models.Rol
import com.chema.ptoyecto_tfg.utils.Constantes
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
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
    private lateinit var rd_btn_list_mode: RadioButton
    private lateinit var rd_btn_map_mode: RadioButton
    private var resultModeInList: Boolean = true

    private var result: ArrayList<ArtistUser> = ArrayList()
    private var removeList: ArrayList<ArtistUser> = ArrayList()
    private var finalResult: ArrayList<ArtistUser> = ArrayList()


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
        rd_btn_list_mode = view.findViewById(R.id.rd_btn_list_mode)
        rd_btn_map_mode = view.findViewById(R.id.rd_btn_map_mode)

        getMyLocation()

        btn_search.setOnClickListener {
            applyFilters()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++

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
        }
        filterResult()
    }

    private fun fillListFiltered(dc: DocumentChange){

        val fav = ArtistUser(
            dc.document.get("userId").toString(),
            dc.document.get("userName").toString(),
            dc.document.get("email").toString(),
            dc.document.get("phone").toString().toInt(),
            dc.document.get("img").toString(),
            dc.document.get("rol") as ArrayList<Rol>?,
            dc.document.get("idFavoritos") as ArrayList<String>?,
            dc.document.get("prices") as ArrayList<String>?,
            dc.document.get("sizes") as ArrayList<String>?,
            dc.document.get("cif").toString(),
            dc.document.get("latitudUbicacion").toString().toDouble(),
            dc.document.get("longitudUbicacion").toString().toDouble()
        )
        result.add(fav)
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
            applyFilterSize(artistUser, price)
            return
        }

        if (price.isNotEmpty()) {
            applyFilterPrice(artistUser, price)
            return
        }
    }

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

}