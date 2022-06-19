package com.chema.ptoyecto_tfg.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chema.ptoyecto_tfg.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.chema.ptoyecto_tfg.databinding.ActivitySearchResultMapsBinding
import com.chema.ptoyecto_tfg.models.ArtistUser
import com.chema.ptoyecto_tfg.utils.VariablesCompartidas
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.Marker
import java.lang.Exception

class SearchResultMapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySearchResultMapsBinding
    private val LOCATION_REQUEST_CODE: Int = 0
    private var resultados : ArrayList<ArtistUser>? = ArrayList()
    private var resultadosIndex : ArrayList<Int> = ArrayList()
    private var resultadosSt : ArrayList<String> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation : LatLng? = null
    private var lat : String? = null
    private var lon : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchResultMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        resultados = args!!.getSerializable("USER_LIST") as ArrayList<ArtistUser>?
        lat = args.getString("userLat")!!
        lon = args.getString("userLon")!!
        if(lat != null && lon != null){
            userLocation = LatLng(lat.toString().toDouble(),lon.toString().toDouble())
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            if(userLocation != null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation!!,5f))
            }
            addMarkers()

            mMap.setOnMarkerClickListener(this)
            enableMyLocation()
        }catch (e : Exception){
            Log.e("CHE_EXCEPTRION",e.toString())
        }
    }

    private fun addMarkers(){
        if(resultados != null){
            for(us in resultados!!){
                val studio = LatLng(us.latitudUbicacion!!, us.longitudUbicacion!!)
                val name = us.userName
                mMap.addMarker(MarkerOptions().position(studio).title("${name.toString()}"))
                resultadosSt.add(name.toString())
                //resultadosIndex.add(resultados!!.indexOf(us))
            }
        }
    }
    override fun onMarkerClick(p0: Marker): Boolean {

        AlertDialog.Builder(this).setTitle("${p0.title.toString()}")
            .setPositiveButton(R.string.CONFIRM) { view, _ ->
                goToMuro(p0)
                //finish()
                view.dismiss()
            }.setNegativeButton(R.string.NO) { view, _ ->
                //elimina marcador
                //p0.remove()
                view.dismiss()
            }.create().show()
        return false
    }

    private fun goToMuro(p0: Marker){
        val index : Int = resultadosSt.indexOf(p0.title.toString())
        val artistUser : ArtistUser = resultados!![index]
        VariablesCompartidas.usuarioArtistaVisitaMuro = artistUser

        val intent = Intent(this, ArtistMuroConatinerActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    fun enableMyLocation() {
        if (!::mMap.isInitialized) return
        if (isPermissionsGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, R.string.accept_permissions, Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.modo_normal -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.modo_hybrid -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.modo_satellite -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
        return super.onOptionsItemSelected(item)
    }
}