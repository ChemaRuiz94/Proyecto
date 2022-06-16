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

class BasicUserSearchFragment : Fragment() {

    private lateinit var basicUserSearchViewModel: BasicUserSearchViewModel
    private var _binding : FragmentBasicUserSearchBinding? = null

    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation : Location? = null
    private val db = FirebaseFirestore.getInstance()

    private lateinit var btn_search : Button
    private lateinit var ed_txt_search_by_name : EditText
    private lateinit var ed_txt_max_distance : EditText
    private lateinit var ed_txt_max_price : EditText
    private lateinit var ed_txt_max_size : EditText
    private lateinit var rd_btn_list_mode : RadioButton
    private lateinit var rd_btn_map_mode : RadioButton
    private var resultModeInList : Boolean = true

    private var result : ArrayList<ArtistUser> = ArrayList()
    private var auxResult : ArrayList<ArtistUser> = ArrayList()
    private var finalResult : ArrayList<ArtistUser> = ArrayList()

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

        btn_search.setOnClickListener{
            busqueda()
        }

        rd_btn_list_mode.setOnClickListener{

        }
        rd_btn_map_mode.setOnClickListener{

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
            .addOnSuccessListener { location : Location? ->
                userLocation = location
            }
    }


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
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec)
        return Radius * c
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++

    private fun busqueda(){
        result.clear()
        auxResult.clear()
        finalResult.clear()
        runBlocking {
            val job : Job = launch(context = Dispatchers.Default) {
                var datos : QuerySnapshot = getDataFromFireStore() as QuerySnapshot
                obtenerDatos(datos as QuerySnapshot?)
            }
            job.join()
        }
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        //PRIMER FILTRO EL NOMBRE
        if(ed_txt_search_by_name.text.toString().trim().isNotEmpty()){
            val nombre = ed_txt_search_by_name.text.toString()
            for(artist in result){
                if(artist.userName!!.startsWith(nombre) || artist.userName!!.contains(nombre) || artist.userName.equals(nombre)){
                    finalResult.add(artist)
                }
            }
        }
        if(ed_txt_max_distance.text.toString().trim().isNotEmpty()){
            aplicarFiltroDistancia()
        }
        if(ed_txt_max_price.text.toString().trim().isNotEmpty()){
            aplicarFiltroPrecio()
        }

        if(ed_txt_max_size.text.toString().trim().isNotEmpty()){
            aplicarFiltroSize()
        }

        if(checkAllEmpty()){
            val resultIntent = Intent(requireContext(), ListResutlActivity::class.java)
            val args = Bundle()
            args.putSerializable("USER_LIST", result)
            resultIntent.putExtra("BUNDLE", args)
            startActivity(resultIntent)
        }else{
            val resultIntent = Intent(requireContext(), ListResutlActivity::class.java)
            val args = Bundle()
            args.putSerializable("USER_LIST", finalResult)
            resultIntent.putExtra("BUNDLE", args)
            startActivity(resultIntent)
        }

    }

    private fun aplicarFiltroDistancia(){
        auxResult.addAll(finalResult)
        //auxResult = (finalResult)
        finalResult.clear()

        for(artist in result){
            //var latLonArtist : LatLng = LatLng(artist.latitudUbicacion!!,artist.longitudUbicacion!!)
            var dist = calculationByDistance(userLocation,artist.latitudUbicacion!!,artist.longitudUbicacion!!)
            if(dist < ed_txt_max_distance.text.toString().toDouble()){
                if(!finalResult.contains(artist)){
                    auxResult.add(artist)
                    //finalResult.add(artist)
                }
            }
        }
        finalResult.addAll(auxResult)
    }

    private fun aplicarFiltroPrecio(){
        auxResult.clear()

        for(artist in result){
            val maxPrice = ed_txt_max_price.text.toString().trim()
                for(artistPrice in artist.prices!!){
                    if(artistPrice != null ){
                        if(artistPrice.toString().toDouble() <= maxPrice.toString().toDouble()){
                            if(!finalResult.contains(artist)){
                                auxResult.add(artist)
                                break
                            }
                        }
                    }
                }
        }
        finalResult.addAll(auxResult)
    }

    private fun aplicarFiltroSize(){
        auxResult.clear()

        for(artist in result){
            val maxSize = ed_txt_max_size.text.toString().trim()
            for(artistSize in artist.sizes!!){
                if(artistSize != null ){
                    if(artistSize.toString().toDouble() <= maxSize.toString().toDouble()){
                        if(!finalResult.contains(artist)){
                            auxResult.add(artist)
                            break
                        }
                    }
                }
            }
        }
        finalResult.addAll(auxResult)
    }

    private fun checkAllEmpty() : Boolean{
        var isAllEmpty = true
        if(ed_txt_search_by_name.text.toString().trim().isNotEmpty()){
            isAllEmpty = false
        }

        if(ed_txt_max_distance.text.toString().trim().isNotEmpty()){
            isAllEmpty = false
        }

        if(ed_txt_max_price.text.toString().trim().isNotEmpty()){
            isAllEmpty = false
        }

        if(ed_txt_max_size.text.toString().trim().isNotEmpty()){
            isAllEmpty = false
        }
        return isAllEmpty
    }

    private fun changeResultMode(){

    }
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


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
                    dc.document.get("prices") as ArrayList<String>?,
                    dc.document.get("sizes") as ArrayList<String>?,
                    dc.document.get("cif").toString(),
                    dc.document.get("latitudUbicacion").toString().toDouble(),
                    dc.document.get("longitudUbicacion").toString().toDouble()
                )
                result.add(fav)
            }
        }

    }
}